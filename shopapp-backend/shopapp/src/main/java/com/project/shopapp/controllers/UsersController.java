package com.project.shopapp.controllers;

import com.project.shopapp.models.Product;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.responses.UserResponse;
import com.project.shopapp.services.UserServive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UsersController {
    private final UserServive userServive;


    @GetMapping("")
    public ResponseEntity<?> getAllProducts() {
        try {
            List<User> existingUser = userServive.findAll();
            List<UserResponse> userResponses = UserResponse.fromUsers(existingUser);
            return ResponseEntity.ok(userResponses);
        } catch (Exception e) {
            // Logging the exception might also be a good idea
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching products: " + e.getMessage());
        }
    }
}
