package com.example.demo.controller;

import com.example.demo.entities.Product;
import com.example.demo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/products")
public class AdminController {

    @Autowired
    private AdminService adminService;


    @GetMapping("/get")
    public String hello()
    {
        return "hello from controller";
    }

    @GetMapping("/getall")
    public List<Product> getProducts()
    {

        return adminService.getAllProduct();
    }


//    @GetMapping("/getid/{id}")
//    public   Book getById(@PathVariable long id)
//    {
//        return books.stream().filter(book -> book.getId() == id).findFirst().orElse(null);
//    }

    @PostMapping("/add")
    public  void addBook(@RequestBody Product product)
    {
        // TODO  בדיקות תקינות
        adminService.addProduct(product);
    }


}
