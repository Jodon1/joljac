/* dto/UserDto.java  (응답 전용) */
package com.example.dormmatching.dto;

import com.example.dormmatching.entity.UserEntity;

public record UserDto(
        String identifier,
        String name,
        Integer grade,
        String studentNumber,
        String department
) {
    public UserDto(UserEntity u) {
        this(u.getIdentifier(), u.getName(), u.getGrade(), u.getStudentNumber(), u.getDepartment());
    }
}