package com.example.simplezakka.service; 
 
import com.example.simplezakka.dto.User.UserRequest; 
import com.example.simplezakka.dto.User.UserResponse; 
import com.example.simplezakka.entity.User1; 
import com.example.simplezakka.entity.User1.User;
import com.example.simplezakka.repository.UserRepository; /
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
 
@Service

public class UserService {
 
    private final UserRepository userRepository;
 
    @Transactional

    public UserResponse registerUser(UserRequest request) {
 

        User1 user = new User1();

        user.setName(request.getUserInfo().getName());

        user.setmail(request.getUserInfo().getEmail());

        user.setAddress(request.getUserInfo().getAddress());

        user.setRegistrationDate(LocalDateTime.now());


        User1 savedUser = userRepository.save(user);
 

        return new RegisterResponse(savedUser.getUserId(), "会員登録が成功しました。", savedUser.getRegistrationDate());

    }
 

}
 