package Avalieaqui.review;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<Review, String> {
    Review findByUserIdAndProductId(String userId, String productId);
}
