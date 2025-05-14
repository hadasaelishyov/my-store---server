package com.example.demo.controllers;

import com.example.demo.entities.Order;
import com.example.demo.entities.StatusOrder;
import com.example.demo.services.OrderService;
import com.example.demo.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", orderService.getAll().size());
        stats.put("pendingOrders", orderService.getByStatus(StatusOrder.PENDING).size());
        stats.put("lowStockProducts", productService.getLowStockProducts(5));
        // Add more admin stats
        return stats;
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> status) {
        return new ResponseEntity<>(orderService.updateStatus(id, StatusOrder.valueOf(status.get("status"))), HttpStatus.OK);
    }
}