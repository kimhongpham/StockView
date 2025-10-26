package com.recognition.service;

import com.recognition.dto.request.UpdateUserRequest;
import com.recognition.entity.Users;
import com.recognition.repository.UserRepository;
import com.recognition.repository.WatchlistRepository;
import com.recognition.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WatchlistRepository watchlistRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByIdOrThrow_UserExists() {
        UUID id = UUID.randomUUID();
        Users user = new Users();
        user.setId(id);
        user.setEmail("test@example.com");

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Users result = userService.findByIdOrThrow(id);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testFindByIdOrThrow_UserNotFound() {
        UUID id = UUID.randomUUID();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.findByIdOrThrow(id));
    }

    @Test
    void testToDto() {
        Users user = new Users();
        user.setId(UUID.randomUUID());
        user.setUsername("tester");
        user.setEmail("tester@example.com");

        var dto = userService.toDto(user);

        assertEquals(user.getId(), dto.getId());
        assertEquals("tester", dto.getUsername());
        assertEquals("tester@example.com", dto.getEmail());
    }

    @Test
    void testGetCurrentUser() {
        UUID id = UUID.randomUUID();
        Users user = new Users();
        user.setId(id);
        user.setUsername("currentuser");
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        var dto = userService.getCurrentUser(id);
        assertEquals("currentuser", dto.getUsername());
    }

    @Test
    void testUpdateUser() {
        UUID id = UUID.randomUUID();
        Users user = new Users();
        user.setId(id);
        user.setFirstName("OldFirst");
        user.setLastName("OldLast");

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        var req = new UpdateUserRequest();
        req.setFirstName("NewFirst");
        req.setLastName("NewLast");

        var dto = userService.updateUser(id, req);

        assertEquals("NewFirst", dto.getFirstName());
        assertEquals("NewLast", dto.getLastName());
    }

    @Test
    void testWatchlistMethods() {
        UUID userId = UUID.randomUUID();
        String symbol = "AAPL";

        // Mock getWatchlist
        Mockito.when(watchlistRepository.findByUserId(userId))
                .thenReturn(List.of(new com.recognition.entity.Watchlist() {{
                    setSymbol(symbol);
                }}));

        List<String> watchlist = userService.getWatchlist(userId);
        assertTrue(watchlist.contains("AAPL"));

        // Mock addWatchlist
        Mockito.when(watchlistRepository.existsByUserIdAndSymbol(userId, symbol))
                .thenReturn(false);
        var user = new Users();
        user.setId(userId);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.addWatchlist(userId, symbol));

        // Mock removeWatchlist
        assertDoesNotThrow(() -> userService.removeWatchlist(userId, symbol));
    }
}
