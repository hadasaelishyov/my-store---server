package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@ToString

@Entity
public class Order {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    private StatusOrder stutusOrder;
    private double totalAmount;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<OrderItem>();

    public Order(User user, LocalDate orderDate, StatusOrder stutusOrder) {
        this.user = user;
        this.orderDate = orderDate;
        this.stutusOrder = stutusOrder;
        this.totalAmount = 0;
    }

}
