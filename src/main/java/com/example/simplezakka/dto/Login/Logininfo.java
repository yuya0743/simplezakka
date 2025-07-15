package com.example.simplezakka.dto.Login;

public class LoginInfo {
    private String email;
    private String password;

    public LoginInfo() {
        // デフォルトコンストラクタ（必要）
    }

    // ✅ このコンストラクタを追加
    public LoginInfo(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter & Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
