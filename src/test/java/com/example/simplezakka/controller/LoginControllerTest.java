package com.example.simplezakka.controller;
import com.example.simplezakka.service.AuthService;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.example.simplezakka.dto.Login.Logininfo;

import jakarta.servlet.http.HttpSession;
import com.example.simplezakka.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions; // ResultActions用

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

@WebMvcTest(LogInController.class) // LogInControllerのテスト
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTPリクエストをシミュレート

    @Autowired
    private ObjectMapper objectMapper; // JSON <-> Object 変換

    @MockBean // Service層のモック
    private AuthService authService;

    private Logininfo successLogininfo;
    private Logininfo failLogininfo;
    private MockHttpSession mockSession;

    @BeforeEach
    void setUp() {
        // --- テストデータ準備 ---
        successLogininfo = new Logininfo("テスト太郎","success@sample.com","password","東京都新宿区1-1-1");
        failLogininfo = new Logininfo("テスト花子","fail@sample.com","wrongpassword","東京都新宿区2-2-2");

        lenient().when(authService.getUserInfoByEmail("success@sample.com")).thenReturn(successLogininfo);
        lenient().when(authService.getUserInfoByEmail("fail@sample.com")).thenReturn(failLogininfo);
        lenient().when(authService.login(successLogininfo.getEmail(), successLogininfo.getPassword())).thenReturn(true);
        lenient().when(authService.login(failLogininfo.getEmail(), failLogininfo.getPassword())).thenReturn(false);
    // 他のメソッドはテストごとに when で設定
    }


 
@Nested
public class GetMypageTest {
    @DisplayName("GET /api/mypage")
    class GetMypageSuccessTests {
        @Test
        @DisplayName("emailで登録されているユーザが存在する場合、登録情報を返す")
        void getmypage_WhenUserExists_ShouldReturnLoginInfotWithStatusOk() throws Exception {
            // Arrange
            MockHttpSession mockSession = new MockHttpSession();
            
            String email = (String) mockSession.getAttribute("userEmail");
            
            when(mockSession.getAttribute("userEmail")).thenReturn("success@sample.com");
            
            // Act & Assert
           mockMvc.perform(get("/api/mypage")
                            .session(mockSession)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name", is(successLogininfo.getName())))
                    .andExpect(jsonPath("$.email", is(successLogininfo.getEmail())))
                    .andExpect(jsonPath("$.password", is(successLogininfo.getPassword())))
                    .andExpect(jsonPath("$.address", is(successLogininfo.getAddress())));

         
        }
        @Test
        void getMypage_WhenEmailIsNull_ShouldReturnHTTPStatusNOT_FOUND() throws Exception {
            // Arrange
            MockHttpSession mockSession = new MockHttpSession();
            
            String email = isNull();
            
            when(mockSession.getAttribute("userEmail")).thenReturn("success@sample.com");
            
            // Act & Assert
           mockMvc.perform(get("/api/mypage")
                            .session(mockSession)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    ;

         
        }

         @Test
        @DisplayName("セッションにカートが存在する場合、カート情報を200 OKで返す")
        void getMypage_WhenEmailIsNull_ShouldReturnHTTPStatusUNAUTHORIZED() throws Exception {
            // Arrange
            MockHttpSession mockSession = new MockHttpSession();
            
            String email = (String) mockSession.getAttribute("userEmail");
            
            when(mockSession.getAttribute("userEmail")).thenReturn("fail@sample.com");
            
            // Act & Assert
           mockMvc.perform(get("/api/mypage")
                            .session(mockSession)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name", is(failLogininfo.getName())))
                    .andExpect(jsonPath("$.email", is(failLogininfo.getEmail())))
                    .andExpect(jsonPath("$.password", is(failLogininfo.getPassword())))
                    .andExpect(jsonPath("$.address", is(failLogininfo.getAddress())));

         
        }
    
    } // End of GetMypageTest
    

@Nested
public class LoginTest{
    @DisplayName("GET /api/login")
    class GetLoginSuccessTests{
    @Test
        @DisplayName("emailで登録されているユーザが存在する場合、ログイン成功を返す")
        void postlogin_WhenLoginExists_ShouldReturnLoginWithStatusOk() throws Exception {
            MockHttpSession mockSession = new MockHttpSession();
            Logininfo loginInfo = successLogininfo;
           
           
            mockMvc.perform(get("/api/login")
                            .session(mockSession)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   
                    .andExpect(jsonPath("$.email", is(successLogininfo.getEmail())))
                    .andExpect(jsonPath("$.password", is(successLogininfo.getPassword())));
                    


     }
     @Test
        @DisplayName("emailで登録されていないユーザがログインしようとした場合、ログイン失敗を返す")
     
         
        void postlogin_WhenLoginNotExists_ShouldReturnLoginWithStatusUNAUTHORIZED() throws Exception {
            MockHttpSession mockSession = new MockHttpSession();
            Logininfo loginInfo = failLogininfo;
           
           
            mockMvc.perform(get("/api/login")
                            .session(mockSession)
                            .accept(MediaType.APPLICATION_JSON))
                            
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   
                    .andExpect(jsonPath("$.email", is(failLogininfo.getEmail())))
                    .andExpect(jsonPath("$.password", is(failLogininfo.getPassword())));
                    
     }
    }
    @Nested
 public class LogoutTest   {
    @DisplayName("GET /api/logout")
    class LogoutTests {
        @Test
        @DisplayName("ログアウト成功時、セッションを無効化し、200 OKを返す")
        void logout_WithActiveSession_ShouldInvalidateSessionAndReturnOk() throws Exception {
            MockHttpSession mockSession = new MockHttpSession();
             mockMvc.perform(get("/api/logout")
                            .session(mockSession)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   
                    .andExpect(request().sessionAttributeDoesNotExist("userEmail"));
                    
                    
                    

            }

        }

    }
}
}
}
