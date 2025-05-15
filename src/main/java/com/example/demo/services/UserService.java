package com.example.demo.services;

import com.example.demo.entities.User;
import com.example.demo.entities.UserRole;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    /**
     * Get all users (admin function)
     */
    public List<User> getAll() {
        return userRepo.findAll();
    }

    /**
     * Get user by ID
     */
    public Optional<User> getById(Long id) {
        return userRepo.findById(id);
    }

    /**
     * Get user by email
     */
    public Optional<User> getByEmail(String email) {
        return Optional.ofNullable(userRepo.findByEmail(email));
    }

    /**
     * Get user by username
     */
    public Optional<User> getByUsername(String username) {
        return Optional.ofNullable(userRepo.findByUsername(username));
    }

    /**
     * Register a new user
     */
    @Transactional
    public User register(User user) {
        // Check if user exists
        if (userRepo.existsByEmail(user.getEmail()) ||
                userRepo.existsByUsername(user.getUsername())) {
            return null; // User already exists
        }

        // Set default role if not specified
        if (user.getRole() == null) {
            user.setRole(UserRole.USER);
        }

        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Encrypt password before saving
        user.setPassword(user.getPassword());

        return userRepo.save(user);
    }

    /**
     * Update user details
     */
    @Transactional
    public User update(Long id, User updatedUser) {
        User existingUser = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update only non-null fields
        if (updatedUser.getUsername() != null) {
            existingUser.setUsername(updatedUser.getUsername());
        }

        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }

        if (updatedUser.getAddress() != null) {
            existingUser.setAddress(updatedUser.getAddress());
        }

        // Only encode password if it's provided and different
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(updatedUser.getPassword());
        }

        existingUser.setUpdatedAt(LocalDateTime.now());
        return userRepo.save(existingUser);
    }

    /**
     * Change user role (admin function)
     */
    @Transactional
    public User changeRole(Long id, UserRole newRole) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setRole(newRole);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepo.save(user);
    }

    /**
     * Deactivate user (soft delete)
     */
    @Transactional
    public User deactivateUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepo.save(user);
    }

    /**
     * Reactivate user
     */
    @Transactional
    public User reactivateUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepo.save(user);
    }

    /**
     * Hard delete a user (admin function)
     */
    @Transactional
    public void delete(Long id) {
        userRepo.deleteById(id);
    }

    /**
     * Authenticate user
     */
    public boolean authenticate(String email, String rawPassword) {
        User user = userRepo.findByEmail(email);
        if (user != null && user.isActive()) {
            return rawPassword.compareTo(user.getPassword())==0 ;
        }
        return false;
    }

    /**
     * Generate password reset token (would typically email this to user)
     */
    @Transactional
    public String generatePasswordResetToken(String email) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            // In a real application, save this token to a separate table with expiration
            String resetToken = UUID.randomUUID().toString();
            // Here you would typically save the token and send an email
            return resetToken;
        }
        return null;
    }


}