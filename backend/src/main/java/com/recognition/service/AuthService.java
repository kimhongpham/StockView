package com.recognition.service;

import com.recognition.dto.response.AuthResponse;
import com.recognition.dto.request.LoginRequest;
import com.recognition.dto.request.RegisterRequest;

/**
 * Authentication related operations.
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse logout(String token);

    /**
     * If social login or token auto-login, may register or fetch existing user by provider.
     */
    void registerOrUpdateSocial(String provider, String providerId, String email, String username, String avatarUrl);
}
