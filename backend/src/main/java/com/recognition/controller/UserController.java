package com.recognition.controller;

import com.recognition.dto.UserDTO;
import com.recognition.dto.request.UpdateUserRequest;
import com.recognition.dto.request.WatchlistRequest;
import com.recognition.security.JwtTokenProvider;
import com.recognition.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * User-related endpoints (no UserDetailsImpl needed)
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // ==========================
    // LẤY THÔNG TIN NGƯỜI DÙNG HIỆN TẠI
    // ==========================
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            UUID userId = jwtTokenProvider.getUserIdFromToken(token);
            UserDTO dto = userService.getCurrentUser(userId);
            return ResponseEntity.ok(buildResponse(true, "OK", dto));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body(buildResponse(false, "Invalid or expired token", null));
        }
    }

    // ==========================
    // CẬP NHẬT NGƯỜI DÙNG HIỆN TẠI
    // ==========================
    @PutMapping("/me")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authHeader,
                                        @Valid @RequestBody UpdateUserRequest request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            UUID userId = jwtTokenProvider.getUserIdFromToken(token);

            UserDTO updated = userService.updateUser(userId, request);
            return ResponseEntity.ok(buildResponse(true, "Updated", updated));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body(buildResponse(false, "Invalid or expired token", null));
        }
    }

    // ==========================
    // WATCHLIST
    // ==========================
    @GetMapping("/watchlist")
    public ResponseEntity<?> getWatchlist(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            UUID userId = jwtTokenProvider.getUserIdFromToken(token);

            List<String> list = userService.getWatchlist(userId);
            return ResponseEntity.ok(buildResponse(true, "OK", list));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(buildResponse(false, "Invalid token", null));
        }
    }

    @PostMapping("/watchlist")
    public ResponseEntity<?> addWatchlist(@Valid @RequestBody WatchlistRequest request,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            UUID userId = jwtTokenProvider.getUserIdFromToken(token);

            userService.addWatchlist(userId, request.getSymbol());
            return ResponseEntity.ok(buildResponse(true, "Added", null));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(buildResponse(false, "Invalid token", null));
        }
    }

    @DeleteMapping("/watchlist")
    public ResponseEntity<?> removeWatchlist(@Valid @RequestBody WatchlistRequest request,
                                             @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            UUID userId = jwtTokenProvider.getUserIdFromToken(token);

            userService.removeWatchlist(userId, request.getSymbol());
            return ResponseEntity.ok(buildResponse(true, "Removed", null));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(buildResponse(false, "Invalid token", null));
        }
    }

    private Object buildResponse(boolean success, String message, Object data) {
        return new HashMap<>() {{
            put("success", success);
            put("message", message);
            put("data", data);
        }};
    }
}
