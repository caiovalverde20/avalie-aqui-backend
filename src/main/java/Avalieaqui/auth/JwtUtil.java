package Avalieaqui.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import Avalieaqui.user.User;
import Avalieaqui.user.UserRepository;

import java.util.Date;

@Component
public class JwtUtil {

    @Autowired
    private UserRepository userRepository;

    private String secret = "MySecretKey";
    private long expiration = 7 * 30_436_875L * 24 * 60 * 60 * 1000; // 7 meses

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token, User user) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            String username = claims.getSubject();
            User userFromDb = userRepository.findByEmail(username);

            if (claims.getExpiration().before(new Date())) {
                return false;
            }

            return userFromDb != null && userFromDb.getToken().equals(token);
        } catch (ExpiredJwtException | SignatureException e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        String email = getUsernameFromToken(token);
        User user = userRepository.findByEmail(email);
        return user != null ? user.getId() : null;
    }

}
