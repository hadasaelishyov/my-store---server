package com.example.demo.repositories;

import com.example.demo.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {

    Category findByName(String name);

    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

    // מציאת קטגוריות ראשיות (שאין להן הורה)
    List<Category> findByParentIsNull();

    // מציאת תת-קטגוריות של קטגוריה מסוימת
    List<Category> findByParentId(Long parentId);

    // מציאת קטגוריות אקטיביות בלבד
    List<Category> findByActiveTrue();

    // מציאת קטגוריות שיש להן מוצרים
    @Query("SELECT DISTINCT c FROM Category c JOIN c.products p WHERE p.active = true")
    List<Category> findCategoriesWithActiveProducts();

    // מציאת קטגוריות לפי חיפוש טקסט
    List<Category> findByNameContainingIgnoreCase(String keyword);
}