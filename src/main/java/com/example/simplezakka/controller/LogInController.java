package com.example.simplezakka.controller;

import com.example.simplezakka.dto.Login.LoginInfo;
import com.example.simplezakka.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class LogInController {

    private final AuthService authService;

    public LogInController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginInfo> login(@RequestBody LoginInfo loginInfo) {
        boolean success = authService.login(loginInfo.getEmail(), loginInfo.getPassword());
        if (success) {
            return ResponseEntity.ok(loginInfo);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/mypage")
    public ResponseEntity<String> myPage(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        if (email != null) {
            return ResponseEntity.ok("ログイン中: " + email);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ログインしていません");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate(); 
        return ResponseEntity.ok("ログアウトしました");
    }
}

