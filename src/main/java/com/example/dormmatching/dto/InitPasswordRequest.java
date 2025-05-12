package com.example.dormmatching.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class InitPasswordRequest {
    @NotBlank
    private String password;  // 새 비밀번호만 받습니다.
}