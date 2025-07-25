package com.example.simplezakka.service;


import com.example.simplezakka.dto.Login.LoginInfo;
import com.example.simplezakka.entity.User1;
import com.example.simplezakka.repository.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
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
        when(authRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

        boolean result = authService.login("unknown@email.com", "password");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("getUserInfoByEmail: ユーザーが存在する場合、DTOに変換された情報を返す")
    void getUserInfoByEmail_WhenUserExists_ShouldReturnLoginInfo() {
        when(authRepository.findByEmail("user@email.com"))
            .thenReturn(Optional.of(existingUser));

        LoginInfo result = authService.getUserInfoByEmail("user@email.com");

        assertThat(result.getEmail()).isEqualTo("user@email.com");
        assertThat(result.getPassword()).isEqualTo("password");
        assertThat(result.getName()).isEqualTo("ユーザー");
        assertThat(result.getAddress()).isEqualTo("東京都港区");
    }
    
    @Test
    @DisplayName("getUserInfoByEmail: ユーザーが存在しない場合、nullを返す")
    void getUserInfoByEmail_ShouldReturnNull_WhenUserDoesNotExist() {
        when(authRepository.findByEmail("unknown@email.com"))
            .thenReturn(Optional.empty());
            
        LoginInfo result = authService.getUserInfoByEmail("unknown@email.com");

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("convertToLogin: User1 から Logininfo に正しく変換される")
    void convertToLogin_ShouldReturnLogin() {
        LoginInfo result = authService.convertToLogin(existingUser);

        assertThat(result.getEmail()).isEqualTo("user@email.com");
        assertThat(result.getPassword()).isEqualTo("password");
        assertThat(result.getName()).isEqualTo("ユーザー");
        assertThat(result.getAddress()).isEqualTo("東京都港区");
    }
}