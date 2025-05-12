package com.example.demo.repositories;

import com.example.demo.entities.Cart;
import com.example.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByPhone(String phone);
    List<User> findByAddress(String address);
    User findByUsernameAndPassword(String username, String password);
    boolean existsByEmail(String email);

    void deleteByEmail(String email);
}
