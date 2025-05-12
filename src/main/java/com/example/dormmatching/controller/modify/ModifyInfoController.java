package com.example.dormmatching.controller.modify;
import com.example.dormmatching.dto.InitPasswordRequest;
import com.example.dormmatching.dto.Response.ErrorResponse;
import com.example.dormmatching.dto.Response.SuccessResponse;
import com.example.dormmatching.entity.user.User;
import com.example.dormmatching.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/modify")
@RequiredArgsConstructor
public class ModifyInfoController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/init-password")
    public ResponseEntity<?> initPassword(
            @Valid @RequestBody InitPasswordRequest req,
            Authentication authentication) {
        try {
            // 1) 인증된 사용자 식별
            String studentNumber = authentication.getName();

            // 2) User 엔티티 로드
            User user = userRepository.findByStudentNumber(studentNumber)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            // 4) 비밀번호 변경 및 플래그 세팅
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            user.setPasswordInitialized(true);
            userRepository.save(user);

            // 5) 응답
            return ResponseEntity.ok(new SuccessResponse<>(
                    200, true, "비밀번호가 성공적으로 변경되었습니다.", null
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(400, false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(new ErrorResponse(500, false, "서버 오류가 발생했습니다."));
        }
    }
}