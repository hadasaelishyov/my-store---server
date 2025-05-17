package com.example.demo.controllers;

import com.example.demo.entities.Category;
import com.example.demo.services.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Category>> getAllActive() {
        return ResponseEntity.ok(categoryService.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        return categoryService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Category> getByName(@PathVariable String name) {
        return categoryService.getByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Category>> searchByName(@RequestParam String keyword) {
        return ResponseEntity.ok(categoryService.searchByName(keyword));
    }

    @GetMapping("/main")
    public ResponseEntity<List<Category>> getMainCategories() {
        return ResponseEntity.ok(categoryService.getMainCategories());
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Category>> getSubcategories(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.getSubcategories(parentId));
    }

    @GetMapping("/with-products")
    public ResponseEntity<List<Category>> getCategoriesWithProducts() {
        return ResponseEntity.ok(categoryService.getCategoriesWithProducts());
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Category category) {
        try {
            Category saved = categoryService.add(category);
            if (saved == null) {
                return ResponseEntity.badRequest().body("Category with this name already exists");
            }
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("Parent category not found");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Category category) {
        try {
            return ResponseEntity.ok(categoryService.update(id, category));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Category> deactivateCategory(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(categoryService.deactivateCategory(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Category> activateCategory(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(categoryService.activateCategory(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/move-subcategories/{sourceParentId}/{targetParentId}")
    public ResponseEntity<Void> moveSubcategories(
            @PathVariable Long sourceParentId,
            @PathVariable Long targetParentId) {
        try {
            categoryService.moveSubcategories(sourceParentId, targetParentId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}