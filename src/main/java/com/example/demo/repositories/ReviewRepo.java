package com.example.demo.repositories;

import com.example.demo.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Long> {


    // כל הביקורות על מוצר מסוים
    List<Review> findByProductId(Long productId);


    // לסדר את הביקורות של מוצר מהחדשות לישנות
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
}
