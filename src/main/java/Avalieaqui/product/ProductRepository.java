package Avalieaqui.product;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ProductRepository extends MongoRepository<Product, String> {

    @Query(value = "{ 'categoryId': ?0 }", sort = "{ 'views': -1 }")
    List<Product> findByCategoryIdOrderByViewsDesc(String categoryId);
}
