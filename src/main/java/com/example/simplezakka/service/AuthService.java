package com.example.simplezakka.service;

import com.example.simplezakka.dto.Login.LoginInfo;
import com.example.simplezakka.dto.User.UserInfo;
import com.example.simplezakka.entity.User1;
import com.example.simplezakka.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private User1 authenticatedUser;

    @Autowired
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

    // 🔧 修正ポイント：引数名と使い方
    public LoginInfo findUserByEmail(String email) {
        Optional<User1> userOpt = authRepository.findByEmail(email);
        return userOpt.map(this::convertToLogin).orElse(null);
    }

    public LoginInfo convertToLogin(User1 user) {
        return new LoginInfo(
                user.getEmail(),
                user.getPassword()
        );
    }
}
