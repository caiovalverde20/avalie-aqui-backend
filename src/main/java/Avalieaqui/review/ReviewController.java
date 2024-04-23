package Avalieaqui.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

        try {
            stars = Integer.parseInt(reviewData.get("stars").toString());
            comment = reviewData.get("comment").toString();
        } catch (NumberFormatException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Formato de estrelas inválido.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Review review = reviewService.addReview(token, productId, stars, comment);

        if (review == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token inválido ou user já tem uma review nesse produto.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("review", review);

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
    public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable String productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reviews);
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
