
package com.example.demo.service;

import com.example.demo.entities.Cart;
import com.example.demo.repositories.CartRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepo cartRepo;

    public List<Cart> getAll() {
        return cartRepo.findAll();
    }

    public Optional<Cart> getById(Long id) {
        return cartRepo.findById(id);
    }

    public Cart add(Cart item) {
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
