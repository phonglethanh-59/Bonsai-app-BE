package com.vti.bevtilib.controller;

import com.vti.bevtilib.dto.AddToCartRequest;
import com.vti.bevtilib.dto.CartItemDTO;
import com.vti.bevtilib.dto.UpdateCartRequest;
import com.vti.bevtilib.exception.ResourceNotFoundException;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.service.CartService;
import com.vti.bevtilib.service.UserService;
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
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<CartItemDTO>> getCart(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User user = getUser(authentication);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        Page<CartItemDTO> items = cartService.getCartItems(user, pageable);
        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<?> addToCart(@Valid @RequestBody AddToCartRequest request, Authentication authentication) {
        User user = getUser(authentication);
        CartItemDTO item = cartService.addToCart(user, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(Map.of("message", "Đã thêm vào giỏ hàng!", "item", item));
    }

    @PutMapping
    public ResponseEntity<?> updateCartItem(@Valid @RequestBody UpdateCartRequest request, Authentication authentication) {
        User user = getUser(authentication);
        CartItemDTO item = cartService.updateCartItem(user, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật giỏ hàng!", "item", item));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long productId, Authentication authentication) {
        User user = getUser(authentication);
        cartService.removeFromCart(user, productId);
        return ResponseEntity.ok(Map.of("message", "Đã xóa khỏi giỏ hàng!"));
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart(Authentication authentication) {
        User user = getUser(authentication);
        cartService.clearCart(user);
        return ResponseEntity.ok(Map.of("message", "Đã xóa toàn bộ giỏ hàng!"));
    }

    private User getUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }
}
