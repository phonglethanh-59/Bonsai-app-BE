package com.vti.bevtilib.service;

import com.vti.bevtilib.model.User;
import com.vti.bevtilib.model.UserDetail;
import com.vti.bevtilib.repository.UserDetailRepository;
import com.vti.bevtilib.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.vti.bevtilib.dto.AdminUserUpdateDTO;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExistsForOtherUser(String email, String currentUserId) {
        return userRepository.existsByUserDetail_EmailAndUserIdNot(email, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean phoneExistsForOtherUser(String phone, String currentUserId) {
        return userDetailRepository.existsByPhoneAndUser_UserIdNot(phone, currentUserId);
    }

    @Override
    public User updateReaderProfile(String username, UserDetail userDetailFromForm) throws Exception {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng: " + username));

        // --- BẮT ĐẦU LOGIC VALIDATION MỚI ---
        if (StringUtils.hasText(userDetailFromForm.getEmail()) && emailExistsForOtherUser(userDetailFromForm.getEmail(), currentUser.getUserId())) {
            throw new Exception("Email này đã được sử dụng bởi một tài khoản khác.");
        }
        if (StringUtils.hasText(userDetailFromForm.getPhone()) && phoneExistsForOtherUser(userDetailFromForm.getPhone(), currentUser.getUserId())) {
            throw new Exception("Số điện thoại này đã được sử dụng bởi một tài khoản khác.");
        }
        // --- KẾT THÚC LOGIC VALIDATION ---

        UserDetail detailsToUpdate = currentUser.getUserDetail();
        if (detailsToUpdate == null) {
            detailsToUpdate = new UserDetail();
            detailsToUpdate.setUser(currentUser);
        }

        detailsToUpdate.setFullName(userDetailFromForm.getFullName());
        detailsToUpdate.setEmail(userDetailFromForm.getEmail());
        detailsToUpdate.setPhone(userDetailFromForm.getPhone());
        detailsToUpdate.setAddress(userDetailFromForm.getAddress());
        detailsToUpdate.setDob(userDetailFromForm.getDob());
        detailsToUpdate.setGender(userDetailFromForm.getGender());

        currentUser.setUserDetail(detailsToUpdate);
        return userRepository.save(currentUser);
    }
    @Override
    public User updateUserAvatar(String username, MultipartFile avatarFile) throws IOException, Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng"));

        UserDetail userDetail = user.getUserDetail();
        if (userDetail == null) {
            userDetail = new UserDetail();
            userDetail.setUser(user);
        }

        String avatarUrl = saveFile(avatarFile, "avatars");
        userDetail.setAvatar(avatarUrl);
        user.setUserDetail(userDetail);

        return userRepository.save(user);
    }

    // Hàm tiện ích để lưu file
    private String saveFile(MultipartFile file, String subDir) throws IOException {
        Path uploadPath = Paths.get("uploads", subDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return "/uploads/" + subDir + "/" + uniqueFilename;
    }
    @Override
    public User adminUpdateUser(String userId, AdminUserUpdateDTO updateDto, String currentAdminUsername) throws Exception {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng với ID: " + userId));

        // Ràng buộc: Admin không thể tự khóa tài khoản của mình
        if (userToUpdate.getUsername().equals(currentAdminUsername) && updateDto.getStatus() != null && !updateDto.getStatus()) {
            throw new Exception("Admin không thể tự khóa tài khoản của chính mình.");
        }

        UserDetail detail = userToUpdate.getUserDetail();
        if (detail == null) {
            detail = new UserDetail();
            detail.setUser(userToUpdate);
        }

        // Cập nhật thông tin từ DTO
        detail.setFullName(updateDto.getFullName());
        detail.setEmail(updateDto.getEmail());
        detail.setPhone(updateDto.getPhone());
        detail.setAddress(updateDto.getAddress());
        detail.setDob(updateDto.getDob());
        detail.setGender(updateDto.getGender());
        detail.setAvatar(updateDto.getAvatar());

        if (updateDto.getStatus() != null) {
            userToUpdate.setStatus(updateDto.getStatus());
        }

        userToUpdate.setUserDetail(detail);
        return userRepository.save(userToUpdate);
    }

    @Override
    public User adminUpdateUserRole(String userId, String newRole) throws Exception {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng với ID: " + userId));

        // Ràng buộc: Không thể phân quyền cho tài khoản ADMIN
        if ("ADMIN".equalsIgnoreCase(userToUpdate.getRole())) {
            throw new Exception("Không thể thay đổi vai trò của tài khoản ADMIN.");
        }
        // Ràng buộc: Chỉ cho phép phân quyền thành STAFF hoặc READER
        if (!"STAFF".equalsIgnoreCase(newRole) && !"CUSTOMER".equalsIgnoreCase(newRole)) {
            throw new Exception("Vai trò mới không hợp lệ. Chỉ chấp nhận STAFF hoặc CUSTOMER.");
        }

        userToUpdate.setRole(newRole.toUpperCase());
        return userRepository.save(userToUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsersWithFilters(Pageable pageable, String search, String role, Boolean status) {
        return userRepository.findAllWithFilters(search, role, status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(String userId) throws Exception {
        return userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng với ID: " + userId));
    }

    @Override
    public User createUser(Map<String, Object> userData) throws Exception {
        String username = (String) userData.get("username");
        String password = (String) userData.get("password");
        String role = (String) userData.get("role");
        String fullName = (String) userData.get("fullName");
        String email = (String) userData.get("email");
        String phone = (String) userData.get("phone");
        String address = (String) userData.get("address");
        String gender = (String) userData.get("gender");

        // Validate required fields
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Tên đăng nhập không được để trống");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Mật khẩu không được để trống");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new Exception("Vai trò không được để trống");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new Exception("Tên đăng nhập đã tồn tại");
        }

        // Create new user
        User newUser = User.builder()
                .userId(UUID.randomUUID().toString())
                .username(username)
                .password(password) // In production, this should be encrypted
                .role(role.toUpperCase())
                .status(true)
                .build();

        // Create user detail if provided
        if (fullName != null || email != null || phone != null || address != null || gender != null) {
            UserDetail userDetail = new UserDetail();
            userDetail.setUser(newUser);
            userDetail.setFullName(fullName);
            userDetail.setEmail(email);
            userDetail.setPhone(phone);
            userDetail.setAddress(address);
            userDetail.setGender(gender);
            newUser.setUserDetail(userDetail);
        }

        return userRepository.save(newUser);
    }

    @Override
    public void deleteUser(String userId, String currentAdminUsername) throws Exception {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng với ID: " + userId));

        // Ràng buộc: Admin không thể xóa chính mình
        if (userToDelete.getUsername().equals(currentAdminUsername)) {
            throw new Exception("Admin không thể xóa tài khoản của chính mình");
        }

        // Ràng buộc: Không thể xóa tài khoản ADMIN
        if ("ADMIN".equalsIgnoreCase(userToDelete.getRole())) {
            throw new Exception("Không thể xóa tài khoản ADMIN");
        }

        userRepository.delete(userToDelete);
    }

    @Override
    public User toggleUserStatus(String userId, String currentAdminUsername) throws Exception {
        User userToToggle = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng với ID: " + userId));

        // Ràng buộc: Admin không thể khóa chính mình
        if (userToToggle.getUsername().equals(currentAdminUsername)) {
            throw new Exception("Admin không thể khóa tài khoản của chính mình");
        }

        // Ràng buộc: Không thể khóa tài khoản ADMIN
        if ("ADMIN".equalsIgnoreCase(userToToggle.getRole())) {
            throw new Exception("Không thể khóa tài khoản ADMIN");
        }

        userToToggle.setStatus(!userToToggle.isStatus());
        return userRepository.save(userToToggle);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        long totalAdmins = userRepository.countByRole("ADMIN");
        long totalStaff = userRepository.countByRole("STAFF");
        long totalCustomers = userRepository.countByRole("CUSTOMER");
        long lockedAccounts = userRepository.countByStatus(false);

        return Map.of(
            "totalAdmins", totalAdmins,
            "totalStaff", totalStaff,
            "totalCustomers", totalCustomers,
            "lockedAccounts", lockedAccounts,
            "totalUsers", totalAdmins + totalStaff + totalCustomers
        );
    }
}