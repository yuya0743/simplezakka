package com.example.simplezakka.service;

import com.example.simplezakka.dto.User.UserInfo;
import com.example.simplezakka.dto.User.UserRequest;
import com.example.simplezakka.dto.User.UserResponse;
import com.example.simplezakka.entity.User1;
import com.example.simplezakka.repository.UserRepository;
import jakarta.servlet.http.HttpSession;

import org.h2.engine.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional; 
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

     private HttpSession session;
    private User1 user;
    private UserRequest userRequest;
    private UserInfo userInfo;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();

        // 商品データ準備
        User1 user = new User1(); 
        user.setName("山下");
        user.setPassword("0000");
        user.setEmail("yama@gmail.com");
        user.setAddress("東京都");
public class UserServiceTest {
    
}
