package com.example.simplezakka.dto.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {
    @Valid
    @NotNull(message = "ログイン情報は必須です")
    private UserInfo UserInfo;
    @NotBlank
    (message = "パスワードは必須です")private String password;
}


 