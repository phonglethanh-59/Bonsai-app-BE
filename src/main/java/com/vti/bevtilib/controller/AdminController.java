package com.vti.bevtilib.controller;

import com.vti.bevtilib.dto.DashboardStatsDTO;
import com.vti.bevtilib.dto.OrderDTO;
import com.vti.bevtilib.dto.ReviewDTO;
import com.vti.bevtilib.exception.BusinessException;
import com.vti.bevtilib.exception.ResourceNotFoundException;
import com.vti.bevtilib.model.Category;
import com.vti.bevtilib.model.Product;
import com.vti.bevtilib.model.ProductImage;
import com.vti.bevtilib.repository.ProductImageRepository;
import com.vti.bevtilib.service.FileStorageService;
import com.vti.bevtilib.service.DashboardService;
import com.vti.bevtilib.service.OrderService;
import com.vti.bevtilib.service.ProductService;
import com.vti.bevtilib.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    private final ReviewService reviewService;
    private final CategoryRepository categoryRepository;
    private final DashboardService dashboardService;
    private final FileStorageService fileStorageService;
    private final ProductImageRepository productImageRepository;

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

    @PostMapping("/products/{id}/images")
    public ResponseEntity<?> uploadProductImages(@PathVariable Long id,
                                                  @RequestParam("files") MultipartFile[] files) {
        Product product = productService.getProductById(id);
        if (product == null) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id);
        }
        if (files == null || files.length == 0) {
            throw new BusinessException("Vui lòng chọn ít nhất 1 ảnh.");
        }
        if (files.length > 10) {
            throw new BusinessException("Tối đa 10 ảnh cho mỗi lần upload.");
        }

        try {
            int currentMaxOrder = productImageRepository.findMaxDisplayOrder(id);
            java.util.List<String> uploadedUrls = new java.util.ArrayList<>();

            for (int i = 0; i < files.length; i++) {
                String imageUrl = fileStorageService.saveFile(files[i], "products");
                ProductImage img = new ProductImage(imageUrl, currentMaxOrder + 1 + i, product);
                productImageRepository.save(img);
                uploadedUrls.add(imageUrl);
            }

            // Set ảnh đầu tiên làm cover nếu chưa có
            if (product.getCoverImage() == null || product.getCoverImage().isBlank()) {
                product.setCoverImage(uploadedUrls.get(0));
                productService.saveProduct(product);
            }

            return ResponseEntity.ok(Map.of(
                "message", "Upload " + files.length + " ảnh thành công!",
                "images", uploadedUrls
            ));
        } catch (java.io.IOException e) {
            throw new BusinessException("Lỗi upload ảnh: " + e.getMessage());
        }
    }

    @DeleteMapping("/products/{productId}/images/{imageId}")
    public ResponseEntity<?> deleteProductImage(@PathVariable Long productId, @PathVariable Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ảnh với ID: " + imageId));

        if (!image.getProduct().getId().equals(productId)) {
            throw new BusinessException("Ảnh không thuộc sản phẩm này.");
        }

        // Delete file from disk
        fileStorageService.deleteFile(image.getImageUrl());

        // If this was the cover image, update cover to next image or null
        Product product = image.getProduct();
        if (image.getImageUrl().equals(product.getCoverImage())) {
            productImageRepository.delete(image);
            java.util.List<ProductImage> remaining = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
            product.setCoverImage(remaining.isEmpty() ? null : remaining.get(0).getImageUrl());
            productService.saveProduct(product);
        } else {
            productImageRepository.delete(image);
        }

        return ResponseEntity.ok(Map.of("message", "Xóa ảnh thành công!"));
    }

    @GetMapping("/products/{id}/images")
    public ResponseEntity<?> getProductImages(@PathVariable Long id) {
        java.util.List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(id);
        java.util.List<Map<String, Object>> result = images.stream().map(img -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", img.getId());
            map.put("imageUrl", img.getImageUrl());
            map.put("displayOrder", img.getDisplayOrder());
            return map;
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/products/{productId}/cover-image")
    public ResponseEntity<?> removeCoverImage(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        // If there are uploaded images, use the first one as cover
        java.util.List<ProductImage> uploadedImages = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        if (!uploadedImages.isEmpty()) {
            product.setCoverImage(uploadedImages.get(0).getImageUrl());
        } else {
            product.setCoverImage(null);
        }
        productService.saveProduct(product);
        return ResponseEntity.ok(Map.of("message", "Đã xóa ảnh bìa URL cũ."));
    }

    @PutMapping("/products/{productId}/images/{imageId}/set-cover")
    public ResponseEntity<?> setCoverImage(@PathVariable Long productId, @PathVariable Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ảnh với ID: " + imageId));

        if (!image.getProduct().getId().equals(productId)) {
            throw new BusinessException("Ảnh không thuộc sản phẩm này.");
        }

        Product product = image.getProduct();
        product.setCoverImage(image.getImageUrl());
        productService.saveProduct(product);

        return ResponseEntity.ok(Map.of("message", "Đã đặt làm ảnh bìa!"));
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

    // ===================== REVIEW MANAGEMENT =====================

    @GetMapping("/reviews")
    public ResponseEntity<Page<ReviewDTO>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        Page<ReviewDTO> reviews = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReviewByAdmin(reviewId);
        return ResponseEntity.ok(Map.of("message", "Xóa đánh giá thành công!"));
    }

    // ===================== DASHBOARD STATS =====================

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = userService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<DashboardStatsDTO> getAdvancedDashboardStats() {
        DashboardStatsDTO stats = dashboardService.getAdvancedStats();
        return ResponseEntity.ok(stats);
    }
}
