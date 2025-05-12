package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@ToString

@Entity
public class User {
    @Id
    private String email;
    private String username;
    private String password;
    private String phone;
    private String address;
    private boolean is_admin;

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Cart> carts = new ArrayList<>();

    public User(String username, String password, String email, String phone, String address, boolean is_admin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.is_admin = is_admin;
    }
}
