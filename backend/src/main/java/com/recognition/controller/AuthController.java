package com.recognition.controller;

import com.recognition.entity.Users;
import com.recognition.repository.UserRepository;
import com.recognition.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/auth/oauth2/success")
    public void oauth2Success(Authentication authentication, HttpServletResponse response) throws IOException {

        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            response.sendRedirect("http://localhost:5173/oauth2/redirect?error=NoAuthentication");
            return;
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");

        Optional<Users> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            response.sendRedirect("http://localhost:5173/oauth2/redirect?error=UserNotFound");
            return;
        }

        Users user = userOpt.get();

        // Nếu user chưa có ID (mới tạo), lưu để auto-generate ID
        if (user.getId() == null) {
            userRepository.save(user);
        }

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
}
