package com.vti.bevtilib.service;

import com.vti.bevtilib.config.ProductSpecification;
import com.vti.bevtilib.dto.CategoryDTO;
import com.vti.bevtilib.dto.ProductDTO;
import com.vti.bevtilib.exception.BusinessException;
import com.vti.bevtilib.exception.ResourceNotFoundException;
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

import java.math.BigDecimal;
import java.util.List;

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
    public ProductDTO getProductDtoById(Long id) {
        Product product = productRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
        return convertToDto(product);
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        validateProduct(product);
        if (productRepository.existsBySku(product.getSku())) {
            throw new BusinessException("SKU '" + product.getSku() + "' đã tồn tại.");
        }
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) {
        validateProduct(product);
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
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
        existing.setCareGuide(product.getCareGuide());
        return productRepository.save(existing);
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new BusinessException("Tên sản phẩm không được để trống.");
        }
        if (product.getSku() == null || product.getSku().isBlank()) {
            throw new BusinessException("SKU không được để trống.");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Giá sản phẩm phải lớn hơn 0.");
        }
        if (product.getStockQuantity() < 0) {
            throw new BusinessException("Số lượng tồn kho không được âm.");
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public List<ProductDTO> getRelatedProducts(Long productId) {
        Product product = productRepository.findById(productId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));

        if (product.getCategory() == null) {
            return List.of();
        }
        return productRepository.findTop6ByCategory_IdAndIdNotAndDeletedFalse(
                        product.getCategory().getId(), productId)
                .stream()
                .map(this::convertToDto)
                .collect(java.util.stream.Collectors.toList());
    }

    private ProductDTO convertToDto(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setSku(product.getSku());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setOrigin(product.getOrigin());
        dto.setSupplier(product.getSupplier());
        dto.setCoverImage(product.getCoverImage());
        dto.setPrice(product.getPrice());
        dto.setAge(product.getAge());
        dto.setHeight(product.getHeight());
        dto.setPotType(product.getPotType());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCareLevel(product.getCareLevel() != null ? product.getCareLevel().name() : null);
        dto.setFeatured(product.isFeatured());
        dto.setAverageRating(product.getAverageRating());
        dto.setReviewCount(product.getReviewCount());
        dto.setCareGuide(product.getCareGuide());
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
