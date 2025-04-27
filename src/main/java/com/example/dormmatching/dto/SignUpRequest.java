package com.example.dormmatching.dto;

import com.example.dormmatching.entity.UserEntity;
import jakarta.validation.constraints.*;

public record SignUpRequest(
        @NotBlank String identifier,
        @NotBlank String password,
        @NotBlank String name,
        @NotNull  UserEntity.Gender gender,
        @NotNull  String birthYear,
        @NotNull  String studentNumber,
        @NotNull  Integer grade,
        String department
) {}
