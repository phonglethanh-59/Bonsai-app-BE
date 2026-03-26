package com.vti.bevtilib.controller;

import com.vti.bevtilib.exception.ResourceNotFoundException;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.model.UserDetail;
import com.vti.bevtilib.service.UserService;
import com.vti.bevtilib.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        String username = authentication.getName();
        User updatedUser = userService.updateReaderProfile(username, userDetailFromForm);
        session.removeAttribute("showFirstLoginPopup");
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật thông tin thành công!",
                "updatedUser", updatedUser
        ));
    }

    @PostMapping("/profile/avatar")
    public ResponseEntity<?> uploadAvatar(Authentication authentication, @RequestParam("avatarFile") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng chọn một file ảnh."));
        }
        String username = authentication.getName();
        User updatedUser = userService.updateUserAvatar(username, file);
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật ảnh đại diện thành công!",
                "avatarUrl", updatedUser.getUserDetail().getAvatar()
        ));
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getMyOrders(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        return ResponseEntity.ok(orderService.getOrdersForUser(user, status, pageable));
    }
}
