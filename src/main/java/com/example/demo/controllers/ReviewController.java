package com.example.demo.controllers;

import com.example.demo.entities.Review;
import com.example.demo.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public List<Review> getAll() {
        return reviewService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Review> getById(@PathVariable Long id) {
        return reviewService.getById(id);
    }

    @PostMapping
    public Review add(@RequestBody Review review) {
        return reviewService.add(review);
    }

    @PutMapping("/{id}")
    public Review update(@PathVariable Long id, @RequestBody Review review) {
        return reviewService.update(id, review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }
}