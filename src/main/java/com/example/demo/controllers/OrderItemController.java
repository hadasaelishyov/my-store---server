package com.example.demo.controllers;

import com.example.demo.entities.OrderItem;
import com.example.demo.exceptions.InvalidOrderStateException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.services.OrderItemService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orderItems")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    // Basic CRUD operations

    @GetMapping
    public ResponseEntity<List<OrderItem>> getAll() {
        return ResponseEntity.ok(orderItemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getById(@PathVariable Long id) {
        return orderItemService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody OrderItem orderItem) {
        try {
            return ResponseEntity.ok(orderItemService.add(orderItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidOrderStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody OrderItem orderItem) {
        try {
            return ResponseEntity.ok(orderItemService.update(id, orderItem));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidOrderStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            orderItemService.delete(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidOrderStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Specialized endpoints for order item management

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItem>> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderItemService.getByOrderId(orderId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<OrderItem>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(orderItemService.getByProductId(productId));
    }

    // Quantity update endpoint

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<?> updateQuantity(@PathVariable Long id, @RequestParam int quantity) {
        try {
            return ResponseEntity.ok(orderItemService.updateQuantity(id, quantity));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidOrderStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Reporting endpoints

    @GetMapping("/reports/topSelling")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProducts(@RequestParam(defaultValue = "10") int limit) {
        List<Object[]> topSellingProducts = orderItemService.getTopSellingProducts(limit);

        List<Map<String, Object>> result = topSellingProducts.stream()
                .map(row -> Map.of(
                        "productId", row[0],
                        "productName", row[1],
                        "totalSold", row[2]
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/reports/totalSalesByProduct")
    public ResponseEntity<List<Map<String, Object>>> getTotalSalesByProduct() {
        List<Object[]> salesByProduct = orderItemService.getTotalSalesByProduct();

        List<Map<String, Object>> result = salesByProduct.stream()
                .map(row -> Map.of(
                        "productId", row[0],
                        "totalQuantity", row[1]
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/reports/itemsSoldInDateRange")
    public ResponseEntity<List<OrderItem>> getItemsSoldInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(orderItemService.getItemsSoldInDateRange(startDate, endDate));
    }
}