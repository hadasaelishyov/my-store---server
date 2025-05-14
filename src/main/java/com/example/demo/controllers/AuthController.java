package com.example.demo.controllers;

import com.example.demo.dto.UserLoginDto;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.entities.User;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto loginDto) {
        boolean isAuthenticated = userService.authenticate(loginDto.getEmail(), loginDto.getPassword());

        if (isAuthenticated) {
            User user = userService.getByEmail(loginDto.getEmail()).orElse(null);
            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("email", user.getEmail());
                response.put("username", user.getUsername());
                response.put("isAdmin", user.getRole());
                // You could add JWT token here for proper authentication
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        if (userService.getByEmail(registrationDto.getEmail()).isPresent()) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }

        // Convert DTO to User entity
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(registrationDto.getPassword());
        user.setPhone(registrationDto.getPhone());
        user.setAddress(registrationDto.getAddress());
        user.setRole(registrationDto.());

        User newUser = userService.add(user);

        // Don't return password in response
        newUser.setPassword(null);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}