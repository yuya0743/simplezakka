package com.example.simplezakka.service;

import com.example.simplezakka.dto.User.UserInfo;
import com.example.simplezakka.dto.Login.Logininfo;
import com.example.simplezakka.entity.User1;
import com.example.simplezakka.repository.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private AuthService authService;

    private User1 existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User1();
        existingUser.setEmail("test@example.com");
        existingUser.setPassword("password123");
        existingUser.setName("テストユーザー");
        existingUser.setAddress("東京都");
    }

    @Test
    @DisplayName("Login: 成功時にtrueを返す")
    void Login_WhenSucess_ShouldReturnTrue() {
        when(authRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(existingUser));

        boolean result = authService.login("test@example.com", "password123");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Login: パスワード不一致時にfalseを返す")
    void Login_WhenPasswordIsWrong_ShouldReturnFalse() {
        when(authRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(existingUser));

        boolean result = authService.login("test@example.com", "wrongpassword");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Login: ユーザーが存在しない場合、falseを返す")
    void Login_WhenUserNotExist_ShouldReturnFalse() {
        when(authRepository.findByEmail("notfound@example.com"))
            .thenReturn(Optional.empty());

        boolean result = authService.login("notfound@example.com", "any");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("getUserInfoByEmail: ユーザーが存在する場合、DTOに変換された情報を返す")
    void getUserInfoByEmail_WhenUserExists_ShouldReturnLoginInfo() {
        when(authRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(existingUser));

        Logininfo result = authService.getUserInfoByEmail("test@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(existingUser.getEmail());
        assertThat(result.getPassword()).isEqualTo(existingUser.getPassword());
        assertThat(result.getName()).isEqualTo(existingUser.getName());
        assertThat(result.getAddress()).isEqualTo(existingUser.getAddress());

        verify(authRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("getUserInfoByEmail: ユーザーが存在しない場合、nullを返す")
    void getUserInfoByEmail_ShouldReturnNull_WhenUserDoesNotExist() {
        when(authRepository.findByEmail("notfound@example.com"))
            .thenReturn(Optional.empty());

        Logininfo result = authService.getUserInfoByEmail("notfound@example.com");

        assertThat(result).isNull();

        verify(authRepository, times(1)).findByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("convertToLogin: User1 から Logininfo に正しく変換される")
    void convertToLogin_ShouldReturnLogin() {
        Logininfo result = authService.convertToLogin(existingUser);

        assertThat(result.getEmail()).isEqualTo(existingUser.getEmail());
        assertThat(result.getPassword()).isEqualTo(existingUser.getPassword());
        assertThat(result.getName()).isEqualTo(existingUser.getName());
        assertThat(result.getAddress()).isEqualTo(existingUser.getAddress());
    }
}
