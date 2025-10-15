//package com.recognition.service;
//
//import com.automarkettracker.backend.dto.UserDto;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import java.util.UUID;
//
//public interface UserService {
//
//    /**
//     * Tạo mới user.
//     */
//    UserDto createUser(UserDto userDto);
//
//    /**
//     * Cập nhật thông tin user.
//     */
//    UserDto updateUser(UUID id, UserDto userDto);
//
//    /**
//     * Xoá user theo ID.
//     */
//    void deleteUser(UUID id);
//
//    /**
//     * Lấy chi tiết 1 user.
//     */
//    UserDto getUserById(UUID id);
//
//    /**
//     * Tìm kiếm hoặc liệt kê tất cả user (có thể kèm filter).
//     */
//    Page<UserDto> searchUsers(String keyword, Pageable pageable);
//
//    /**
//     * Kiểm tra username/email đã tồn tại.
//     */
//    boolean existsByUsername(String username);
//}
