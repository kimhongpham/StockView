package com.recognition.service.impl;

import com.recognition.dto.request.LoginRequest;
import com.recognition.dto.request.RegisterRequest;
import com.recognition.dto.response.AuthResponse;
import com.recognition.entity.Users;
import com.recognition.repository.UserRepository;
import com.recognition.security.JwtTokenProvider;
import com.recognition.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

/**
 * Authentication service implementation: register, login, logout.
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // validate uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse(false, "Email already in use", null, null);
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            return new AuthResponse(false, "Username already in use", null, null);
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        user.setProvider(request.getProvider() == null ? "local" : request.getProvider());
        user.setProviderId(request.getProviderId());
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        user = userRepository.save(user);

        // Auto login after register: create token
        String token = jwtTokenProvider.createToken(user.getId(), user.getRole());
        return new AuthResponse(true, "Registered successfully", token, null);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // find by username or email
        Users user = userRepository.findByUsername(request.getUsernameOrEmail())
                .orElseGet(() -> userRepository.findByEmail(request.getUsernameOrEmail()).orElse(null));
        if (user == null) {
            return new AuthResponse(false, "Invalid credentials", null, null);
        }
        if (user.getPasswordHash() == null) {
            return new AuthResponse(false, "Account has no password (use social login)", null, null);
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return new AuthResponse(false, "Invalid credentials", null, null);
        }
        user.setLastLogin(OffsetDateTime.now());
        userRepository.save(user);
        String token = jwtTokenProvider.createToken(user.getId(), user.getRole());
        return new AuthResponse(true, "Login successful", token, null);
    }

    @Override
    public AuthResponse logout(String token) {
        if (token == null || token.isBlank()) {
            return new AuthResponse(false, "Token required", null, null);
        }
        jwtTokenProvider.invalidateToken(token);
        return new AuthResponse(true, "Logged out", null, null);
    }

    @Override
    public void registerOrUpdateSocial(String provider, String providerId, String email, String username, String avatarUrl) {
        // attempt find by providerId or email
        Users u = userRepository.findByEmail(email).orElse(null);
        if (u == null) {
            u = new Users();
            u.setEmail(email);
            u.setUsername(username != null ? username : email.split("@")[0]);
            u.setProvider(provider);
            u.setProviderId(providerId);
            u.setAvatarUrl(avatarUrl);
            u.setIsVerified(true);
            u.setCreatedAt(OffsetDateTime.now());
            u.setUpdatedAt(OffsetDateTime.now());
            userRepository.save(u);
        } else {
            u.setProvider(provider);
            u.setProviderId(providerId);
            u.setAvatarUrl(avatarUrl);
            u.setUpdatedAt(OffsetDateTime.now());
            userRepository.save(u);
        }
    }
}
