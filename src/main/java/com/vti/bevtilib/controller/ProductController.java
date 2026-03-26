package com.vti.bevtilib.controller;

import com.vti.bevtilib.dto.ProductDTO;
import com.vti.bevtilib.model.Category;
import com.vti.bevtilib.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "price", "name", "averageRating", "reviewCount", "stockQuantity"
    );

    @GetMapping("/products")
    public ResponseEntity<Page<ProductDTO>> getProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "availability", required = false) String availability,
            @RequestParam(name = "minRating", required = false) Double minRating,
            @RequestParam(name = "priceMin", required = false) Double priceMin,
            @RequestParam(name = "priceMax", required = false) Double priceMax,
            @RequestParam(name = "careLevel", required = false) String careLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            sortField = "createdAt";
        }
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        // Giới hạn page size tối đa 100
        size = Math.min(size, 100);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(direction, sortField));

        Page<ProductDTO> productPage = productService.listAllProducts(keyword, categoryId, availability,
                minRating, priceMin, priceMax, careLevel, pageable);
        return ResponseEntity.ok(productPage);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO dto = productService.getProductDtoById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/products/{id}/related")
    public ResponseEntity<List<ProductDTO>> getRelatedProducts(@PathVariable Long id) {
        List<ProductDTO> related = productService.getRelatedProducts(id);
        return ResponseEntity.ok(related);
    }
}
