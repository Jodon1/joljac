package com.example.dormmatching.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse<T> {
    private int status;      // HTTP 상태 코드
    private boolean success; // 처리 성공 여부
    private String message;  // 결과 메시지
}
