package com.example.demo.repositories;

import com.example.demo.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {
    List<Cart> findByUserEmail(String email);

    Optional<Cart> findByUserEmailAndActiveTrue(String email);

    Optional<Cart> findByUserIdAndActiveTrue(Long userId);

    // מציאת עגלות נטושות (לא פעילות ולא הושלמו להזמנה)
    List<Cart> findByActiveFalseAndUpdatedAtBefore(LocalDateTime dateTime);

    // ספירת כמות העגלות הפעילות
    Long countByActiveTrue();

    // מציאת עגלות שמכילות מוצר מסוים
    @Query("SELECT c FROM Cart c JOIN c.cartItems ci WHERE ci.product.id = :productId AND c.active = true")
    List<Cart> findActiveCartsByProductId(@Param("productId") Long productId);

    // מציאת עגלות שלא עודכנו מזמן אבל עדיין פעילות
    List<Cart> findByActiveTrueAndUpdatedAtBefore(LocalDateTime dateTime);
}