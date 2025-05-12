package com.example.dormmatching.controller.auth;

import com.example.dormmatching.dto.*;
import com.example.dormmatching.dto.Response.ErrorResponse;
import com.example.dormmatching.dto.Response.SuccessResponse;
import com.example.dormmatching.service.auth.AuthService;
import com.example.dormmatching.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest req) {
        try {
            LoginResponse resp = authService.login(req);

            String msg = resp.isPasswordInitialized()
                    ? "로그인 성공"
                    : "init-password";

            // 2) 정상 로그인
            SuccessResponse<LoginResponse> body = new SuccessResponse<>(
                    HttpStatus.OK.value(),
                    true,
                    msg,
                    resp
            );
            return ResponseEntity.ok(body);

        } catch (BadCredentialsException e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    false,
                    "아이디 또는 비밀번호가 올바르지 않습니다."
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "서버 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    "Refresh Token 헤더가 없습니다."
            );
            return ResponseEntity.badRequest().body(error);
        }
        String refreshToken = header.substring(7);
        if (!jwtService.validateRefreshToken(refreshToken)) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    false,
                    "Refresh Token이 유효하지 않습니다."
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        String subject   = jwtService.extractIdentifier(refreshToken);
        String newAccess = jwtService.generateToken(subject);
        boolean init = true;
        LoginResponse data = new LoginResponse(newAccess, refreshToken, init);
        SuccessResponse<LoginResponse> body = new SuccessResponse<>(
                HttpStatus.OK.value(),
                true,
                "토큰 재발급 성공",
                data
        );
        return ResponseEntity.ok(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            // 토큰 자체가 없으면 400 Bad Request
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(
                            HttpStatus.BAD_REQUEST.value(),
                            false,
                            "Authorization 헤더에 Bearer 토큰을 포함해 주세요."
                    ));
        }
        String token = header.substring(7);
        if (!jwtService.validateToken(token)) {
            // 토큰이 올바르지 않거나 만료되었으면 401 Unauthorized
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(
                            HttpStatus.UNAUTHORIZED.value(),
                            false,
                            "유효하지 않은 액세스 토큰입니다."
                    ));
        }

        // 토큰이 유효하면 subject(학번)를 꺼내서 로그아웃 수행
        String studentNumber = jwtService.extractIdentifier(token);
        try {
            authService.logout(studentNumber);
            return ResponseEntity.ok(new SuccessResponse<>(
                    HttpStatus.OK.value(),
                    true,
                    "로그아웃 되었습니다.",
                    null
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(
                            HttpStatus.BAD_REQUEST.value(),
                            false,
                            e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "서버 오류가 발생했습니다."
                    ));
        }
    }

}
