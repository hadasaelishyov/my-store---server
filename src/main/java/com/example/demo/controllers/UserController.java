package com.example.demo.controllers;

import com.example.demo.entities.User;
import com.example.demo.entities.UserRole;
import com.example.demo.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    // Get all users (admin)
    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    // Get user by ID
    @GetMapping("/id/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getByEmail(@PathVariable String email) {
        return userService.getByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getByUsername(@PathVariable String username) {
        return userService.getByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User newUser = userService.register(user);
        if (newUser != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } else {
            // User already exists
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // Authenticate user
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String email,
            @RequestParam String password) {

        boolean authenticated = userService.authenticate(email, password);
        Map<String, Object> response = new HashMap<>();

        if (authenticated) {
            User user = userService.getByEmail(email).orElse(null);
            response.put("authenticated", true);
            response.put("user", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("authenticated", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.update(id, user));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Change user role (admin)
    @PatchMapping("/{id}/role")
    public ResponseEntity<User> changeRole(
            @PathVariable Long id,
            @RequestParam UserRole role) {
        try {
            return ResponseEntity.ok(userService.changeRole(id, role));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Deactivate user
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<User> deactivateUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.deactivateUser(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Reactivate user
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<User> reactivateUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.reactivateUser(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Request password reset
    @PostMapping("/password-reset/request")
    public ResponseEntity<Map<String, String>> requestPasswordReset(@RequestParam String email) {
        String resetToken = userService.generatePasswordResetToken(email);
        Map<String, String> response = new HashMap<>();

        if (resetToken != null) {
            response.put("message", "Password reset token generated successfully");
            // In a real application, you would email this token, not return it
            // For development purposes only
            response.put("token", resetToken);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Email not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Delete user (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}