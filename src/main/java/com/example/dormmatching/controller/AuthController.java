package com.example.dormmatching.controller;

import com.example.dormmatching.dto.LoginRequest;
import com.example.dormmatching.dto.SignUpRequest;
import com.example.dormmatching.dto.UserDto;
import com.example.dormmatching.entity.UserEntity;
import com.example.dormmatching.repository.UserRepository;
import com.example.dormmatching.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    /* ─────────────────────────────────────────
       1) 회원가입
       ───────────────────────────────────────── */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(@Valid @RequestBody SignUpRequest req) {
        try {
            /* 1‑1. 중복 ID/학번 체크 */
            if (userRepository.existsByIdentifier(req.identifier()))
                return conflict("Identifier is already in use");

            if (userRepository.existsByStudentNumber(req.studentNumber()))
                return conflict("Student number is already in use");

            /* 1‑2. 엔티티 생성 */
            UserEntity userEntity = UserEntity.builder()
                    .identifier(req.identifier())
                    .password(passwordEncoder.encode(req.password()))
                    .name(req.name())
                    .gender(req.gender())
                    .birthYear(req.birthYear())
                    .studentNumber(req.studentNumber())
                    .grade(req.grade())
                    .department(req.department())
                    .refreshToken(null)
                    .build();

            userRepository.save(userEntity);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("success", true,
                            "message", "User registered successfully",
                            "user", new UserDto(userEntity))
            );

        } catch (Exception e) {
            return serverError();
        }
    }

    /* ─────────────────────────────────────────
       2) 로그인
       ───────────────────────────────────────── */
    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signin(@Valid @RequestBody LoginRequest req) {
        try {
            Optional<UserEntity> userOpt = userRepository.findByIdentifier(req.identifier());
            if (userOpt.isEmpty() || !passwordEncoder.matches(req.password(), userOpt.get().getPassword()))
                return unauthorized("Invalid identifier or password");

            UserEntity userEntity = userOpt.get();

            String token = jwtService.generateToken(userEntity.getIdentifier());
            String refreshToken  = jwtService.generateRefreshToken(userEntity.getIdentifier());

            userEntity.setRefreshToken(refreshToken);
            userRepository.save(userEntity);

            return ResponseEntity.ok(
                    Map.of("success", true,
                            "message", "Login success",
                            "token", token,
                            "RT", refreshToken,
                            "user", new UserDto(userEntity))
            );

        } catch (Exception e) {
            return serverError();
        }
    }

    /* ─────────────────────────────────────────
       공통 응답 헬퍼
       ───────────────────────────────────────── */
    private ResponseEntity<Map<String, Object>> conflict(String msg) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("success", false, "message", msg));
    }

    private ResponseEntity<Map<String, Object>> unauthorized(String msg) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("success", false, "message", msg));
    }

    private ResponseEntity<Map<String, Object>> serverError() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Internal server error"));
    }
}
