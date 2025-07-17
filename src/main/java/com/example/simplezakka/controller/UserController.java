package com.example.simplezakka.controller;

import com.example.simplezakka.dto.User.UserInfo;
import com.example.simplezakka.dto.User.UserRequest;
import com.example.simplezakka.dto.User.UserResponse;
import com.example.simplezakka.entity.User1;
import com.example.simplezakka.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;

    }
    
   @PostMapping(value = "/users", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<UserInfo>registerUserJson(@RequestBody UserInfo user, HttpSession session) {
        userService.registerUser(user.getName(), user.getPassword(), user.getEmail(), user.getAddress());
        session.setAttribute("userinfo", user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

}