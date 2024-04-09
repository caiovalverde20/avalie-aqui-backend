package Avalieaqui.review;

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

    public boolean removeReview(String token, String productId) {
        String email = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email);

        if (user == null || !jwtUtil.validateToken(token, user)) {
            return false;
        }

        Review existingReview = reviewRepository.findByUserIdAndProductId(user.getId(), productId);
        if (existingReview != null) {
            reviewRepository.delete(existingReview);
            return true;
        } else {
            return false; // Review does not exist
        }
    }
}
