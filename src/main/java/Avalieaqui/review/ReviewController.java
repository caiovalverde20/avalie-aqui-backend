package Avalieaqui.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Avalieaqui.user.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @PostMapping("/{productId}")
    public ResponseEntity<?> addReview(@PathVariable String productId, @RequestBody Map<String, Object> reviewData,
            @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Header incorreto.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        String token = authorizationHeader.substring(7);
        int stars;
        String comment;
        String title;

        try {
            stars = Integer.parseInt(reviewData.get("stars").toString());
            comment = reviewData.get("comment").toString();
            title = reviewData.get("title").toString();
        } catch (NumberFormatException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Formato de estrelas inválido.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Review review = reviewService.addReview(token, productId, stars, comment, title);

        if (review == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token inválido ou user já tem uma review nesse produto.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("review", review);

        return ResponseEntity.ok(successResponse);
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
        String token = authorizationHeader.substring(7);
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
        String token = authorizationHeader.substring(7);
        Review updatedReview = reviewService.toggleDislike(token, reviewId);
        if (updatedReview == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token ou review invalida");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return ResponseEntity.ok(updatedReview);
    }
}
