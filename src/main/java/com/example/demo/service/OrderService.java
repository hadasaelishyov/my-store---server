
package com.example.demo.service;

import com.example.demo.entities.Order;
import com.example.demo.repositories.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepo orderRepo;

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
}
