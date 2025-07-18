package com.example.simplezakka.service;

import com.example.simplezakka.dto.User.UserInfo;
import com.example.simplezakka.dto.Login.Logininfo;
import com.example.simplezakka.entity.User1;
import com.example.simplezakka.repository.AuthRepository;
import com.example.simplezakka.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections; // 空のリスト用
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple; // tupleを使った検証用
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Autowired
    private AuthService authService;

    @InjectMocks
    private AuthService authService;

    private User1 existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User1();
        existingUser.setEmail("user@email.com");
        existingUser.setPassword("password");
        existingUser.setName("ユーザー");
        existingUser.setAddress("東京都港区");
    }

    @Test
    @DisplayName("Login: 成功時にtrueを返す")
    void Login_WhenSucess_ShouldReturnTrue() {
        when(authRepository.findByEmail("user@email.com")).thenReturn(Optional.of(existingUser));

        boolean result = authService.login("user@email.com", "password");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Login: パスワード不一致時にfalseを返す")
    void Login_WhenPasswordIsWrong_ShouldReturnFalse() {
        when(authRepository.findByEmail("user@email.com")).thenReturn(Optional.of(existingUser));

        boolean result = authService.login("user@email.com", "mismatch");

        assertThat(result).isFalse();
    }
    

    @Test
    @DisplayName("Login: ユーザーが存在しない場合、falseを返す")
    void Login_WhenUserNotExist_ShouldReturnFalse() {
        when(authRepository.findByEmail("user@email.com")).thenReturn(Optional.of(existingUser));

        boolean result = authService.login("unknown@email.com", "password");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("getUserInfoByEmail: ユーザーが存在する場合、DTOに変換された情報を返す")
    void getUserInfoByEmail_WhenUserExists_ShouldReturnLoginInfo() {
        when(authRepository.findByEmail("user@email.com"))
            .thenReturn(Optional.of(existingUser));

        Optional<User1> result = authService.getUserInfoByEmail("user@emali.com");

            // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("user@email.com");
        assertThat(result.get().getPassword()).isEqualTo("password");
        assertThat(result.get().getName()).isEqualTo("ユーザー");
        assertThat(result.get().getAddress()).isEqualTo("東京都港区");
    }
    
    @Test
    @DisplayName("getUserInfoByEmail: ユーザーが存在しない場合、nullを返す")
    void getUserInfoByEmail_ShouldReturnNull_WhenUserDoesNotExist() {
        when(authRepository.findByEmail("user@email.com"))
            .thenReturn(Optional.of(existingUser));
            
        Optional.empty() result = authService.getUserInfoByEmail("unknown@emali.com");
    
        assertThat(result).isNull();

    }

    @Test
    @DisplayName("convertToLogin: User1 から Logininfo に正しく変換される")
    void convertToLogin_ShouldReturnLogin() {
    }
}