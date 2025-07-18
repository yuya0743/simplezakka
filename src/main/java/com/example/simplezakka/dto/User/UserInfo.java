package com.example.simplezakka.dto.User;

import org.hibernate.validator.constraints.Length;

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
    @Length(min = 4, max = 20, message = "Password must be between 4 and 20 characters")
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
