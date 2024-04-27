package Avalieaqui.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Avalieaqui.user.UserService;
import Avalieaqui.auth.JwtUtil;
import Avalieaqui.user.User;
import Avalieaqui.user.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @PostMapping("/{productId}")
    public ResponseEntity<?> addReview(@PathVariable String productId, @RequestBody Map<String, Object> reviewData,
            @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Header de autorização incorreto."));
        }

        String token = authorizationHeader.substring(7);
        String email = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email);

        if (user == null || !jwtUtil.validateToken(token, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("token error", "Token inválido"));
        }

        int stars;
        String comment;
        String title;

        try {
            stars = Integer.parseInt(reviewData.get("stars").toString());
            comment = reviewData.get("comment").toString();
            title = reviewData.get("title").toString();
        } catch (NumberFormatException | NullPointerException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Dados de review inválidos ou incompletos."));
        }

        Review existingReview = reviewRepository.findByUserIdAndProductId(user.getId(), productId);
        if (existingReview != null) {
            existingReview.setStars(stars);
            existingReview.setComment(comment);
            existingReview.setTitle(title);
            reviewRepository.save(existingReview);
            return ResponseEntity.ok(Map.of("message", "Review atualizada com sucesso!", "review", existingReview));
        } else {
            Review newReview = new Review(user.getId(), productId, stars, comment, title);
            reviewRepository.save(newReview);
            return ResponseEntity.ok(Map.of("message", "Review adicionada com sucesso!", "review", newReview));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeReview(@PathVariable String productId,
            @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Header incorreto.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        String token = authorizationHeader.substring(7);

        // Verificar se o usuário é um administrador usando o UserService
        if (!userService.isAdmin(token)) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Somente administradores podem excluir revisões.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        boolean removed = reviewService.removeReview(token, productId);

        if (!removed) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Avaliação não encontrada para este usuário e produto.");
            return ResponseEntity.notFound().build();
        }

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "Avaliação removida com sucesso.");
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable String userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getReviewsByProduct(@PathVariable String productId) {
        List<ReviewDto> reviewDTOs = reviewService.getReviewsWithUserDetailsByProduct(productId);
        if (reviewDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reviewDTOs);
    }

    @PostMapping("/like/{reviewId}")
    public ResponseEntity<?> toggleLike(@PathVariable String reviewId,
            @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Header de autorização incorreto."));
        }

        String token = authorizationHeader.substring(7);
        String email = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email);

        if (user == null || !jwtUtil.validateToken(token, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("token error", "Token inválido"));
        }

        Review updatedReview = reviewService.toggleLike(token, reviewId);
        if (updatedReview == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token ou review invalida");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return ResponseEntity.ok(updatedReview);
    }

    @PostMapping("/dislike/{reviewId}")
    public ResponseEntity<?> toggleDislike(@PathVariable String reviewId,
            @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Header de autorização incorreto."));
        }

        String token = authorizationHeader.substring(7);
        String email = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email);

        if (user == null || !jwtUtil.validateToken(token, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("token error", "Token inválido"));
        }

        Review updatedReview = reviewService.toggleDislike(token, reviewId);
        if (updatedReview == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token ou review invalida");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return ResponseEntity.ok(updatedReview);
    }
}
