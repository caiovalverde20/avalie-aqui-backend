package Avalieaqui.review;

import java.time.LocalDateTime;

public class ReviewDto {
    private String id;
    private String userId;
    private String userName;
    private String productId;
    private int stars;
    private String title;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewDto(String id, String userId, String userName, String productId, int stars, String title,
            String comment, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.productId = productId;
        this.stars = stars;
        this.title = title;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getProductId() {
        return productId;
    }

    public int getStars() {
        return stars;
    }

    public String getTitle() {
        return title;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
