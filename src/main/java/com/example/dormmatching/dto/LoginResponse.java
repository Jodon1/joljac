package com.example.dormmatching.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private boolean passwordInitialized;

    public LoginResponse(String accessToken, String refreshToken, boolean passwordInitialized) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.passwordInitialized = passwordInitialized;
    }

}