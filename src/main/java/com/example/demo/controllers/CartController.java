package com.example.demo.controllers;

import com.example.demo.entities.Cart;
import com.example.demo.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Cart> getById(@PathVariable Long id) {
        Optional<Cart> cart = cartService.getById(id);
        return cart.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{email}")
    public List<Cart> getByUserEmail(@PathVariable String email) {
        return cartService.getByUserEmail(email);
    }

    @GetMapping("/user/{email}/active")
    public ResponseEntity<Cart> getActiveCartByUserEmail(@PathVariable String email) {
        Optional<Cart> cart = cartService.getActiveCartByUserEmail(email);
        return cart.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/user/{email}")
    public ResponseEntity<Cart> createCartForUser(@PathVariable String email) {
        Cart newCart = cartService.createCartForUser(email);
        if (newCart != null) {
            return new ResponseEntity<>(newCart, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    public Cart add(@RequestBody Cart cart) {
        return cartService.add(cart);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cart> update(@PathVariable Long id, @RequestBody Cart cart) {
        Cart updatedCart = cartService.update(id, cart);
        if (updatedCart != null) {
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cartService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}