package Avalieaqui.user;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.apache.v2.*;	

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class UserService {

    @Autowired
    private GsonFactory gsonFactory = new GsonFactory();

    //! NÃO ALTEREM ESSA FUNÇÃO
    public User getUserFromGoogle(String token) throws GeneralSecurityException, IOException {

        Dotenv dotenv = Dotenv.load();

        ApacheHttpTransport transport = new ApacheHttpTransport();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, gsonFactory)
        // Specify the CLIENT_ID of the app that accesses the backend:
        .setAudience(Collections.singletonList(System.getenv("GOOGLE_CLIENT_ID")))
        // Or, if multiple clients access the backend:
        //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
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
    
}
