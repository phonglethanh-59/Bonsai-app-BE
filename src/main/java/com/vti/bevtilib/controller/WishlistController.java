package com.vti.bevtilib.controller;

import com.vti.bevtilib.dto.AddToWishlistRequest;
import com.vti.bevtilib.dto.WishlistItemDTO;
import com.vti.bevtilib.exception.ResourceNotFoundException;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.service.UserService;
import com.vti.bevtilib.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<WishlistItemDTO>> getWishlist(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User user = getUser(authentication);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        Page<WishlistItemDTO> items = wishlistService.getWishlistItems(user, pageable);
        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<?> addToWishlist(@Valid @RequestBody AddToWishlistRequest request, Authentication authentication) {
        User user = getUser(authentication);
        WishlistItemDTO item = wishlistService.addToWishlist(user, request.getProductId());
        return ResponseEntity.ok(Map.of("message", "Đã thêm vào danh sách yêu thích!", "item", item));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long productId, Authentication authentication) {
        User user = getUser(authentication);
        wishlistService.removeFromWishlist(user, productId);
        return ResponseEntity.ok(Map.of("message", "Đã xóa khỏi danh sách yêu thích!"));
    }

    @DeleteMapping
    public ResponseEntity<?> clearWishlist(Authentication authentication) {
        User user = getUser(authentication);
        wishlistService.clearWishlist(user);
        return ResponseEntity.ok(Map.of("message", "Đã xóa toàn bộ danh sách yêu thích!"));
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<?> isInWishlist(@PathVariable Long productId, Authentication authentication) {
        User user = getUser(authentication);
        boolean inWishlist = wishlistService.isInWishlist(user, productId);
        return ResponseEntity.ok(Map.of("inWishlist", inWishlist));
    }

    private User getUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }
}
