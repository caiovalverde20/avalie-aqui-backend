package Avalieaqui.user;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.gson.GsonFactory;

import Avalieaqui.StorageService;
import Avalieaqui.auth.JwtUtil;

import com.google.api.client.http.apache.v2.*;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StorageService storageService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GsonFactory gsonFactory = new GsonFactory();

    // ! NÃO ALTEREM ESSA FUNÇÃO
    public User getUserFromGoogle(String token) throws GeneralSecurityException, IOException {

        ApacheHttpTransport transport = new ApacheHttpTransport();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, gsonFactory)
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(System.getenv("GOOGLE_CLIENT_ID")))
                // Or, if multiple clients access the backend:
                // .setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        // (Receive idTokenString by HTTPS POST)

        GoogleIdToken idToken = verifier.verify(token);
        if (idToken != null) {
            Payload payload = idToken.getPayload();

            String userId = payload.getSubject();

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            User user = new User(givenName + " " + familyName, email, null);

            return user;

        } else {
            System.out.println("Invalid ID token.");
        }

        return null;
    }

    public User findUserByToken(String token) {
        String email = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email);

        if (user == null || !jwtUtil.validateToken(token, user)) {
            System.out.println("Usuário não encontrado.");
            return null;
        }
        return user;
    }

    public UserDto updateProfileImage(String token, MultipartFile profileImage) throws IOException {
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return null;
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return null;
        }

        User user = userOptional.get();
        String imageUrl = storageService.uploadFile(profileImage);
        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);

        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getAdm(),
                user.getCpf(), user.getCity(), user.getState(), user.getGender(), user.getBirth(), user.getPhone(),
                user.getProfileImageUrl());
    }

    public UserDto editUser(String token, UserUpdateDto userUpdateDto) {
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return null;
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return null;
        }

        User user = userOptional.get();

        // Verificar se a senha antiga corresponde
        if (userUpdateDto.getOldPassword() != null
                && !passwordEncoder.matches(userUpdateDto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Senha antiga incorreta.");
        }

        if (userUpdateDto.getName() != null)
            user.setName(userUpdateDto.getName());
        if (userUpdateDto.getEmail() != null)
            user.setEmail(userUpdateDto.getEmail());
        if (userUpdateDto.getCpf() != null)
            user.setCpf(userUpdateDto.getCpf());
        if (userUpdateDto.getCity() != null)
            user.setCity(userUpdateDto.getCity());
        if (userUpdateDto.getState() != null)
            user.setState(userUpdateDto.getState());
        if (userUpdateDto.getGender() != null)
            user.setGender(userUpdateDto.getGender());
        if (userUpdateDto.getBirth() != null)
            user.setBirth(userUpdateDto.getBirth());
        if (userUpdateDto.getPhone() != null)
            user.setPhone(userUpdateDto.getPhone());

        if (userUpdateDto.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(userUpdateDto.getPassword());
            user.setPassword(encodedPassword);
        }

        userRepository.save(user);

        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getAdm(),
                user.getCpf(), user.getCity(), user.getState(), user.getGender(), user.getBirth(), user.getPhone());
    }

    public boolean isAdmin(String token) {
        String email = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email);

        if (user == null || !jwtUtil.validateToken(token, user)) {
            return false;
        }
        return user.getAdm();
    }

}
