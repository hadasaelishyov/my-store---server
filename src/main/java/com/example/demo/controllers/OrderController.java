package com.example.demo.controllers;

import com.example.demo.entities.Order;
import com.example.demo.entities.Payment;
import com.example.demo.entities.PaymentMethod;
import com.example.demo.entities.StatusOrder;
import com.example.demo.exceptions.InvalidOrderStateException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.services.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Basic CRUD operations

    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Order>> getAllPaginated(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllPaginated(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
        return orderService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order order) {
        try {
            return ResponseEntity.ok(orderService.add(order));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody Order order) {
        try {
            return ResponseEntity.ok(orderService.update(id, order));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            orderService.delete(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidOrderStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // Specialized endpoints for order management

    @GetMapping("/user/{email}")
    public ResponseEntity<Page<Order>> getByUserEmail(
            @PathVariable String email,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(orderService.getByUserEmail(email, pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Order>> getByStatus(
            @PathVariable StatusOrder status,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(orderService.getByStatus(status, pageable));
    }

    @GetMapping("/dateRange")
    public ResponseEntity<Page<Order>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(orderService.getByDateRange(startDate, endDate, pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<Order>> filterOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) StatusOrder status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Double minAmount,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(orderService.filterOrders(userId, status, startDate, endDate, minAmount, pageable));
    }

    // Order status management

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam StatusOrder newStatus,
            @RequestParam(required = false, defaultValue = "") String comment) {
        try {
            Order updatedOrder = orderService.updateStatus(id, newStatus, comment);
            return ResponseEntity.ok(updatedOrder);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidOrderStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Order creation from cart

    @PostMapping("/cart/{cartId}")
    public ResponseEntity<?> createFromCart(
            @PathVariable Long cartId,
            @RequestParam String shippingAddress,
            @RequestParam String shippingCity,
            @RequestParam String shippingZipCode,
            @RequestParam String shippingCountry) {
        try {
            Order order = orderService.createOrderFromCart(
                    cartId, shippingAddress, shippingCity, shippingZipCode, shippingCountry);
            return ResponseEntity.ok(order);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidOrderStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Payment processing

    @PostMapping("/{id}/payment")
    public ResponseEntity<?> processPayment(
            @PathVariable Long id,
            @RequestParam PaymentMethod paymentMethod,
            @RequestParam String transactionId) {
        try {
            Payment payment = orderService.processPayment(id, paymentMethod, transactionId);
            return ResponseEntity.ok(payment);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Admin dashboard endpoints

    @GetMapping("/stats/status")
    public ResponseEntity<Map<StatusOrder, Long>> getOrderStatusCounts() {
        return ResponseEntity.ok(orderService.getOrderStatusCounts());
    }

    @GetMapping("/stats/revenue")
    public ResponseEntity<List<Object[]>> getRevenueByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(orderService.getRevenueByDateRange(startDate, endDate));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Order>> getRecentOrders() {
        return ResponseEntity.ok(orderService.getRecentOrders());
    }
}