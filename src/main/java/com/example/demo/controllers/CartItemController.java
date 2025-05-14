package com.example.demo.controllers;

import com.example.demo.entities.CartItem;
import com.example.demo.services.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cartItems")
@CrossOrigin(origins = "http://localhost:3000")
public class CartItemController {
    @Autowired
    private CartItemService cartItemService;

    @GetMapping
    public List<CartItem> getAll() {
        return cartItemService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<CartItem> getById(@PathVariable Long id) {
        return cartItemService.getById(id);
    }

    @PostMapping
    public CartItem add(@RequestBody CartItem cartItem) {
        return cartItemService.add(cartItem);
    }

    @PutMapping("/{id}")
    public CartItem update(@PathVariable Long id, @RequestBody CartItem cartItem) {
        return cartItemService.update(id, cartItem);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        cartItemService.delete(id);
    }
}