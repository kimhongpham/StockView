package com.recognition.service.impl;

import com.recognition.dto.request.UpdateUserRequest;
import com.recognition.dto.UserDTO;
import com.recognition.entity.Users;
import com.recognition.entity.Watchlist;
import com.recognition.repository.UserRepository;
import com.recognition.repository.WatchlistRepository;
import com.recognition.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of UserService.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WatchlistRepository watchlistRepository;

    public UserServiceImpl(UserRepository userRepository, WatchlistRepository watchlistRepository) {
        this.userRepository = userRepository;
        this.watchlistRepository = watchlistRepository;
    }

    @Override
    public UserDTO toDto(Users user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setIsActive(user.getIsActive());
        dto.setIsVerified(user.getIsVerified());
        dto.setTimezone(user.getTimezone());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setProvider(user.getProvider());
        dto.setRole(user.getRole());
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }

    @Override
    public Users findByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Override
    public UserDTO getCurrentUser(UUID id) {
        Users user = findByIdOrThrow(id);
        return toDto(user);
    }

    @Override
    public UserDTO updateUser(UUID id, UpdateUserRequest request) {
        Users user = findByIdOrThrow(id);

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getTimezone() != null) user.setTimezone(request.getTimezone());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());

        // Mới thêm từ frontend
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail());

        user.setUpdatedAt(java.time.OffsetDateTime.now());
        userRepository.save(user);
        return toDto(user);
    }

    @Override
    public List<String> getWatchlist(UUID userId) {
        return watchlistRepository.findByUserId(userId)
                .stream()
                .map(Watchlist::getSymbol)
                .collect(Collectors.toList());
    }

    @Override
    public void addWatchlist(UUID userId, String symbol) {
        if (watchlistRepository.existsByUserIdAndSymbol(userId, symbol)) return;
        Users user = findByIdOrThrow(userId);
        Watchlist w = new Watchlist();
        w.setUser(user);
        w.setSymbol(symbol);
        watchlistRepository.save(w);
    }

    @Override
    public void removeWatchlist(UUID userId, String symbol) {
        watchlistRepository.deleteByUserIdAndSymbol(userId, symbol);
    }
}
