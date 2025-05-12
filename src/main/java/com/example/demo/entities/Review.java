package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@ToString

@Entity

public class Review {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public Review(User user, Product product, int rating, String comment, LocalDateTime createdAt) {
        this.user = user;
        this.product = product;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

}
