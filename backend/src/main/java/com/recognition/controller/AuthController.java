package com.recognition.controller;

import com.recognition.entity.Users;
import com.recognition.repository.UserRepository;
import com.recognition.service.JwtService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
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

        // Đảm bảo role không null
        String role = user.getRole() != null ? user.getRole() : "USER";

        String userJson = String.format("{\"id\":\"%s\",\"username\":\"%s\",\"email\":\"%s\",\"role\":\"%s\"}",
                user.getId(), user.getUsername(), user.getEmail(), role);

        String encodedUser = URLEncoder.encode(
                String.format("{\"id\":\"%s\",\"username\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"avatarUrl\":\"%s\"}",
                        user.getId(), user.getUsername(), user.getEmail(), role, user.getAvatarUrl()),
                StandardCharsets.UTF_8
        );
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);

        // Redirect sang frontend React SPA
        response.sendRedirect("http://localhost:5173/oauth2/redirect?token=" + encodedToken + "&user=" + encodedUser);
    }
}
