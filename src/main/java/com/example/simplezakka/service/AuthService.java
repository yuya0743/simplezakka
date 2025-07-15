package com.example.simplezakka.service;

import com.example.simplezakka.dto.Login.Logininfo;
import com.example.simplezakka.entity.User1;
import com.example.simplezakka.repository.AuthRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private User1 authenticatedUser;
    
    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
        this.authenticatedUser = null;
    }

    public User1 getAuthenticatedUser() {
        return authenticatedUser;
    }

    public boolean login(String email, String password) {
        Optional<User1> userOptional = authRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User1 user = userOptional.get();
            if (user.getPassword().equals(password)) {
                this.authenticatedUser = user;
                return true;
            }
        }
        return false;
    }

    public boolean isAuthenticated() {
        return this.authenticatedUser != null;
    }

    public void logout() {
        this.authenticatedUser = null;
    }

    public Logininfo findUserByEmail(String email) {
        Optional<User1> userOpt = authRepository.findByEmail(email);
        return userOpt.map(this::convertToLogin).orElse(null);
    }

    public Logininfo convertToLogin(User1 user) {
        Logininfo info = new Logininfo();
        info.setName(user.getName());
        info.setEmail(user.getEmail());
        info.setPassword(user.getPassword());
        info.setAddress(user.getAddress());
        return info;
    }

    public Logininfo getUserInfoByEmail(String email) {
        Optional<User1> userOpt = authRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User1 user = userOpt.get();
            Logininfo info = new Logininfo();
            info.setName(user.getName());      
            info.setEmail(user.getEmail());
            info.setAddress(user.getAddress()); 
            return info;
        }
        return null;
    }

}
