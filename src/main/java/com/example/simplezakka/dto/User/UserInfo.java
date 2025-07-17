package com.example.simplezakka.dto.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public class UserInfo {
    @NotBlank
    @Valid
    private String name;
    @NotBlank
    @Valid
    private String email;
    @NotBlank
    @Valid
    private String address;
    @NotBlank
    @Valid 
    private String password;

    // Getters and Setters
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
