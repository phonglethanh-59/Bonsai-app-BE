package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.AdminUserUpdateDTO;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.model.UserDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);
    boolean emailExistsForOtherUser(String email, String currentUserId);
    boolean phoneExistsForOtherUser(String phone, String currentUserId);
    User updateReaderProfile(String username, UserDetail userDetailFromForm);
    User updateUserAvatar(String username, MultipartFile avatarFile) throws IOException;
    User adminUpdateUser(String userId, AdminUserUpdateDTO updateDto, String currentAdminUsername);
    User adminUpdateUserRole(String userId, String newRole);

    // Admin CRUD methods
    Page<User> getAllUsersWithFilters(Pageable pageable, String search, String role, Boolean status);
    User getUserById(String userId);
    User createUser(Map<String, Object> userData);
    void deleteUser(String userId, String currentAdminUsername);
    User toggleUserStatus(String userId, String currentAdminUsername);
    Map<String, Object> getDashboardStats();

}
