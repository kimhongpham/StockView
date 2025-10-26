package com.recognition.service;

import com.recognition.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AuthServiceTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setup() {
        // Sử dụng constructor test-friendly
        jwtTokenProvider = new JwtTokenProvider(
                "TestSecretChangeMePleaseChangeInProd12345",
                10000
        );
    }

    @Test
    void testCreateAndValidateToken() {
        UUID userId = UUID.randomUUID();
        String role = "USER";

        String token = jwtTokenProvider.createToken(userId, role);

        assertTrue(jwtTokenProvider.validateToken(token), "Token should be valid immediately after creation");
        assertTrue(userId.equals(jwtTokenProvider.getUserIdFromToken(token)), "UserId should match");
        assertTrue(role.equals(jwtTokenProvider.getRoleFromToken(token)), "Role should match");
    }

    @Test
    void testInvalidateToken() {
        UUID userId = UUID.randomUUID();
        String role = "USER";

        String token = jwtTokenProvider.createToken(userId, role);
        assertTrue(jwtTokenProvider.validateToken(token), "Token should be valid before invalidation");

        jwtTokenProvider.invalidateToken(token);

        assertFalse(jwtTokenProvider.validateToken(token), "Token should be invalid after invalidation");
    }
}
