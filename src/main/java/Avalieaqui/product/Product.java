package Avalieaqui.product;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "produtos")
public class Product {
    @Id
    private String id;
    private String name;
    private String image_link;

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

    public String getImageLink() {
        return image_link;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageLink(String new_image_link) {
        this.image_link = new_image_link;
    }
}
