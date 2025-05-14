package com.example.demo.services;

import com.example.demo.entities.Category;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repositories.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;

    /**
     * Get all categories
     */
    public List<Category> getAll() {
        return categoryRepo.findAll();
    }

    /**
     * Get only active categories
     */
    public List<Category> getAllActive() {
        return categoryRepo.findByActiveTrue();
    }

    /**
     * Get category by ID
     */
    public Optional<Category> getById(Long id) {
        return categoryRepo.findById(id);
    }

    /**
     * Get category by name
     */
    public Optional<Category> getByName(String name) {
        return categoryRepo.findByNameIgnoreCase(name);
    }

    /**
     * Search categories by name (partial match)
     */
    public List<Category> searchByName(String keyword) {
        return categoryRepo.findByNameContainingIgnoreCase(keyword);
    }

    /**
     * Get main/parent categories only
     */
    public List<Category> getMainCategories() {
        return categoryRepo.findByParentIsNull();
    }

    /**
     * Get subcategories of a parent category
     */
    public List<Category> getSubcategories(Long parentId) {
        return categoryRepo.findByParentId(parentId);
    }

    /**
     * Get categories with active products
     */
    public List<Category> getCategoriesWithProducts() {
        return categoryRepo.findCategoriesWithActiveProducts();
    }

    /**
     * Add a new category
     */
    @Transactional
    public Category add(Category category) {
        // Check if category with same name exists
        if (categoryRepo.existsByName(category.getName())) {
            return null; // Category already exists
        }

        // Set timestamps
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        // Set default values
        if (category.isActive() == false) {
            category.setActive(true);
        }

        // Set parent category if provided
        if (category.getParent() != null && category.getParent().getId() != null) {
            Category parent = categoryRepo.findById(category.getParent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            category.setParent(parent);
        }

        return categoryRepo.save(category);
    }

    /**
     * Update an existing category
     */
    @Transactional
    public Category update(Long id, Category updatedCategory) {
        Category existingCategory = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Update properties
        existingCategory.setName(updatedCategory.getName());
        existingCategory.setDescription(updatedCategory.getDescription());
        existingCategory.setImageUrl(updatedCategory.getImageUrl());
        existingCategory.setActive(updatedCategory.isActive());
        existingCategory.setUpdatedAt(LocalDateTime.now());

        // Update parent if provided
        if (updatedCategory.getParent() != null && updatedCategory.getParent().getId() != null) {
            // Check to prevent circular references (category can't be its own parent)
            if (updatedCategory.getParent().getId().equals(id)) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }

            Category parent = categoryRepo.findById(updatedCategory.getParent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            existingCategory.setParent(parent);
        } else {
            // Remove parent reference if null
            existingCategory.setParent(null);
        }

        return categoryRepo.save(existingCategory);
    }

    /**
     * Delete a category
     * Note: This should be used with caution as it may affect products
     */
    @Transactional
    public void delete(Long id) {
        categoryRepo.deleteById(id);
    }

    /**
     * Deactivate a category (soft delete)
     */
    @Transactional
    public Category deactivateCategory(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        category.setActive(false);
        category.setUpdatedAt(LocalDateTime.now());

        return categoryRepo.save(category);
    }

    /**
     * Activate a category
     */
    @Transactional
    public Category activateCategory(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        category.setActive(true);
        category.setUpdatedAt(LocalDateTime.now());

        return categoryRepo.save(category);
    }

    /**
     * Move subcategories from one parent to another
     * Useful when deleting a category but wanting to preserve its subcategories
     */
    @Transactional
    public void moveSubcategories(Long sourceParentId, Long targetParentId) {
        // Get source parent
        Category sourceParent = categoryRepo.findById(sourceParentId)
                .orElseThrow(() -> new ResourceNotFoundException("Source category not found"));

        // Get target parent
        Category targetParent = categoryRepo.findById(targetParentId)
                .orElseThrow(() -> new ResourceNotFoundException("Target category not found"));

        // Get all subcategories of source parent
        List<Category> subcategories = categoryRepo.findByParentId(sourceParentId);

        // Update parent for each subcategory
        for (Category subcategory : subcategories) {
            subcategory.setParent(targetParent);
            subcategory.setUpdatedAt(LocalDateTime.now());
            categoryRepo.save(subcategory);
        }
    }
}