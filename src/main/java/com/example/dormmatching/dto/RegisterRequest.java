package com.example.dormmatching.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    private String studentNumber;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotNull
    private Integer statusId;

    @NotNull
    private Integer roleId;

    @NotNull
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthDate;

    @NotBlank
    @Pattern(regexp = "M|F", message = "성별은 'M' 또는 'F' 중 하나여야 합니다.")
    private String gender;

    @NotBlank
    private String address;

    private Boolean international = false;

    @NotBlank
    @Pattern(regexp = "^\\d{10,11}$", message = "휴대폰 번호는 10~11자리 숫자만 입력해주세요.")
    private String phoneNumber;

    @NotNull
    private Integer departmentId;

    @NotNull
    private Integer grade;    // CSV에서 받아올 학년 정보
}
