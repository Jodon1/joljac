package com.example.dormmatching.service;

import com.example.dormmatching.dto.LoginRequest;
import com.example.dormmatching.entity.UserEntity;
import com.example.dormmatching.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    /* 로그인 */
    public void login(LoginRequest dto) {

        UserEntity userEntity = userRepo.findByIdentifier(dto.identifier())
                .orElseThrow(() -> new IllegalArgumentException("ID를 찾을 수 없습니다."));

        if (!encoder.matches(dto.password(), userEntity.getPassword()))
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
    }
}
