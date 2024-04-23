package Avalieaqui.review;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import Avalieaqui.auth.JwtUtil;
import Avalieaqui.user.User;
import Avalieaqui.user.UserRepository;

@Service
public class ReviewService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public Review addReview(String token, String productId, int stars, String comment) {
        String email = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email);

        if (user == null || !jwtUtil.validateToken(token, user)) {
            return null;
        }

        Review existingReview = reviewRepository.findByUserIdAndProductId(user.getId(), productId);
        if (existingReview != null) {
            existingReview.setStars(stars);
            existingReview.setComment(comment);
            return null;
        } else {
            Review review = new Review(user.getId(), productId, stars, comment);
            return reviewRepository.save(review);
        }
    }

    public Review toggleLike(String token, String reviewId) {
        String userId = jwtUtil.getUserIdFromToken(token);
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            if (review.getLikes().contains(userId)) {
                review.getLikes().remove(userId);
            } else {
                review.getLikes().add(userId);
                review.getDislikes().remove(userId);
            }
            return reviewRepository.save(review);
        }
        return null;
    }

    public Review toggleDislike(String token, String reviewId) {
        String userId = jwtUtil.getUserIdFromToken(token);
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            if (review.getDislikes().contains(userId)) {
                review.getDislikes().remove(userId);
            } else {
                review.getDislikes().add(userId);
                review.getLikes().remove(userId);
            }
            return reviewRepository.save(review);
        }
        return null;
    }
}
