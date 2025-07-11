package com.example.simplezakka.service;

import com.example.simplezakka.dto.user.Loginrequest;
import com.example.simplezakka.dto.user.RegisterRequest;
import com.example.simplezakka.dto.user.AuthResponse;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
}
