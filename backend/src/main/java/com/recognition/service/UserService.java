package com.recognition.service;

import com.recognition.dto.request.UpdateUserRequest;
import com.recognition.dto.UserDTO;
import com.recognition.entity.Users;

import java.util.List;
import java.util.UUID;

/**
 * User domain logic exposed by service layer.
 */
public interface UserService {

    UserDTO toDto(Users user);

    Users findByIdOrThrow(UUID id);

    UserDTO getCurrentUser(UUID id);

    UserDTO updateUser(UUID id, UpdateUserRequest request);

    List<String> getWatchlist(UUID userId);

    void addWatchlist(UUID userId, String symbol);

    void removeWatchlist(UUID userId, String symbol);
}
