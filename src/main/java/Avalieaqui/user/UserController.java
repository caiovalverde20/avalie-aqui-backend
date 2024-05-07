package Avalieaqui.user;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.dao.DuplicateKeyException;

import Avalieaqui.auth.JwtUtil;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody User user) {
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = userRepository.save(user);

        return new UserDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getAdm());
    }

    @GetMapping
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();

        for (User user : users) {
            userDtos.add(new UserDto(user.getId(), user.getName(), user.getEmail(), user.getAdm()));
        }

        return userDtos;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Usuário não encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail(), user.getAdm());
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editUser(@RequestBody UserDto userDto,
            @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Header incorreto.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        String token = authorizationHeader.substring(7);
        UserDto updatedUserDto = userService.editUser(token, userDto);

        if (updatedUserDto == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token inválido ou usuário não encontrado.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("user", updatedUserDto);

        return ResponseEntity.ok(successResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginInfo) {
        String userIdentifier = loginInfo.get("user");
        String password = loginInfo.get("password");
    
        User user = userIdentifier.contains("@") ? 
                    userRepository.findByEmail(userIdentifier) : 
                    userRepository.findByPhone(userIdentifier);
    
        if (user != null && bCryptPasswordEncoder.matches(password, user.getPassword())) {
            UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail(), user.getAdm());
            String token = jwtUtil.generateToken(user.getEmail()); // Considerar gerar token com telefone também se necessário
            user.setToken(token);
            userRepository.save(user);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userDto);
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Credenciais inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    

    @PostMapping("/login-google")
    public ResponseEntity<?> postMethodName(@RequestBody String tokenGoogle)
            throws GeneralSecurityException, IOException {

        User loginUser = userService.getUserFromGoogle(tokenGoogle);
        try {
            User user = userRepository.findByEmail(loginUser.getEmail());
            UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail(), user.getAdm());

            String token = jwtUtil.generateToken(user.getEmail());
            user.setToken(token);
            userRepository.save(user);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userDto);
            return ResponseEntity.ok(response);
        } catch (NullPointerException e) {
            UserDto userDto = new UserDto(loginUser.getId(), loginUser.getName(), loginUser.getEmail(),
                    loginUser.getAdm());
            String token = jwtUtil.generateToken(loginUser.getEmail());
            loginUser.setToken(token);
            userRepository.save(loginUser);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userDto);
            return ResponseEntity.ok(response);
        }
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<?> handleDuplicateKeyException(DuplicateKeyException e) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Um usuário com esse email já existe.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
