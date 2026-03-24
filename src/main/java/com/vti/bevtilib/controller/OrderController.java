package com.vti.bevtilib.controller;

import com.vti.bevtilib.dto.OrderDTO;
import com.vti.bevtilib.dto.OrderRequestDTO;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.service.OrderService;
import com.vti.bevtilib.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO request, Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new Exception("Không tìm thấy người dùng"));
            OrderDTO order = orderService.createOrder(user, request);
            return ResponseEntity.ok(Map.of("message", "Đặt hàng thành công!", "order", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getMyOrders(Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new Exception("Không tìm thấy người dùng"));
            List<OrderDTO> orders = orderService.getOrdersForUser(user);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            OrderDTO order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
