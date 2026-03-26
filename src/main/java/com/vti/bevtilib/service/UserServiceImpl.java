package com.vti.bevtilib.service;

import com.vti.bevtilib.exception.BusinessException;
import com.vti.bevtilib.exception.ResourceNotFoundException;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.model.UserDetail;
import com.vti.bevtilib.repository.UserDetailRepository;
import com.vti.bevtilib.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.vti.bevtilib.dto.AdminUserUpdateDTO;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
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
    private final PasswordEncoder passwordEncoder;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private void validateEmail(String email) {
        if (StringUtils.hasText(email) && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException("Email không đúng định dạng.");
        }
    }

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
    public User updateReaderProfile(String username, UserDetail userDetailFromForm) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + username));

        validateEmail(userDetailFromForm.getEmail());
        if (StringUtils.hasText(userDetailFromForm.getEmail()) && emailExistsForOtherUser(userDetailFromForm.getEmail(), currentUser.getUserId())) {
            throw new BusinessException("Email này đã được sử dụng bởi một tài khoản khác.");
        }
        if (StringUtils.hasText(userDetailFromForm.getPhone()) && phoneExistsForOtherUser(userDetailFromForm.getPhone(), currentUser.getUserId())) {
            throw new BusinessException("Số điện thoại này đã được sử dụng bởi một tài khoản khác.");
        }

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
    public User updateUserAvatar(String username, MultipartFile avatarFile) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

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

    private String saveFile(MultipartFile file, String subDir) throws IOException {
        Path uploadPath = Paths.get("uploads", subDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)")) {
            throw new IOException("Chỉ cho phép upload file ảnh (jpg, jpeg, png, gif, webp).");
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        Path filePath = uploadPath.resolve(uniqueFilename);
        if (!filePath.normalize().startsWith(uploadPath.normalize())) {
            throw new IOException("Đường dẫn file không hợp lệ.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return "/uploads/" + subDir + "/" + uniqueFilename;
    }

    @Override
    public User adminUpdateUser(String userId, AdminUserUpdateDTO updateDto, String currentAdminUsername) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        // Ràng buộc: Admin không thể tự khóa tài khoản của mình
        if (userToUpdate.getUsername().equals(currentAdminUsername) && updateDto.getStatus() != null && !updateDto.getStatus()) {
            throw new BusinessException("Admin không thể tự khóa tài khoản của chính mình.");
        }

        // Validate email format + trùng lặp
        validateEmail(updateDto.getEmail());
        if (StringUtils.hasText(updateDto.getEmail()) && emailExistsForOtherUser(updateDto.getEmail(), userId)) {
            throw new BusinessException("Email này đã được sử dụng bởi một tài khoản khác.");
        }
        if (StringUtils.hasText(updateDto.getPhone()) && phoneExistsForOtherUser(updateDto.getPhone(), userId)) {
            throw new BusinessException("Số điện thoại này đã được sử dụng bởi một tài khoản khác.");
        }

        UserDetail detail = userToUpdate.getUserDetail();
        if (detail == null) {
            detail = new UserDetail();
            detail.setUser(userToUpdate);
        }

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
    public User adminUpdateUserRole(String userId, String newRole) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        if ("ADMIN".equalsIgnoreCase(userToUpdate.getRole())) {
            throw new BusinessException("Không thể thay đổi vai trò của tài khoản ADMIN.");
        }
        if (!"STAFF".equalsIgnoreCase(newRole) && !"CUSTOMER".equalsIgnoreCase(newRole)) {
            throw new BusinessException("Vai trò mới không hợp lệ. Chỉ chấp nhận STAFF hoặc CUSTOMER.");
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
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
    }

    @Override
    public User createUser(Map<String, Object> userData) {
        String username = (String) userData.get("username");
        String password = (String) userData.get("password");
        String role = (String) userData.get("role");
        String fullName = (String) userData.get("fullName");
        String email = (String) userData.get("email");
        String phone = (String) userData.get("phone");
        String address = (String) userData.get("address");
        String gender = (String) userData.get("gender");

        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("Tên đăng nhập không được để trống");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException("Mật khẩu không được để trống");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new BusinessException("Vai trò không được để trống");
        }

        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("Tên đăng nhập đã tồn tại");
        }

        // Validate email format + trùng lặp
        validateEmail(email);
        if (StringUtils.hasText(email) && userRepository.existsByUserDetail_Email(email)) {
            throw new BusinessException("Email này đã được sử dụng bởi một tài khoản khác.");
        }

        User newUser = User.builder()
                .userId(UUID.randomUUID().toString())
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role.toUpperCase())
                .status(true)
                .build();

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
    public void deleteUser(String userId, String currentAdminUsername) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        if (userToDelete.getUsername().equals(currentAdminUsername)) {
            throw new BusinessException("Admin không thể xóa tài khoản của chính mình");
        }

        if ("ADMIN".equalsIgnoreCase(userToDelete.getRole())) {
            throw new BusinessException("Không thể xóa tài khoản ADMIN");
        }

        userRepository.delete(userToDelete);
    }

    @Override
    public User toggleUserStatus(String userId, String currentAdminUsername) {
        User userToToggle = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        if (userToToggle.getUsername().equals(currentAdminUsername)) {
            throw new BusinessException("Admin không thể khóa tài khoản của chính mình");
        }

        if ("ADMIN".equalsIgnoreCase(userToToggle.getRole())) {
            throw new BusinessException("Không thể khóa tài khoản ADMIN");
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
