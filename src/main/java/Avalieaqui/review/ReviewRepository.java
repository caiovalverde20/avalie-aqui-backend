package Avalieaqui.review;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<Review, String> {
    Review findByUserIdAndProductId(String userId, String productId);

    List<Review> findByUserId(String userId);

    List<Review> findByProductId(String productId);
}
