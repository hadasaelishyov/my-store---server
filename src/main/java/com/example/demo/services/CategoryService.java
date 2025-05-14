
package com.example.demo.services;

import com.example.demo.entities.Category;
import com.example.demo.repositories.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;

    public List<Category> getAll() {
        return categoryRepo.findAll();
    }

    public Optional<Category> getById(Long id) {
        return categoryRepo.findById(id);
    }

    public Category add(Category item) {
        return categoryRepo.save(item);
    }

    public Category update(Long id, Category updatedItem) {
        if (categoryRepo.existsById(id)) {
            updatedItem.setId(id);
            return categoryRepo.save(updatedItem);
        }
        return null;
    }

    public void delete(Long id) {
        categoryRepo.deleteById(id);
    }
}
