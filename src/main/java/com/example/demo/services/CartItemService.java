
package com.example.demo.services;

import com.example.demo.entities.CartItem;
import com.example.demo.repositories.CartItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemService {
    @Autowired
    private CartItemRepo cartItemRepo;

    public List<CartItem> getAll() {
        return cartItemRepo.findAll();
    }

    public Optional<CartItem> getById(Long id) {
        return cartItemRepo.findById(id);
    }

    public CartItem add(CartItem item) {
        return cartItemRepo.save(item);
    }

    public CartItem update(Long id, CartItem updatedItem) {
        if (cartItemRepo.existsById(id)) {
            updatedItem.setId(id);
            return cartItemRepo.save(updatedItem);
        }
        return null;
    }

    public void delete(Long id) {
        cartItemRepo.deleteById(id);
    }
}
