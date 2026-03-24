package com.vti.bevtilib.service;

import com.vti.bevtilib.config.ProductSpecification;
import com.vti.bevtilib.dto.CategoryDTO;
import com.vti.bevtilib.dto.ProductDTO;
import com.vti.bevtilib.model.Category;
import com.vti.bevtilib.model.Product;
import com.vti.bevtilib.repository.CategoryRepository;
import com.vti.bevtilib.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<ProductDTO> listAllProducts(String keyword, Long categoryId, String availability,
                                            Double minRating, Double priceMin, Double priceMax,
                                            String careLevel, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.filterBy(keyword, categoryId, availability,
                minRating, priceMin, priceMax, careLevel);
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return productPage.map(this::convertToDto);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) throws Exception {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new Exception("Không tìm thấy sản phẩm với ID: " + id));
        existing.setSku(product.getSku());
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setOrigin(product.getOrigin());
        existing.setSupplier(product.getSupplier());
        existing.setCoverImage(product.getCoverImage());
        existing.setAge(product.getAge());
        existing.setHeight(product.getHeight());
        existing.setPotType(product.getPotType());
        existing.setCareLevel(product.getCareLevel());
        existing.setStockQuantity(product.getStockQuantity());
        existing.setFeatured(product.isFeatured());
        existing.setCategory(product.getCategory());
        return productRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) throws Exception {
        if (!productRepository.existsById(id)) {
            throw new Exception("Không tìm thấy sản phẩm với ID: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductDTO convertToDto(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setOrigin(product.getOrigin());
        dto.setCoverImage(product.getCoverImage());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCareLevel(product.getCareLevel() != null ? product.getCareLevel().name() : null);
        dto.setFeatured(product.isFeatured());
        dto.setAverageRating(product.getAverageRating());
        dto.setReviewCount(product.getReviewCount());
        dto.setCreatedAt(product.getCreatedAt());

        if (product.getCategory() != null) {
            CategoryDTO catDto = new CategoryDTO();
            catDto.setId(product.getCategory().getId());
            catDto.setName(product.getCategory().getName());
            dto.setCategory(catDto);
        }
        return dto;
    }
}
