package com.example.simplezakka.service;

import com.example.simplezakka.dto.Login.Logininfo;
import com.example.simplezakka.entity.User1;
import com.example.simplezakka.repository.AuthRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthRepository authRepository;

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public boolean login(String email, String password) {
        Optional<User1> userOptional = authRepository.findByEmail(email);
        return userOptional.map(user -> user.getPassword().equals(password)).orElse(false);
    }

    public Logininfo getUserInfoByEmail(String email) {
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
}
