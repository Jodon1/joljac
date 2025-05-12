package com.example.dormmatching.controller.manager;

import com.example.dormmatching.dto.RegisterRequest;
import com.example.dormmatching.dto.Response.ErrorResponse;
import com.example.dormmatching.dto.Response.SuccessResponse;
import com.example.dormmatching.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AuthService authService;

    @PostMapping("/users")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody RegisterRequest req) {
        try {
            authService.register(req);
            SuccessResponse<Void> body = new SuccessResponse<>(
                    HttpStatus.CREATED.value(),
                    true,
                    "사용자 생성이 완료되었습니다.",
                    null
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } catch (IllegalArgumentException e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "서버 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
