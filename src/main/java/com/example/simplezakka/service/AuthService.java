package com.example.simplezakka.service;

import com.example.simplezakka.dto.Login.LoginInfo;
import com.example.simplezakka.dto.Login.UserInfo;
import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private User authenticatedUser;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticatedUser = null;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public boolean login(String email, String password) {
        Optional<User> userOptional = userRepository.findById(email);  

        if (userOptional.isPresent()) {
            User user = userOptional.get();
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

     public LoginInfo finduserByEmail(String email) {
        Optional<User> userOpt = userRepository.findById(email);
        return userOpt.map(this::convertToLogin).orElse(null);
    }

    public LoginInfo convertToLogin(User user) {
        return new LoginInfo(
                user.getEmail(),
                user.getPassword()  
        );
    }
}
