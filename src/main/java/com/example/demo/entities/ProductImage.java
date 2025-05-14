package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@ToString

@Entity
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String imageUrl;

    private boolean isMain = false;

    private LocalDateTime createdAt;

    public ProductImage(Product product, String imageUrl) {
        this.product = product;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
    }

    public ProductImage(Product product, String imageUrl, boolean isMain) {
        this(product, imageUrl);
        this.isMain = isMain;
    }
}