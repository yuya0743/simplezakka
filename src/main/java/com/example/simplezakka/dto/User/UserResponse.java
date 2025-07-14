package com.example.simplezakka.dto.User; 
 
import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;
 
import java.time.LocalDateTime;
 
@Data

@NoArgsConstructor

@AllArgsConstructor

public class UserResponse { 

    private Integer userId; 

    private String message;

    private LocalDateTime registrationDate; 

    

}
 