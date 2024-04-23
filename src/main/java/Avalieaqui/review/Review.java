package Avalieaqui.review;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    private String userId;
    private String productId;
    private int stars;
    private String comment;
    private String title;
    @CreatedDate
    private LocalDateTime createdAt;
    private Set<String> likes = new HashSet<>();
    private Set<String> dislikes = new HashSet<>();

    public Review() {
    }

    public Review(String userId, String productId, int stars, String comment, String title) {
        this.userId = userId;
        this.productId = productId;
        this.stars = stars;
        this.comment = comment;
        this.title = title;
    }

    // Getters

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getProductId() {
        return productId;
    }

    public int getStars() {
        return stars;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<String> getLikes() {
        return likes;
    }

    public Set<String> getDislikes() {
        return dislikes;
    }

    // Setters

    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setStars(int stars) {
        if (stars >= 1 && stars <= 5) {
            this.stars = stars;
        } else {
            throw new IllegalArgumentException("Stars must be between 1 and 5");
        }
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setLikes(Set<String> likes) {
        this.likes = likes;
    }

    public void setDislikes(Set<String> dislikes) {
        this.dislikes = dislikes;
    }
}
