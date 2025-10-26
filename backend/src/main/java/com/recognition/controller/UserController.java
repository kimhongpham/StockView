package com.recognition.controller;

import com.recognition.dto.request.UpdateUserRequest;
import com.recognition.dto.UserDTO;
import com.recognition.dto.request.WatchlistRequest;
import com.recognition.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * User-related endpoints. Controllers do minimal work: pass to services.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    /**
     * GET /api/users/me
     * Authentication principal holds userId (set in JwtAuthenticationFilter)
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        UserDTO dto = userService.getCurrentUser(userId);
        return ResponseEntity.ok(buildResponse(true, "OK", dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") UUID id,
                                        @Valid @RequestBody UpdateUserRequest request,
                                        Authentication authentication) {
        // Could add authorization check: allow update only if same user or admin
        UUID actor = (UUID) authentication.getPrincipal();
        if (!actor.equals(id)) {
            // simple check; expand with roles in real app
            return ResponseEntity.status(403).body(buildResponse(false, "Forbidden", null));
        }
        UserDTO updated = userService.updateUser(id, request);
        return ResponseEntity.ok(buildResponse(true, "Updated", updated));
    }

    // Watchlist: GET
    @GetMapping("/watchlist")
    public ResponseEntity<?> getWatchlist(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        List<String> list = userService.getWatchlist(userId);
        return ResponseEntity.ok(buildResponse(true, "OK", list));
    }

    // Watchlist: POST (add)
    @PostMapping("/watchlist")
    public ResponseEntity<?> addWatchlist(@Valid @RequestBody WatchlistRequest request,
                                          Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        userService.addWatchlist(userId, request.getSymbol());
        return ResponseEntity.ok(buildResponse(true, "Added", null));
    }

    // Watchlist: DELETE (remove)
    @DeleteMapping("/watchlist")
    public ResponseEntity<?> removeWatchlist(@Valid @RequestBody WatchlistRequest request,
                                             Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        userService.removeWatchlist(userId, request.getSymbol());
        return ResponseEntity.ok(buildResponse(true, "Removed", null));
    }

    private Object buildResponse(boolean success, String message, Object data) {
        return new java.util.HashMap<>() {{
            put("success", success);
            put("message", message);
            put("data", data);
        }};
    }
}
