package Avalieaqui.product;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import Avalieaqui.category.Category;

@Document(collection = "produtos")
public class Product {
    @Id
    private String id;
    private String name;
    private String image;
    private int views = 0;
    private String categoryId = "3";

    // Construtores
    public Product() {
    }

    public Product(String name) {
        this.name = name;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public int getViews() {
        return views;
    }

    public String getCategoryId() {
        return categoryId;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageLink(String image) {
        this.image = image;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
