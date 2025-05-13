
package com.example.demo.service;

import com.example.demo.entities.Review;
import com.example.demo.repositories.ReviewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepo reviewRepo;

    public List<Review> getAll() {
        return reviewRepo.findAll();
    }

    public Optional<Review> getById(Long id) {
        return reviewRepo.findById(id);
    }

    public Review add(Review item) {

        return reviewRepo.save(item);
    }

    public Review update(Long id, Review updatedItem) {
        if (reviewRepo.existsById(id)) {
            updatedItem.setId(id);
            return reviewRepo.save(updatedItem);
        }
        return null;
    }

    public void delete(Long id) {
        reviewRepo.deleteById(id);
    }
}
