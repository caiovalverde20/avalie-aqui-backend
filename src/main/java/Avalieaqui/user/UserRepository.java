package Avalieaqui.user;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);

    User findByEmailOrPhone(String email, String phone);

    Optional<User> findById(String id);
}
