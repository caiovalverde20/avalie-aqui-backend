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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

        return new UserDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
    }

    @GetMapping
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();

        for (User user : users) {
            userDtos.add(new UserDto(user.getId(), user.getName(), user.getEmail()));
        }

        return userDtos;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginUser) {
        User user = userRepository.findByEmail(loginUser.getEmail());
        if (user != null && bCryptPasswordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail());
            user.setToken(token);
            userRepository.save(user);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);        
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Credenciais inválidas");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/login-google")
    public ResponseEntity<?> postMethodName(@RequestBody String tokenGoogle) throws GeneralSecurityException, IOException {
        
        User loginUser = userService.getUserFromGoogle(tokenGoogle);
        User user = userRepository.findByEmail(loginUser.getEmail());

        if (user != null) {
            String token = jwtUtil.generateToken(user.getEmail());
            user.setToken(token);
            userRepository.save(user);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);    
        } else {
            String token = jwtUtil.generateToken(loginUser.getEmail());
            loginUser.setToken(token);
            userRepository.save(loginUser);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
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
