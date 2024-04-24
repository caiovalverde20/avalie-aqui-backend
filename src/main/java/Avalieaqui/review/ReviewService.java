package Avalieaqui.review;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import Avalieaqui.auth.JwtUtil;
import Avalieaqui.user.User;
import Avalieaqui.user.UserRepository;
import Avalieaqui.user.UserService;

@Service
public class ReviewService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewRepository reviewRepository;

    public Review addReview(String token, String productId, int stars, String comment, String title) {
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
            Review review = new Review(user.getId(), productId, stars, comment, title);
            return reviewRepository.save(review);
        }
    }

    public boolean removeReview(String token, String productId) {
        User user = userService.findUserByToken(token);

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

    public List<ReviewDto> getReviewsWithUserDetailsByProduct(String productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream().map(review -> {
            User user = userRepository.findById(review.getUserId()).orElse(null);
            String userName = user != null ? user.getName() : "Unknown";
            return new ReviewDto(review.getId(), review.getUserId(), userName, review.getProductId(), review.getStars(),
                    review.getComment(), review.getCreatedAt());
        }).collect(Collectors.toList());
    }
}
