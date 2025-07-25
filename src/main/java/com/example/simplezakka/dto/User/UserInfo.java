package com.example.simplezakka.dto.User;

import jakarta.validation.constraints.NotBlank; 

public class UserInfo {
    @NotBlank(message = "名前を入れてください。")
    private String name;

    @NotBlank(message = "メールアドレスを入れてください。") 
    private String email;

    @NotBlank(message = "住所を入れてください。")
    private String address;

    @NotBlank(message = "パスワードを入れてください。")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}