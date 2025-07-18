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

    /* public UserResponse registerUser(UserRequest request) {


        User1 user = new User1();


        user.setName(request.getUserInfo().getName());

        user.setEmail(request.getUserInfo().getEmail());

        user.setAddress(request.getUserInfo().getAddress());

        user.setPassword(request.getUserInfo().getPassword());

        User1 savedUser = userRepository.save(user);
 

        return new UserResponse(savedUser.getUserId(), "会員登録が成功しました。");

    }*/

    public void registerUser(String name, String password, String email, String address) {
        User1 user = new User1();
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);      // ★ここ重要
        user.setAddress(address);
        // 必要に応じて他の初期値もセット
        userRepository.save(user);
    }
 

}
 