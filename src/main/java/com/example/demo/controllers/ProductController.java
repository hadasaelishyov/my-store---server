package com.example.demo.controllers;

import com.example.demo.entities.Product;
import com.example.demo.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Product> getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PostMapping
    public Product add(@RequestBody Product product) {
        return productService.add(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
