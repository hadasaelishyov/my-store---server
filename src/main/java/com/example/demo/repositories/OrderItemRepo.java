package com.example.demo.repositories;

import com.example.demo.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<Object[]> calculateTotalSalesByProduct();

    List<OrderItem> findByOrderOrderDateBetween(LocalDate startDate, LocalDate endDate);

    List<OrderItem> findByProductId(Long productId);
}
