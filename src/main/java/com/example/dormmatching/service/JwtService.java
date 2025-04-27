package com.example.dormmatching.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private static final String SECRET_KEY = "your-secure-secret-key-your-secure-secret-key";
    private static final String REFRESH_SECRET_KEY = "your-refresh-token-secret-key-your-refresh-token-secret-key";
    private static final long EXPIRATION_TIME = 3600000; // 1시간
    private static final long REFRESH_EXPIRATION_TIME = 1209600000; // 14일

    // Access Token 생성 (identifier + role 포함)
    public String generateToken(String identifier) {
        System.out.println("실행4");
        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성 (identifier만 포함)
    public String generateRefreshToken(String identifier) {
        System.out.println("실행5");
        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 유효성 검사 (Access Token)
    public boolean validateToken(String jwt) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(jwt);
            return true;
        } catch (ExpiredJwtException e) {
            // 토큰 만료
            return false;
        } catch (JwtException e) {
            // 서명 오류, 형식 오류 등 기타 문제
            return false;
        }
    }

    // 토큰 유효성 검사 (Refresh Token)
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 토큰 만료
            return false;
        } catch (JwtException e) {
            // 서명 오류, 형식 오류 등 기타 문제
            return false;
        }
    }

    // 토큰에서 사용자 식별자 추출
    public String extractIdentifier(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject(); // setSubject에 넣어둔 값이 곧 사용자 식별자
        } catch (JwtException e) {
            return null;
        }
    }

}
