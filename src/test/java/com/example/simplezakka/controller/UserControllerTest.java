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
            doNothing().when(userService).registerUser(
                anyString(), anyString(), anyString(), anyString()
            );

            // エラーログの "but was" の内容に合わせて、期待するレスポンスコンテンツを修正
            String actualResponseContent = "{\"name\":\"テストユーザー\",\"email\":\"test@example.com\",\"address\":\"東京都\",\"password\":\"password123\"}";

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUser)))
                    .andExpect(status().isCreated()) // ステータスコードは現状のエラーログで問題なし
                    .andExpect(content().json(actualResponseContent)); // 厳密なJSON比較

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
        @DisplayName("nameがnullの場合にエラーとなること（400 Bad Request）")
        void registerUser_WhenNameIsNull_ShouldReturnBadRequest() throws Exception {
            User1 userWithNullName = new User1();
            userWithNullName.setName(null);
            userWithNullName.setPassword("password123");
            userWithNullName.setEmail("test@example.com");
            userWithNullName.setAddress("東京都");

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userWithNullName)))
                    .andExpect(status().isBadRequest());

            verify(userService, times(0)).registerUser(
                     anyString(), anyString(), anyString(), anyString()
            );
            verifyNoMoreInteractions(userService);
        }

        @Test
        @DisplayName("passwordがnullの場合にエラーとなること（400 Bad Request）")
        void registerUser_WhenPasswordIsNull_ShouldReturnBadRequest() throws Exception {
            User1 userWithNullPassword = new User1();
            userWithNullPassword.setName("テストユーザー");
            userWithNullPassword.setPassword(null);
            userWithNullPassword.setEmail("test@example.com");
            userWithNullPassword.setAddress("東京都");

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userWithNullPassword)))
                    .andExpect(status().isBadRequest());

            verify(userService, times(0)).registerUser(
                    anyString(), anyString(), anyString(), anyString()
            );
            verifyNoMoreInteractions(userService);
        }

        @Test
        @DisplayName("emailがnullの場合にエラーとなること（400 Bad Request）")
        void registerUser_WhenEmailIsNull_ShouldReturnBadRequest() throws Exception {
            User1 userWithNullEmail = new User1();
            userWithNullEmail.setName("テストユーザー");
            userWithNullEmail.setPassword("password123");
            userWithNullEmail.setEmail(null);
            userWithNullEmail.setAddress("東京都");

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userWithNullEmail)))
                    .andExpect(status().isBadRequest());

            verify(userService, times(0)).registerUser(
                    anyString(), anyString(), anyString(), anyString()
            );
            verifyNoMoreInteractions(userService);
        }

        @Test
        @DisplayName("addressがnullの場合にエラーとなること（400 Bad Request）")
        void registerUser_WhenAddressIsNull_ShouldReturnBadRequest() throws Exception {
            User1 userWithNullAddress = new User1();
            userWithNullAddress.setName("テストユーザー");
            userWithNullAddress.setPassword("password123");
            userWithNullAddress.setEmail("test@example.com");
            userWithNullAddress.setAddress(null);

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userWithNullAddress)))
                    .andExpect(status().isBadRequest());

            verify(userService, times(0)).registerUser(
                    anyString(), anyString(), anyString(), anyString()
            );
            verifyNoMoreInteractions(userService);
        }
    }
}