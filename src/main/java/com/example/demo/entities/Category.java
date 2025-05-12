package com.example.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@ToString

@Entity
public class Category {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String description;

    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<Product>();

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
