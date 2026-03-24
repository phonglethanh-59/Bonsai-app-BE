package com.vti.bevtilib.controller;

import com.vti.bevtilib.model.User;
import com.vti.bevtilib.model.UserDetail;
import com.vti.bevtilib.service.UserService;
import com.vti.bevtilib.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/profile")
    public ResponseEntity<User> getCurrentUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String username = authentication.getName();
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> handleProfileUpdate(Authentication authentication,
                                                 @RequestBody UserDetail userDetailFromForm,
                                                 HttpSession session) {
        try {
            String username = authentication.getName();
            User updatedUser = userService.updateReaderProfile(username, userDetailFromForm);
            session.removeAttribute("showFirstLoginPopup");
            return ResponseEntity.ok(Map.of(
                    "message", "Cập nhật thông tin thành công!",
                    "updatedUser", updatedUser
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/profile/avatar")
    public ResponseEntity<?> uploadAvatar(Authentication authentication, @RequestParam("avatarFile") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng chọn một file ảnh."));
        }
        try {
            String username = authentication.getName();
            User updatedUser = userService.updateUserAvatar(username, file);
            return ResponseEntity.ok(Map.of(
                    "message", "Cập nhật ảnh đại diện thành công!",
                    "avatarUrl", updatedUser.getUserDetail().getAvatar()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("message", "Lỗi khi tải file lên."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getMyOrders(Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new Exception("Không tìm thấy người dùng"));
            return ResponseEntity.ok(orderService.getOrdersForUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
