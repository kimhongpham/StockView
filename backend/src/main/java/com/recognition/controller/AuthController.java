package com.recognition.controller;

import com.recognition.dto.request.LoginRequest;
import com.recognition.dto.request.RegisterRequest;
import com.recognition.dto.response.AuthResponse;
import com.recognition.entity.Users;
import com.recognition.repository.UserRepository;
import com.recognition.security.JwtTokenProvider;
import com.recognition.service.AuthService;
import com.recognition.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller xử lý Authentication:
 * - /auth/register: đăng ký tài khoản
 * - /auth/login: đăng nhập tài khoản
 * - /auth/logout: đăng xuất
 * - /auth/oauth2/success: callback từ social login
 */
@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthService authService,
                          UserRepository userRepository,
                          JwtService jwtService,
                          JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Đăng ký người dùng mới (local)
     */
    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * Đăng nhập người dùng (local)
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * Đăng xuất (vô hiệu hoá token)
     */
    @PostMapping("/logout")
    public AuthResponse logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return authService.logout(token);
    }

    /**
     * Callback OAuth2 (Google, GitHub...) sau khi xác thực thành công
     */
    @GetMapping("/oauth2/success")
    public void oauth2Success(Authentication authentication, HttpServletResponse response) throws IOException {
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
//            response.sendRedirect("http://localhost:5173/oauth2/redirect?error=NoAuthentication");
            response.sendRedirect("http://localhost:8080/oauth2/redirect?error=NoAuthentication");
            return;
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        Users user = userRepository.findByEmail(email).orElseGet(() -> {
            Users newUser = new Users();
            newUser.setEmail(email);
            newUser.setUsername(oauth2User.getAttribute("name"));
            newUser.setAvatarUrl(oauth2User.getAttribute("picture"));
            newUser.setRole("USER");
            return userRepository.save(newUser);
        });

        String token = jwtService.generateToken(user);
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
//        response.sendRedirect("http://localhost:5173/oauth2/redirect?token=" + encodedToken);
        response.sendRedirect("http://localhost:8080/oauth2/redirect?token=" + encodedToken);
    }

    /**
     * Xác thực token để kiểm tra login hợp lệ (ví dụ: auto-login frontend)
     */
    @GetMapping("/validate")
    public AuthResponse validateToken(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new AuthResponse(false, "Missing or invalid token", null, null);
        }
        token = token.substring(7);

        if (!jwtTokenProvider.validateToken(token)) {
            return new AuthResponse(false, "Token invalid or expired", null, null);
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(token);
        Optional<Users> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return new AuthResponse(false, "User not found", null, null);
        }

        Users user = userOpt.get();
        return new AuthResponse(true, "Token valid", token, user.getUsername());
    }
}
