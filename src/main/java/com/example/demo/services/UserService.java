package com.example.demo.services;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAll() {
        return userRepo.findAll();
    }

    public Optional<User> getByEmail(String email) {
        return Optional.ofNullable(userRepo.findByEmail(email));
    }

    public User add(User user) {
        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public User update(String email, User updatedUser) {
        if (userRepo.existsByEmail(email)) {
            User existingUser = userRepo.findByEmail(email);

            // Only encode password if it's changed
            if (updatedUser.getPassword() != null &&
                    !updatedUser.getPassword().equals(existingUser.getPassword())) {
                updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            updatedUser.setEmail(email);
            return userRepo.save(updatedUser);
        }
        return null;
    }

    public void delete(String email) {
        if (userRepo.existsByEmail(email)) {
            userRepo.deleteByEmail(email);
        }
    }

    public boolean authenticate(String email, String rawPassword) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            return passwordEncoder.matches(rawPassword, user.getPassword());
        }
        return false;
    }
}