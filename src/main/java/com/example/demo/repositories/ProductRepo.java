package com.example.demo.repositories;

import com.example.demo.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByBrand(String brand);

    boolean existsByName(String name);
    List<Product> findByPriceBetween(double minPrice, double maxPrice);

    List<Product> findByQuantityLessThan(int quantity);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.category.id = :categoryId")
    List<Product> findByPriceRangeAndCategory(
            @Param("minPrice") double minPrice,
            @Param("maxPrice") double maxPrice,
            @Param("categoryId") Long categoryId
    );
}
