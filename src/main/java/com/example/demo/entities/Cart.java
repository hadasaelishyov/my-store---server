package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@ToString

@Entity

public class Cart {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;
    private boolean active;
    private int quantity;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItems = new ArrayList<CartItem>();

    public Cart(User user) {
        this.user = user;
        this.active = true;
        quantity = 0;
    }
}
