package com.example.demo.services;

import com.example.demo.entities.*;
import com.example.demo.exceptions.InsufficientInventoryException;
import com.example.demo.exceptions.InvalidOrderStateException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing orders in the e-commerce system.
 * Handles order creation, processing, status updates, and management.
 */
@Service
public class OrderService {
    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private OrderItemRepo orderItemRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    /**
     * Get all orders with optional pagination
     */
    public Page<Order> getAllPaginated(Pageable pageable) {
        return orderRepo.findAll(pageable);
    }

    /**
     * Get all orders (without pagination)
     */
    public List<Order> getAll() {
        return orderRepo.findAll();
    }

    /**
     * Get order by ID
     */
    public Optional<Order> getById(Long id) {
        return orderRepo.findById(id);
    }

    /**
     * Get orders by user email with pagination
     */
    public Page<Order> getByUserEmail(String email, Pageable pageable) {
        return orderRepo.findByUser_Email(email, pageable);
    }

    /**
     * Get orders by status with pagination
     */
    public Page<Order> getByStatus(StatusOrder status, Pageable pageable) {
        return orderRepo.findByStatusOrder(status, pageable);
    }

    /**
     * Get orders by date range with pagination
     */
    public Page<Order> getByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return orderRepo.findByOrderDateBetween(startDate, endDate, pageable);
    }

    /**
     * Filter orders with advanced criteria and pagination
     */
    public Page<Order> filterOrders(Long userId, StatusOrder status,
                                    LocalDate startDate, LocalDate endDate,
                                    Double minAmount, Pageable pageable) {
        return orderRepo.findOrdersByFilters(userId, status, startDate, endDate, minAmount, pageable);
    }

    /**
     * Create a new order
     */
    public Order add(Order order) {
        // Validate order data
        if (order.getUser() == null) {
            throw new IllegalArgumentException("Order must have a user");
        }

        // Set timestamps
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(LocalDateTime.now());
        }
        order.setUpdatedAt(LocalDateTime.now());

        // Set initial status if not provided
        if (order.getStatusOrder() == null) {
            order.updateStatus(StatusOrder.PENDING, "Order created");
        }

        return orderRepo.save(order);
    }

    /**
     * Update an existing order
     */
    public Order update(Long id, Order updatedOrder) {
        Order existingOrder = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Update order fields
        if (updatedOrder.getShippingAddress() != null) {
            existingOrder.setShippingAddress(updatedOrder.getShippingAddress());
        }
        if (updatedOrder.getShippingCity() != null) {
            existingOrder.setShippingCity(updatedOrder.getShippingCity());
        }
        if (updatedOrder.getShippingZipCode() != null) {
            existingOrder.setShippingZipCode(updatedOrder.getShippingZipCode());
        }
        if (updatedOrder.getShippingCountry() != null) {
            existingOrder.setShippingCountry(updatedOrder.getShippingCountry());
        }
        if (updatedOrder.getTrackingNumber() != null) {
            existingOrder.setTrackingNumber(updatedOrder.getTrackingNumber());
        }

        existingOrder.setUpdatedAt(LocalDateTime.now());
        return orderRepo.save(existingOrder);
    }

    /**
     * Delete an order (should only be available for PENDING orders)
     */
    @Transactional
    public void delete(Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Only allow deletion of pending orders
        if (order.getStatusOrder() != StatusOrder.PENDING) {
            throw new InvalidOrderStateException("Can only delete orders in PENDING state");
        }

        orderRepo.deleteById(id);
    }

    /**
     * Get orders by status
     */
    public List<Order> getByStatus(StatusOrder statusOrder) {
        return orderRepo.findByStatusOrder(statusOrder);
    }

    /**
     * Update order status with validation and history tracking
     */
    @Transactional
    public Order updateStatus(Long id, StatusOrder newStatus, String comment) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Validate status transition
        validateStatusTransition(order.getStatusOrder(), newStatus);

        // Update the order status
        order.updateStatus(newStatus, comment);

        // If order is completed, update product inventory
        if (newStatus == StatusOrder.DELIVERED) {
            updateProductInventoryForDeliveredOrder(order);
        }

        // If order is cancelled, restore inventory
        if (newStatus == StatusOrder.CANCELLED) {
            restoreProductInventory(order);
        }

        return orderRepo.save(order);
    }

    /**
     * Validate that a status transition is allowed
     */
    private void validateStatusTransition(StatusOrder currentStatus, StatusOrder newStatus) {
        // Define allowed transitions
        Map<StatusOrder, List<StatusOrder>> allowedTransitions = Map.of(
                StatusOrder.PENDING, List.of(StatusOrder.PROCESSING, StatusOrder.CANCELLED),
                StatusOrder.PROCESSING, List.of(StatusOrder.SHIPPED, StatusOrder.CANCELLED),
                StatusOrder.SHIPPED, List.of(StatusOrder.DELIVERED, StatusOrder.RETURNED),
                StatusOrder.DELIVERED, List.of(StatusOrder.RETURNED),
                StatusOrder.CANCELLED, List.of(), // Terminal state
                StatusOrder.RETURNED, List.of()   // Terminal state
        );

        if (!allowedTransitions.getOrDefault(currentStatus, List.of()).contains(newStatus)) {
            throw new InvalidOrderStateException(
                    "Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    /**
     * Update product inventory when order is marked as delivered
     */
    private void updateProductInventoryForDeliveredOrder(Order order) {
        // Implementation depends on your inventory management approach
        // This is a placeholder - actual implementation would depend on your requirements
    }

    /**
     * Restore product inventory when order is cancelled
     */
    private void restoreProductInventory(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepo.save(product);
        }
    }

    /**
     * Create an order from a shopping cart
     */
    @Transactional
    public Order createOrderFromCart(Long cartId, String shippingAddress,
                                     String shippingCity, String shippingZipCode,
                                     String shippingCountry) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + cartId));

        // Only process active carts
        if (!cart.isActive()) {
            throw new InvalidOrderStateException("Cannot create order from inactive cart");
        }

        // Check if cart is empty
        if (cart.getCartItems().isEmpty()) {
            throw new InvalidOrderStateException("Cannot create order from empty cart");
        }

        // Validate inventory before creating order
        validateInventory(cart);

        // Create new order
        Order order = new Order(cart.getUser(), LocalDate.now(), StatusOrder.PENDING);
        order.setShippingAddress(shippingAddress);
        order.setShippingCity(shippingCity);
        order.setShippingZipCode(shippingZipCode);
        order.setShippingCountry(shippingCountry);
        order = orderRepo.save(order);

        // Get cart items
        List<CartItem> cartItems = cartItemRepo.findByCartId(cartId);
        double totalAmount = 0.0;

        // Create order items from cart items and update inventory
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // Update product inventory
            int newQuantity = product.getQuantity() - cartItem.getQuantity();
            if (newQuantity < 0) {
                throw new InsufficientInventoryException(
                        "Not enough inventory for product: " + product.getName());
            }
            product.setQuantity(newQuantity);
            productRepo.save(product);

            // Create order item
            double itemPrice = cartItem.getUnitPrice() * cartItem.getQuantity();
            totalAmount += itemPrice;

            OrderItem orderItem = new OrderItem(order, product, cartItem.getQuantity(), cartItem.getUnitPrice());
            orderItemRepo.save(orderItem);
        }

        // Update order total
        order.setTotalAmount(totalAmount);
        order = orderRepo.save(order);

        // Deactivate the cart
        cart.setActive(false);
        cartRepo.save(cart);

        return order;
    }

    /**
     * Validate that there is sufficient inventory for all items in the cart
     */
    private void validateInventory(Cart cart) {
        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            if (product.getQuantity() < item.getQuantity()) {
                throw new InsufficientInventoryException(
                        "Not enough inventory for product: " + product.getName() +
                                ". Available: " + product.getQuantity() +
                                ", Requested: " + item.getQuantity());
            }
        }
    }

    /**
     * Process payment for an order
     */
    @Transactional
    public Payment processPayment(Long orderId, PaymentMethod paymentMethod, String transactionId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Create payment
        Payment payment = new Payment(order, paymentMethod, order.getTotalAmount());

        // Complete payment with transaction ID
        payment.completePayment(transactionId);

        // Save payment
        payment = paymentRepo.save(payment);

        // Update order status
        order.updateStatus(StatusOrder.PROCESSING, "Payment processed");
        orderRepo.save(order);

        return payment;
    }

    /**
     * Get order statistics for admin dashboard
     */
    public Map<StatusOrder, Long> getOrderStatusCounts() {
        List<Object[]> results = orderRepo.countOrdersByStatus();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (StatusOrder) result[0],
                        result -> (Long) result[1]
                ));
    }

    /**
     * Get revenue statistics by date range
     */
    public List<Object[]> getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        return orderRepo.sumOrderAmountsByDate(startDate, endDate);
    }

    /**
     * Get recent orders
     */
    public List<Order> getRecentOrders() {
        return orderRepo.findTop10ByOrderByCreatedAtDesc();
    }
}