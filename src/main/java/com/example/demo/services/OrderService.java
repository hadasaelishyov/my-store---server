package com.example.demo.services;

import com.example.demo.entities.*;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repositories.CartItemRepo;
import com.example.demo.repositories.CartRepo;
import com.example.demo.repositories.OrderItemRepo;
import com.example.demo.repositories.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<Order> getAll() {
        return orderRepo.findAll();
    }

    public Optional<Order> getById(Long id) {
        return orderRepo.findById(id);
    }

    public Order add(Order item) {
        return orderRepo.save(item);
    }

    public Order update(Long id, Order updatedItem) {
        if (orderRepo.existsById(id)) {
            updatedItem.setId(id);
            return orderRepo.save(updatedItem);
        }
        return null;
    }

    public void delete(Long id) {
        orderRepo.deleteById(id);
    }

    @Transactional
    public Order createOrderFromCart(Long cartId) {
        Optional<Cart> cartOptional = cartRepo.findById(cartId);

        if (!cartOptional.isPresent()) {
            return null;
        }

        Cart cart = cartOptional.get();

        // Only process active carts
        if (!cart.isActive()) {
            return null;
        }

        // Create new order
        Order order = new Order(cart.getUser(), LocalDate.now(), StatusOrder.PENDING);
        order = orderRepo.save(order);

        // Get cart items
        List<CartItem> cartItems = cartItemRepo.findByCartId(cartId);
        double totalAmount = 0.0;

        // Create order items from cart items
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            double itemPrice = product.getPrice() * cartItem.getQuantity();
            totalAmount += itemPrice;

            OrderItem orderItem = new OrderItem(order, product, cartItem.getQuantity(), product.getPrice());
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

    public List<Order> getByStatus(StatusOrder statusOrder) {
        return orderRepo.findAll().stream()
                .filter(order -> order.getStatusOrder() == statusOrder)
                .collect(Collectors.toList());
    }

    @Transactional
    public Order updateStatus(Long id, StatusOrder status) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setStatusOrder(status);
        return orderRepo.save(order);
    }
}