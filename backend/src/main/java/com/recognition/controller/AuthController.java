package com.recognition.controller;

import com.recognition.dto.request.LoginRequest;
import com.recognition.dto.request.RegisterRequest;
import com.recognition.dto.response.AuthResponse;
import com.recognition.entity.Users;
import com.recognition.repository.UserRepository;
import com.recognition.service.AuthService;
import com.recognition.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthService authService;

    public AuthController(UserRepository userRepository, JwtService jwtService, AuthService authService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authService = authService;
    }

    // ===================== OAuth2 redirect endpoint =====================
    @GetMapping("/auth/oauth2/success")
    public void oauth2Success(@AuthenticationPrincipal OAuth2User oauth2User,
                              HttpServletResponse response) throws IOException {

        String email = oauth2User.getAttribute("email");

        Optional<Users> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Redirect sang frontend với thông báo lỗi
            response.sendRedirect("http://localhost:5173/oauth2/redirect?error=UserNotFound");
            return;
        }

        Users user = userOpt.get();
        String token = jwtService.generateToken(user);

        String role = user.getRole() != null ? user.getRole() : "USER";

        String encodedUser = URLEncoder.encode(
                String.format("{\"id\":\"%s\",\"username\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"avatarUrl\":\"%s\"}",
                        user.getId(), user.getUsername(), user.getEmail(), role, user.getAvatarUrl()),
                StandardCharsets.UTF_8
        );
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);

        response.sendRedirect("http://localhost:5173/oauth2/redirect?token=" + encodedToken + "&user=" + encodedUser);
    }

    // ===================== REST API: register / login / logout =====================
    @PostMapping("/api/auth/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse res = authService.register(request);
        return ResponseEntity.ok(buildResponse(res.isSuccess(), res.getMessage(), res.getToken()));
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse res = authService.login(request);
        return ResponseEntity.ok(buildResponse(res.isSuccess(), res.getMessage(), res.getToken()));
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = null;
        if (header != null && header.startsWith("Bearer ")) token = header.substring(7);
        AuthResponse res = authService.logout(token);
        return ResponseEntity.ok(buildResponse(res.isSuccess(), res.getMessage(), null));
    }

    private AuthResponse buildResponse(boolean success, String message, String token) {
        return new AuthResponse(success, message, token, null);
    }
}
