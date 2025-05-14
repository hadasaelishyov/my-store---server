package com.example.demo.repositories;

import com.example.demo.entities.Order;
import com.example.demo.entities.StatusOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByUser_Email(String email);

    List<Order> findByStatusOrder(StatusOrder statusOrder);

    List<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);

    List<Order> findByUser_EmailAndStatusOrder(String email, StatusOrder statusOrder);

    List<Order> findByTotalAmountGreaterThan(double amount);
}