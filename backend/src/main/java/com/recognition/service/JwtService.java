package com.recognition.service;

import com.recognition.entity.Users;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret:defaultSecretChangeMePleaseChangeInProd1234567890}")
    private String jwtSecret;

    @Value("${jwt.expirationMs:86400000}") // 1 ngày
    private long expirationMs;

    private Key key;

    @jakarta.annotation.PostConstruct
    public void init() {
        if (jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
        }
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Users user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Key getSecretKey() {
        return key;
    }
}
