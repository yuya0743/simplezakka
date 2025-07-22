package com.example.simplezakka.service;

import com.example.simplezakka.dto.User.UserInfo;
import com.example.simplezakka.dto.User.UserRequest;
import com.example.simplezakka.dto.User.UserResponse;
import com.example.simplezakka.entity.Order;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private HttpSession session;
    private User1 user;
    private UserRequest userRequest;
    private UserInfo userInfo;

    @BeforeEach
    void setUp(String name, String password, String email, String address){
        session = new MockHttpSession();

        // 商品データ準備
        User1 user = new User1(); 
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        user.setAddress(address);
        userRepository.save(user);
    }

    @Test
    @DisplayName("ユーザー登録が成功すること")
    void register_WhenSucess() {
        // Arrange
               lenient().when(userRepository.save(any(User1.class))).thenAnswer(invocation -> {
            User1 userToSave = invocation.getArgument(0);
            if (userToSave.getUserId() == null) {
                userToSave.setUserId(123);
            }
            // Orderエンティティの addOrderDetail を使う場合、通常この関連設定は不要
            // orderToSave.getOrderDetails().forEach(detail ->
            // detail.setOrder(orderToSave));
            return userToSave;
        });

    }

}