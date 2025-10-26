package com.recognition.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT token helper: create, validate, parse user id & role.
 * Supports in-memory blacklist for invalidated tokens.
 * Constructor added for test-friendly instantiation.
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:defaultSecretChangeMePleaseChangeInProd123456}")
    private String jwtSecret;

    @Value("${jwt.expirationMs:86400000}") // 1 day default
    private long jwtExpirationMs;

    private Key key;

    // token -> expiry
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    /** Default constructor for Spring */
    public JwtTokenProvider() {}

    /** Test-friendly constructor */
    public JwtTokenProvider(String secret, long expirationMs) {
        this.jwtSecret = secret;
        this.jwtExpirationMs = expirationMs;
        init();
    }

    @PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            jwtSecret = "defaultSecretChangeMePleaseChangeInProd1234567890";
        }
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String createToken(UUID userId, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(jwtExpirationMs);
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("role", role)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            if (isBlacklisted(token)) return false;
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return UUID.fromString(claims.getSubject());
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        Object role = claims.get("role");
        return role != null ? role.toString() : null;
    }

    public void invalidateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            Date exp = claims.getExpiration();
            blacklist.put(token, exp != null ? exp.getTime() : Instant.now().plusMillis(jwtExpirationMs).toEpochMilli());
        } catch (JwtException ignored) {
            blacklist.put(token, Instant.now().plusMillis(60000).toEpochMilli());
        }
    }

    private boolean isBlacklisted(String token) {
        Long expiry = blacklist.get(token);
        if (expiry == null) return false;
        if (expiry < System.currentTimeMillis()) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }
}
