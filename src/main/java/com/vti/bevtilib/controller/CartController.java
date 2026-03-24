package com.vti.bevtilib.controller;

import com.vti.bevtilib.dto.CartItemDTO;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.service.CartService;
import com.vti.bevtilib.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getCart(Authentication authentication) {
        try {
            User user = getUser(authentication);
            List<CartItemDTO> items = cartService.getCartItems(user);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> payload, Authentication authentication) {
        try {
            User user = getUser(authentication);
            Long productId = Long.valueOf(payload.get("productId").toString());
            int quantity = payload.containsKey("quantity") ? Integer.parseInt(payload.get("quantity").toString()) : 1;
            CartItemDTO item = cartService.addToCart(user, productId, quantity);
            return ResponseEntity.ok(Map.of("message", "Đã thêm vào giỏ hàng!", "item", item));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateCartItem(@RequestBody Map<String, Object> payload, Authentication authentication) {
        try {
            User user = getUser(authentication);
            Long productId = Long.valueOf(payload.get("productId").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());
            CartItemDTO item = cartService.updateCartItem(user, productId, quantity);
            return ResponseEntity.ok(Map.of("message", "Đã cập nhật giỏ hàng!", "item", item));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long productId, Authentication authentication) {
        try {
            User user = getUser(authentication);
            cartService.removeFromCart(user, productId);
            return ResponseEntity.ok(Map.of("message", "Đã xóa khỏi giỏ hàng!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart(Authentication authentication) {
        try {
            User user = getUser(authentication);
            cartService.clearCart(user);
            return ResponseEntity.ok(Map.of("message", "Đã xóa toàn bộ giỏ hàng!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private User getUser(Authentication authentication) throws Exception {
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng"));
    }
}
