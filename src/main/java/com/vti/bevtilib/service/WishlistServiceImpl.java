package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.WishlistItemDTO;
import com.vti.bevtilib.exception.BusinessException;
import com.vti.bevtilib.exception.ResourceNotFoundException;
import com.vti.bevtilib.model.Product;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.model.WishlistItem;
import com.vti.bevtilib.repository.ProductRepository;
import com.vti.bevtilib.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    @Transactional(readOnly = true)
    public Page<WishlistItemDTO> getWishlistItems(User user, Pageable pageable) {
        return wishlistItemRepository.findByUserWithProduct(user, pageable)
                .map(this::convertToDto);
    }

    @Override
    public WishlistItemDTO addToWishlist(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm."));

        if (wishlistItemRepository.existsByUserAndProduct_Id(user, productId)) {
            throw new BusinessException("Sản phẩm đã có trong danh sách yêu thích.");
        }

        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setProduct(product);

        return convertToDto(wishlistItemRepository.save(item));
    }

    @Override
    public void removeFromWishlist(User user, Long productId) {
        WishlistItem item = wishlistItemRepository.findByUserAndProduct_Id(user, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không có trong danh sách yêu thích."));
        wishlistItemRepository.delete(item);
    }

    @Override
    public void clearWishlist(User user) {
        wishlistItemRepository.deleteByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInWishlist(User user, Long productId) {
        return wishlistItemRepository.existsByUserAndProduct_Id(user, productId);
    }

    private WishlistItemDTO convertToDto(WishlistItem item) {
        Product product = item.getProduct();
        return WishlistItemDTO.builder()
                .id(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImage(product.getCoverImage())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .createdAt(item.getCreatedAt() != null ? item.getCreatedAt().format(DATE_FORMAT) : null)
                .build();
    }
}
