package com.example.simplezakka.controller;

import com.example.simplezakka.dto.User.UserRequest;
import com.example.simplezakka.dto.User.UserResponse;
import com.example.simplezakka.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<UserResponse> placeUser(
            @Valid @RequestBody UserRequest UserRequest,
            HttpSession session) {
        
        try {
            UserResponse UserResponse = userService.registerUser(UserRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}