
package com.example.demo.service;

import com.example.demo.entities.OrderItem;
import com.example.demo.repositories.OrderItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepo orderItemRepo;

    public List<OrderItem> getAll() {
        return orderItemRepo.findAll();
    }

    public Optional<OrderItem> getById(Long id) {
        return orderItemRepo.findById(id);
    }

    public OrderItem add(OrderItem item) {
        return orderItemRepo.save(item);
    }

    public OrderItem update(Long id, OrderItem updatedItem) {
        if (orderItemRepo.existsById(id)) {
            updatedItem.setId(id);
            return orderItemRepo.save(updatedItem);
        }
        return null;
    }

    public void delete(Long id) {
        orderItemRepo.deleteById(id);
    }
}
