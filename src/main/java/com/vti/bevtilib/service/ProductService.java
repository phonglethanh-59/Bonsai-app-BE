package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.ProductDTO;
import com.vti.bevtilib.model.Category;
import com.vti.bevtilib.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Page<ProductDTO> listAllProducts(String keyword, Long categoryId, String availability,
                                     Double minRating, Double priceMin, Double priceMax,
                                     String careLevel, Pageable pageable);
    List<Category> getAllCategories();
    ProductDTO getProductDtoById(Long id);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    List<ProductDTO> getRelatedProducts(Long productId);
    Product getProductById(Long id);
    Product saveProduct(Product product);
}
