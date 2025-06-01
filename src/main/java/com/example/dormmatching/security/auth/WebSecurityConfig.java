package com.example.dormmatching.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * 1순위 필터 체인: “웹(Thymeleaf) 전용” 보안 설정
     *   - 이 체인은 오직 /admin/** 와 정적 리소스만 처리한다.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        // 이 체인이 적용될 URL 패턴을 제한
        http
                .securityMatcher("/admin/**", "/css/**", "/js/**", "/images/**", "/logout", "/admin/login")
                //   ↑ 웹 로그인 및 대시보드, 로그아웃, 정적 리소스를 여기서만 처리

                // CSRF 비활성화 (필요에 따라 켤 수도 있음)
                .csrf(csrf -> csrf.disable())

                // 세션은 Stateful(기본값)
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스 및 로그인 페이지는 모두 허용
                        .requestMatchers(
                                "/admin/login",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // /admin/**(로그인 이후) 는 반드시 ADMIN 권한 필요
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                )
                // 폼 로그인 설정
                .formLogin(form -> form
                        .loginPage("/admin/login")             // GET: 로그인 폼
                        .loginProcessingUrl("/admin/login")    // POST: 로그인 폼 제출
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/admin/login?error")
                        .usernameParameter("username")
                        .passwordParameter("password")
                )
                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/admin/login?logout")
                        .invalidateHttpSession(true)
                )
                // UserDetailsService 지정 (로그인 인증 시 사용)
                .userDetailsService(userDetailsService);

        return http.build();
    }

    // AuthenticationManager 빈 등록 (폼 로그인에서 필요)
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
