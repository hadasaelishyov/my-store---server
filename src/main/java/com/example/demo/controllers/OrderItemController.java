package com.example.demo.controllers;

import com.example.demo.entities.OrderItem;
import com.example.demo.services.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orderItems")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;

    @GetMapping
    public List<OrderItem> getAll() {
        return orderItemService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<OrderItem> getById(@PathVariable Long id) {
        return orderItemService.getById(id);
    }

    @PostMapping
    public OrderItem add(@RequestBody OrderItem orderItem) {
        return orderItemService.add(orderItem);
    }

    @PutMapping("/{id}")
    public OrderItem update(@PathVariable Long id, @RequestBody OrderItem orderItem) {
        return orderItemService.update(id, orderItem);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderItemService.delete(id);
    }
}