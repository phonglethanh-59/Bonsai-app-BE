package com.vti.bevtilib.controller;

import com.vti.bevtilib.dto.OrderDTO;
import com.vti.bevtilib.exception.BusinessException;
import com.vti.bevtilib.exception.ResourceNotFoundException;
import com.vti.bevtilib.model.Category;
import com.vti.bevtilib.model.Product;
import com.vti.bevtilib.service.OrderService;
import com.vti.bevtilib.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.vti.bevtilib.dto.AdminUserUpdateDTO;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.service.UserService;
import com.vti.bevtilib.repository.CategoryRepository;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final CategoryRepository categoryRepository;

    // ===================== USER MANAGEMENT =====================

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean status) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), sort);
        Page<User> users = userService.getAllUsersWithFilters(pageable, search, role, status);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody Map<String, Object> userData) {
        User newUser = userService.createUser(userData);
        return ResponseEntity.ok(newUser);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId,
                                        @RequestBody AdminUserUpdateDTO updateDto,
                                        Authentication authentication) {
        String adminUsername = authentication.getName();
        User updatedUser = userService.adminUpdateUser(userId, updateDto, adminUsername);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable String userId,
                                            @RequestBody Map<String, String> payload) {
        String newRole = payload.get("role");
        if (newRole == null || newRole.isBlank()) {
            throw new BusinessException("Vai trò không được để trống.");
        }
        User updatedUser = userService.adminUpdateUserRole(userId, newRole);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId, Authentication authentication) {
        String currentAdminUsername = authentication.getName();
        userService.deleteUser(userId, currentAdminUsername);
        return ResponseEntity.ok(Map.of("message", "Xóa người dùng thành công"));
    }

    @PutMapping("/users/{userId}/toggle-status")
    public ResponseEntity<User> toggleUserStatus(@PathVariable String userId, Authentication authentication) {
        String currentAdminUsername = authentication.getName();
        User updatedUser = userService.toggleUserStatus(userId, currentAdminUsername);
        return ResponseEntity.ok(updatedUser);
    }

    // ===================== PRODUCT MANAGEMENT =====================

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        // Ngăn client tự set các field không nên set
        product.setId(null);
        product.setAverageRating(0.0);
        product.setReviewCount(0);
        product.setCreatedAt(null);
        product.setUpdatedAt(null);

        Product saved = productService.createProduct(product);
        return ResponseEntity.ok(Map.of("message", "Tạo sản phẩm thành công!", "product", saved));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updated = productService.updateProduct(id, product);
        return ResponseEntity.ok(Map.of("message", "Cập nhật sản phẩm thành công!", "product", updated));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "Xóa sản phẩm thành công!"));
    }

    // ===================== CATEGORY MANAGEMENT =====================

    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new BusinessException("Tên danh mục không được để trống.");
        }
        category.setId(null); // Ngăn client tự set ID
        Category saved = categoryRepository.save(category);
        return ResponseEntity.ok(Map.of("message", "Tạo danh mục thành công!", "category", saved));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));
        if (category.getName() == null || category.getName().isBlank()) {
            throw new BusinessException("Tên danh mục không được để trống.");
        }
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        return ResponseEntity.ok(Map.of("message", "Cập nhật danh mục thành công!", "category", categoryRepository.save(existing)));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id);
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Xóa danh mục thành công!"));
    }

    // ===================== ORDER MANAGEMENT =====================

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        Page<OrderDTO> orders = orderService.getAllOrders(status, pageable);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        if (status == null || status.isBlank()) {
            throw new BusinessException("Trạng thái không được để trống.");
        }
        OrderDTO order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái đơn hàng thành công!", "order", order));
    }

    // ===================== DASHBOARD STATS =====================

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = userService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
}
