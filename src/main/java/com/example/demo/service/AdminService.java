package com.example.demo.service;


import com.example.demo.entities.Product;
import com.example.demo.repositories.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService extends UserService {
    @Autowired
    private ProductRepo productRepo;

    @Override
    public boolean login(String email, String password) {
        return email.equals("admin@gmail.com") && password.equals("admin");
    }

    public List<Product> getAllProduct() {
        return productRepo.findAll();
    }

    public void addProduct(Product product)
    {
        if (!productRepo.existsById(product.getId())
                && !productRepo.existsByName(product.getName()))
            productRepo.save(product);
    }


}
