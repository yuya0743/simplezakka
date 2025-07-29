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
    public ResponseEntity<LoginInfo> login(@RequestBody LoginInfo loginInfo, HttpSession session) {
        boolean success = authService.login(loginInfo.getEmail(), loginInfo.getPassword());
        if (success) {
            session.setAttribute("userEmail", loginInfo.getEmail()); 
            return ResponseEntity.ok(loginInfo);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/mypage")
    public ResponseEntity<LoginInfo> myPage(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        if (email != null) {
            LoginInfo info = authService.getUserInfoByEmail(email);
            if (info != null) {
                return ResponseEntity.ok(info);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("ログアウトしました");
    }
}

