package com.example.demo.controllers;

import com.example.demo.entities.Order;
import com.example.demo.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Order> getAll() {
        return orderService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Order> getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @PostMapping
    public Order add(@RequestBody Order order) {
        return orderService.add(order);
    }

    @PutMapping("/{id}")
    public Order update(@PathVariable Long id, @RequestBody Order order) {
        return orderService.update(id, order);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
}