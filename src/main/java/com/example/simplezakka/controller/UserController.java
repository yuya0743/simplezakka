package com.example.simplezakka.controller;

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
@RequestMapping
public class UserController {

    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;

    }
    
   @PostMapping(value = "/api/users", consumes = "application/json")
    @ResponseBody
    public String registerUserJson(@RequestBody User1 user) {
    // JSONデータを受け取り、ユーザーを登録する処理
    userService.registerUser(user.getName(), user.getPassword(), user.getEmail(), user.getAddress());
    return "{\"message\":\"登録が完了しました！\"}";
}
}