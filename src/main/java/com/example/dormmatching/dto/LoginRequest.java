// LoginRequest.java
package com.example.dormmatching.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String studentNumber;

    @NotBlank
    private String password;
}
