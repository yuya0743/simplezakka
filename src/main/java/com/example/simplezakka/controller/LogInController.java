package com.example.simplezakka.controller;

import com.example.simplezakka.dto.Login.Logininfo;
import com.example.simplezakka.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

import javax.management.StringValueExp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/user")
public class LogInController {

    private final AuthService authService;

    public LogInController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Logininfo> login(@RequestBody Logininfo loginInfo, HttpSession session) {
        boolean success = authService.login(loginInfo.getEmail(), loginInfo.getPassword());
        if (success) {
            session.setAttribute("userEmail", loginInfo.getEmail()); 
            return ResponseEntity.ok(loginInfo);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/mypage")
    public ResponseEntity<Logininfo> myPage(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        if (email != null) {
            Logininfo info = authService.getUserInfoByEmail(email);
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


 

     // 商品の更新 
    @PutMapping("/mypageedit") 
    public ResponseEntity<Logininfo> updateProduct( 
        @PathVariable String email, 
        @Valid @RequestBody Logininfo logininfo) { 
            
            Logininfo updatedInfo = authService.updatelogininfo(email, logininfo); 
            if (updatedInfo == null) { 
                return ResponseEntity.notFound().build(); 
            } 
            
            return ResponseEntity.ok(updatedInfo); 
         } 
        }


