package com.example.demo.controllers;

import com.example.demo.entities.Cart;
import com.example.demo.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carts")
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping
    public List<Cart> getAll() {
        return cartService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Cart> getById(@PathVariable Long id) {
        return cartService.getById(id);
    }

    @PostMapping
    public Cart add(@RequestBody Cart cart) {
        return cartService.add(cart);
    }

    @PutMapping("/{id}")
    public Cart update(@PathVariable Long id, @RequestBody Cart cart) {
        return cartService.update(id, cart);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        cartService.delete(id);
    }
}