package com.example.dormmatching.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 성공 응답 전용 DTO
 * @param <T> 반환 데이터 타입
 */
@Getter
@AllArgsConstructor
public class SuccessResponse<T> {
    private int status;      // HTTP 상태 코드
    private boolean success; // 처리 성공 여부 (항상 true)
    private String message;  // 결과 메시지
    private T data;          // 실제 응답 데이터
}