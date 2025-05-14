package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Data
@ToString

@Entity
public class ProductSpecification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String specName;

    private String specValue;

    public ProductSpecification(Product product, String specName, String specValue) {
        this.product = product;
        this.specName = specName;
        this.specValue = specValue;
    }
}