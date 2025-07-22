package com.example.simplezakka.service; 

import com.example.simplezakka.dto.User.UserRequest; 
import com.example.simplezakka.dto.User.UserResponse; 
import com.example.simplezakka.entity.User1; 
import com.example.simplezakka.repository.UserRepository; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService{

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional

    public void registerUser(String name, String password, String email, String address) {
        User1 user = new User1();
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);  
        user.setAddress(address);
        userRepository.save(user);
    }
 

}
 