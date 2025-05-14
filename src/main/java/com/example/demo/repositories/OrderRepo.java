package com.example.demo.repositories;

import com.example.demo.entities.Order;
import com.example.demo.entities.StatusOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    // שאילתות בסיסיות
    List<Order> findByUser_Email(String email);

    // תמיכה בדפדוף להזמנות של משתמש
    Page<Order> findByUser_Email(String email, Pageable pageable);

    List<Order> findByUser_Id(Long userId);

    List<Order> findByStatusOrder(StatusOrder statusOrder);

    // מציאת הזמנות לפי סטטוס עם דפדוף
    Page<Order> findByStatusOrder(StatusOrder statusOrder, Pageable pageable);

    List<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);

    // הזמנות לפי תאריך עם דפדוף
    Page<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<Order> findByUser_EmailAndStatusOrder(String email, StatusOrder statusOrder);

    List<Order> findByTotalAmountGreaterThan(double amount);

    // מציאת הזמנות לפי מוצר
    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.product.id = :productId")
    List<Order> findByProductId(@Param("productId") Long productId);

    // שאילתות לדשבורד מנהל

    // כמות הזמנות לפי סטטוס
    @Query("SELECT o.statusOrder, COUNT(o) FROM Order o GROUP BY o.statusOrder")
    List<Object[]> countOrdersByStatus();

    // סיכום הכנסות לפי תאריך
    @Query("SELECT o.orderDate, SUM(o.totalAmount) FROM Order o " +
            "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY o.orderDate " +
            "ORDER BY o.orderDate")
    List<Object[]> sumOrderAmountsByDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // הזמנות אחרונות
    List<Order> findTop10ByOrderByCreatedAtDesc();

    // שאילתה מורכבת למציאת הזמנות לפי מספר פרמטרים
    @Query("SELECT o FROM Order o WHERE " +
            "(:userId IS NULL OR o.user.id = :userId) AND " +
            "(:status IS NULL OR o.statusOrder = :status) AND " +
            "(:startDate IS NULL OR o.orderDate >= :startDate) AND " +
            "(:endDate IS NULL OR o.orderDate <= :endDate) AND " +
            "(:minAmount IS NULL OR o.totalAmount >= :minAmount)")
    Page<Order> findOrdersByFilters(
            @Param("userId") Long userId,
            @Param("status") StatusOrder status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") Double minAmount,
            Pageable pageable
    );
}