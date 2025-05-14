package com.example.demo.repositories;

import com.example.demo.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {
    List<Cart> findByUserEmail(String email);

    Optional<Cart> findByUserEmailAndActiveTrue(String email);
}