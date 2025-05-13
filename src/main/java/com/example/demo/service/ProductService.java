package com.example.demo.service;

import com.example.demo.entities.Product;
import com.example.demo.repositories.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepo productRepo;

    public List<Product> getAll() {
        return productRepo.findAll();
    }

    public Optional<Product> getById(Long id) {
        return productRepo.findById(id);
    }

    public Product add(Product item) {
        if (!productRepo.existsById(item.getId())
                && !productRepo.existsByName(item.getName()))
                        return productRepo.save(item);
        return null;
    }

    public Product update(Long id, Product updatedItem) {
        if (productRepo.existsById(id)) {
            updatedItem.setId(id);
            return productRepo.save(updatedItem);
        }
        return null;
    }

    public void delete(Long id) {
        productRepo.deleteById(id);
    }
}
