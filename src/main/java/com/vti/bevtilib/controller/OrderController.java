package com.vti.bevtilib.controller;

import com.vti.bevtilib.dto.OrderDTO;
import com.vti.bevtilib.dto.OrderRequestDTO;
import com.vti.bevtilib.exception.ResourceNotFoundException;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.service.OrderService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequestDTO request, Authentication authentication) {
        User user = getUser(authentication);
        OrderDTO order = orderService.createOrder(user, request);
        return ResponseEntity.ok(Map.of("message", "Đặt hàng thành công!", "order", order));
    }

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getMyOrders(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = getUser(authentication);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        Page<OrderDTO> orders = orderService.getOrdersForUser(user, status, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id, Authentication authentication) {
        User user = getUser(authentication);
        OrderDTO order = orderService.getOrderById(id, user);
        return ResponseEntity.ok(order);
    }

    private User getUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }
}
