package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Data
@ToString

@Entity
public class OrderItem {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private int quantity;
    private double unitPrice;
    public OrderItem(Order order, Product product, int quantity, double unitPrice) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

}
