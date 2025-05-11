package com.example.dormmatching.security.auth;

import com.example.dormmatching.entity.UserEntity;
import com.example.dormmatching.repository.UserRepository;
import com.example.dormmatching.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        if (!jwtService.validateToken(token)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Access token is missing or invalid");
            return;
        }

        String identifier = jwtService.extractIdentifier(token);
        if (identifier == null) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid token: no identifier");
            return;
        }

        Optional<UserEntity> opt = userRepository.findById(identifier);
        if (opt.isEmpty()) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND,
                    "User not found");
            return;
        }

        UserEntity user = opt.get();
        // UserEntity에 banned/role이 없으므로 추가 검사는 생략합니다.

        // 인증 객체 생성 (권한은 필요에 따라 빈 리스트로)
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        user, null, new CustomUserDetails(user).getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource()
                .buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse res, int status, String msg) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        String body = String.format("{\"success\":false,\"message\":\"%s\"}", msg);
        try (PrintWriter w = res.getWriter()) {
            w.write(body);
        }
    }
}
