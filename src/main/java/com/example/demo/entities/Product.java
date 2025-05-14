package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@ToString

@Entity
public class Product {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String description;
    private double price;
    private String image;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private int quantity;
    private String brand;
    private String model;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews = new ArrayList<Review>();

    public Product(String name, String description, double price, String image, int category_id, int quantity, String brand, String model) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = new Category();
        this.quantity = quantity;
        this.brand = brand;
        this.model = model;
    }
}
