package com.example.dormmatching.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class ApiSecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    /**
     * 2순위 필터 체인: “API(JWT) 전용” 보안 설정
     *   - 이 체인은 /api/** 와 Swagger UI 엔드포인트만 처리한다.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                // 이 체인이 적용될 URL 패턴을 제한
                .securityMatcher("/api/**", "/swagger-ui/**", "/v3/api-docs/**")

                // CSRF 비활성화 (REST API 용으로 설정)
                .csrf(csrf -> csrf.disable())

                // 세션을 생성하지 않는 STATELESS
                .sessionManagement(sm -> sm.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))

                // 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 허용할 엔드포인트(로그인, Swagger 등)
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // 그 외 모든 /api/** 요청은 JWT 검사 후 인증 필요
                        .requestMatchers("/api/**").authenticated()
                )
                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 등록
                .addFilterBefore(jwtFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
