package com.example.simplezakka.controller;

import com.example.simplezakka.entity.User1;
import com.example.simplezakka.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User1 validUser;

    @BeforeEach
    void setUp() {
        validUser = new User1();
        validUser.setName("テストユーザー");
        validUser.setPassword("password123");
        validUser.setEmail("test@example.com");
        validUser.setAddress("東京都");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("正常系テスト")
    class SuccessTests {

        @Test
        @DisplayName("有効なユーザー情報で登録が成功すること")
        void registerUser_Success() throws Exception {
            doNothing().when(userService).registerUser(anyString(), anyString(), anyString(), anyString());

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUser)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("{\"message\":\"登録が完了しました！\"}"));

            verify(userService, times(1)).registerUser(
                    validUser.getName(),
                    validUser.getPassword(),
                    validUser.getEmail(),
                    validUser.getAddress()
            );
            verifyNoMoreInteractions(userService);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("異常系テスト - 各項目がnullの場合")
    class NullFieldTests {

        @Test
        @DisplayName("nameがnullの場合にエラーとなること（500 Internal Server Error）")
        void registerUser_WhenNameIsNull_ShouldReturnInternalServerError() throws Exception {
            User1 userWithNullName = new User1();
            userWithNullName.setName(null);
            userWithNullName.setPassword("password123");
            userWithNullName.setEmail("test@example.com");
            userWithNullName.setAddress("東京都");

            doThrow(new RuntimeException("Name cannot be null")).when(userService).registerUser(
                    eq(null), anyString(), anyString(), anyString()
            );

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userWithNullName)))
                    .andExpect(status().isInternalServerError());

            // 修正: すべての引数をマッチャーでラップ
            verify(userService, times(1)).registerUser(
                    eq(userWithNullName.getName()), // nullでもeq()でラップ
                    eq(userWithNullName.getPassword()),
                    eq(userWithNullName.getEmail()),
                    eq(userWithNullName.getAddress())
            );
            verifyNoMoreInteractions(userService);
        }

        @Test
        @DisplayName("passwordがnullの場合にエラーとなること（500 Internal Server Error）")
        void registerUser_WhenPasswordIsNull_ShouldReturnInternalServerError() throws Exception {
            User1 userWithNullPassword = new User1();
            userWithNullPassword.setName("テストユーザー");
            userWithNullPassword.setPassword(null);
            userWithNullPassword.setEmail("test@example.com");
            userWithNullPassword.setAddress("東京都");

            doThrow(new RuntimeException("Password cannot be null")).when(userService).registerUser(
                    anyString(), eq(null), anyString(), anyString()
            );

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userWithNullPassword)))
                    .andExpect(status().isInternalServerError());

            // 修正: すべての引数をマッチャーでラップ
            verify(userService, times(1)).registerUser(
                    eq(userWithNullPassword.getName()),
                    eq(userWithNullPassword.getPassword()), // nullでもeq()でラップ
                    eq(userWithNullPassword.getEmail()),
                    eq(userWithNullPassword.getAddress())
            );
            verifyNoMoreInteractions(userService);
        }

        @Test
        @DisplayName("emailがnullの場合にエラーとなること（500 Internal Server Error）")
        void registerUser_WhenEmailIsNull_ShouldReturnInternalServerError() throws Exception {
            User1 userWithNullEmail = new User1();
            userWithNullEmail.setName("テストユーザー");
            userWithNullEmail.setPassword("password123");
            userWithNullEmail.setEmail(null);
            userWithNullEmail.setAddress("東京都");

            doThrow(new RuntimeException("Email cannot be null")).when(userService).registerUser(
                    anyString(), anyString(), eq(null), anyString()
            );

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userWithNullEmail)))
                    .andExpect(status().isInternalServerError());

            // 修正: すべての引数をマッチャーでラップ
            verify(userService, times(1)).registerUser(
                    eq(userWithNullEmail.getName()),
                    eq(userWithNullEmail.getPassword()),
                    eq(userWithNullEmail.getEmail()), // nullでもeq()でラップ
                    eq(userWithNullEmail.getAddress())
            );
            verifyNoMoreInteractions(userService);
        }

        @Test
        @DisplayName("addressがnullの場合にエラーとなること（500 Internal Server Error）")
        void registerUser_WhenAddressIsNull_ShouldReturnInternalServerError() throws Exception {
            User1 userWithNullAddress = new User1();
            userWithNullAddress.setName("テストユーザー");
            userWithNullAddress.setPassword("password123");
            userWithNullAddress.setEmail("test@example.com");
            userWithNullAddress.setAddress(null);

            doThrow(new RuntimeException("Address cannot be null")).when(userService).registerUser(
                    anyString(), anyString(), anyString(), eq(null)
            );

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userWithNullAddress)))
                    .andExpect(status().isInternalServerError());

            // 修正: すべての引数をマッチャーでラップ
            verify(userService, times(1)).registerUser(
                    eq(userWithNullAddress.getName()),
                    eq(userWithNullAddress.getPassword()),
                    eq(userWithNullAddress.getEmail()),
                    eq(userWithNullAddress.getAddress()) // nullでもeq()でラップ
            );
            verifyNoMoreInteractions(userService);
        }
    }
}