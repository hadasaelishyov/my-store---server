package com.example.demo.services;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    public List<User> getAll() {
        return userRepo.findAll();
    }

    public Optional<User> getByEmail(String email) {
        return Optional.ofNullable(userRepo.findByEmail(email));
    }

    public User add(User user) {
        return userRepo.save(user);
    }

    public User update(String email, User updatedUser) {
        if (userRepo.existsByEmail(email)) {
            updatedUser.setEmail(email); // מוודאים שה-ID נשמר
            return userRepo.save(updatedUser);
        }
        return null;
    }

    public void delete(String email) {
        if (userRepo.existsByEmail(email)) {
            userRepo.deleteByEmail(email);
        }
    }
}
