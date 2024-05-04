package Avalieaqui.product;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import Avalieaqui.category.Category;

@Document(collection = "produtos")
public class Product {
    @Id
    private String id;
    private String name;
    private String description;
    private String image;
    private int views = 0;
    private String categoryId = "3";
    private String specification;
    private double average;

    public Product() {
    }

    public Product(String name) {
        this.name = name;
        this.image = image;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public String getSpecification() {
        return specification;
    }

    public double getAverage() {
        return average;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public void setAverage(double average) {
        this.average = average;
    }
}
