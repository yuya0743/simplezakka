package com.example.simplezakka.controller;

import com.example.simplezakka.dto.Login.LoginInfo;
import com.example.simplezakka.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class LogInController {

    private final AuthService loginService;

    public LogInController(AuthService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginInfo loginInfo) {
        boolean success = loginService.login(loginInfo.getEmail(), loginInfo.getPassword());
        if (success) {
            return ResponseEntity.ok("ログイン成功");
        } else {
            return ResponseEntity.status(401).body("メールアドレスまたはパスワードが間違っています");
        }
    }
}
