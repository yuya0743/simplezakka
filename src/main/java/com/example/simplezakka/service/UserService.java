package com.example.simplezakka.service; 
 
import com.example.simplezakka.dto.User.UserInfo;
import com.example.simplezakka.dto.User.UserRequest; 
import com.example.simplezakka.dto.User.UserResponse; 
import com.example.simplezakka.entity.User1; 
import com.example.simplezakka.entity.User1.User;
import com.example.simplezakka.repository.UserRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
 
@Service
public class UserService {
 
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

 
    @Transactional

    public UserResponse registerUser(UserRequest request) {
 

        UserInfo user = new UserInfo();


        user.setName(request.getUserInfo().getName());

        user.setEmail(request.getUserInfo().getEmail());

        user.setAddress(request.getUserInfo().getAddress());

        UserInfo savedUser = userRepository.save(user);
 

        return new UserResponse(savedUser.getUserId(), "会員登録が成功しました。");

    }
 

}
 