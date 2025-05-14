package com.example.demo.services;

import com.example.demo.entities.Category;
import com.example.demo.entities.Product;
import com.example.demo.entities.ProductImage;
import com.example.demo.entities.ProductSpecification;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repositories.CategoryRepo;
import com.example.demo.repositories.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    // Directory for storing product images
    private final String UPLOAD_DIR = "./uploads/products/";

    public ProductService() {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get all products (with optional pagination)
     */
    public List<Product> getAll() {
        return productRepo.findAll();
    }

    /**
     * Get all active products (with pagination)
     */
    public Page<Product> getAllActive(Pageable pageable) {
        return productRepo.findProductsByFilters(null, null, null, null, null, pageable);
    }

    /**
     * Get product by ID
     */
    public Optional<Product> getById(Long id) {
        return productRepo.findById(id);
    }

    /**
     * Get products by category ID (with pagination)
     */
    public Page<Product> getByCategoryId(Long categoryId, Pageable pageable) {
        return productRepo.findByCategoryId(categoryId, pageable);
    }

    /**
     * Search products by name (with pagination)
     */
    public Page<Product> searchByName(String name, Pageable pageable) {
        return productRepo.findByNameContainingIgnoreCase(name, pageable);
    }

    /**
     * Advanced product search with filters
     */
    public Page<Product> searchProducts(String name, Long categoryId, String brand,
                                        Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepo.findProductsByFilters(name, categoryId, brand, minPrice, maxPrice, pageable);
    }

    /**
     * Get products by brand
     */
    public List<Product> getByBrand(String brand) {
        return productRepo.findByBrand(brand);
    }

    /**
     * Get all available brands
     */
    public List<String> getAllBrands() {
        return productRepo.findDistinctBrands();
    }

    /**
     * Add a new product
     */
    @Transactional
    public Product add(Product product) {
        // Set timestamps
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setActive(true);

        // Validate category exists
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepo.findById(product.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }

        return productRepo.save(product);
    }

    /**
     * Update an existing product
     */
    @Transactional
    public Product update(Long id, Product updatedProduct) {
        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Update basic fields
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setBrand(updatedProduct.getBrand());
        existingProduct.setModel(updatedProduct.getModel());
        existingProduct.setActive(updatedProduct.isActive());
        existingProduct.setUpdatedAt(LocalDateTime.now());

        // Update category if provided
        if (updatedProduct.getCategory() != null && updatedProduct.getCategory().getId() != null) {
            Category category = categoryRepo.findById(updatedProduct.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            existingProduct.setCategory(category);
        }

        return productRepo.save(existingProduct);
    }

    /**
     * Add product image
     */
    @Transactional
    public Product addProductImage(Long productId, MultipartFile imageFile, boolean isMain) throws IOException {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Generate unique filename
        String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path targetPath = Paths.get(UPLOAD_DIR + filename);

        // Save file to disk
        Files.copy(imageFile.getInputStream(), targetPath);

        // Save image reference to database
        ProductImage image = new ProductImage(product, "/uploads/products/" + filename, isMain);
        product.getImages().add(image);

        // If this is the main image and other images exist, update their isMain status
        if (isMain) {
            product.getImages().stream()
                    .filter(img -> img.isMain() && !img.equals(image))
                    .forEach(img -> img.setMain(false));
        }

        return productRepo.save(product);
    }

    /**
     * Add product specification
     */
    @Transactional
    public Product addProductSpecification(Long productId, String specName, String specValue) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        ProductSpecification spec = new ProductSpecification(product, specName, specValue);
        product.getSpecifications().add(spec);

        return productRepo.save(product);
    }

    /**
     * Delete a specification
     */
    @Transactional
    public Product deleteSpecification(Long productId, Long specId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        product.setSpecifications(
                product.getSpecifications().stream()
                        .filter(spec -> !spec.getId().equals(specId))
                        .collect(Collectors.toList())
        );

        return productRepo.save(product);
    }

    /**
     * Deactivate a product (soft delete)
     */
    @Transactional
    public Product deactivateProduct(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setActive(false);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepo.save(product);
    }

    /**
     * Activate a product
     */
    @Transactional
    public Product activateProduct(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setActive(true);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepo.save(product);
    }

    /**
     * Hard delete a product
     */
    @Transactional
    public void delete(Long id) {
        productRepo.deleteById(id);
    }

    /**
     * Get low stock products
     */
    public List<Product> getLowStockProducts(int threshold) {
        return productRepo.findByQuantityLessThan(threshold);
    }

    /**
     * Get out of stock products
     */
    public List<Product> getOutOfStockProducts() {
        return productRepo.findByQuantity(0);
    }

    /**
     * Get products by price range
     */
    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) {
        return productRepo.findByPriceBetween(minPrice, maxPrice);
    }

    /**
     * Get products by price range and category
     */
    public List<Product> getProductsByPriceRangeAndCategory(double minPrice, double maxPrice, Long categoryId) {
        return productRepo.findByPriceRangeAndCategory(minPrice, maxPrice, categoryId);
    }

    /**
     * Get popular products
     */
    public List<Product> getPopularProducts(int limit) {
        return productRepo.findPopularProducts(limit);
    }

    /**
     * Update product stock quantity
     */
    @Transactional
    public Product updateStock(Long id, int newQuantity) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setQuantity(newQuantity);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepo.save(product);
    }

    /**
     * Adjust stock (add or remove quantity)
     */
    @Transactional
    public Product adjustStock(Long id, int quantityChange) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        int newQuantity = product.getQuantity() + quantityChange;
        if (newQuantity < 0) {
            newQuantity = 0; // Prevent negative stock
        }

        product.setQuantity(newQuantity);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepo.save(product);
    }
}