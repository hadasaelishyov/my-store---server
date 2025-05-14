package com.example.demo.services;

import com.example.demo.entities.Cart;
import com.example.demo.entities.User;
import com.example.demo.repositories.CartRepo;
import com.example.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private UserRepo userRepo;

    public List<Cart> getAll() {
        return cartRepo.findAll();
    }

    public Optional<Cart> getById(Long id) {
        return cartRepo.findById(id);
    }

    public List<Cart> getByUserEmail(String email) {
        return cartRepo.findByUserEmail(email);
    }

    public Optional<Cart> getActiveCartByUserEmail(String email) {
        return cartRepo.findByUserEmailAndActiveTrue(email);
    }

    public Cart createCartForUser(String email) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            Cart cart = new Cart(user);
            cart.setCreatedAt(LocalDateTime.now());
            return cartRepo.save(cart);
        }
        return null;
    }

    public Cart add(Cart item) {
        item.setCreatedAt(LocalDateTime.now());
        return cartRepo.save(item);
    }

    public Cart update(Long id, Cart updatedItem) {
        if (cartRepo.existsById(id)) {
            updatedItem.setId(id);
            return cartRepo.save(updatedItem);
        }
        return null;
    }

    public void delete(Long id) {
        cartRepo.deleteById(id);
    }
}