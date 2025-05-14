package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@ToString

@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private StatusOrder status;

    private String comment;

    private LocalDateTime timestamp;

    public OrderStatusHistory(Order order, StatusOrder status, String comment) {
        this.order = order;
        this.status = status;
        this.comment = comment;
        this.timestamp = LocalDateTime.now();
    }
}