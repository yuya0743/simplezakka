package com.example.simplezakka.dto.Login;

public class LoginInfo {
    private String name;      // 氏名を追加
    private String email;
    private String password;
    private String address;   // 住所を追加

    public LoginInfo() {
        // デフォルトコンストラクタ（必要）
    }

    
    // email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // address
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
