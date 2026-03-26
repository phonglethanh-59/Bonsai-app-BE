package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.CartItemDTO;
import com.vti.bevtilib.exception.BusinessException;
import com.vti.bevtilib.exception.ResourceNotFoundException;
import com.vti.bevtilib.model.CartItem;
import com.vti.bevtilib.model.Product;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.repository.CartItemRepository;
import com.vti.bevtilib.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CartItemDTO> getCartItems(User user) {
        return cartItemRepository.findByUserWithProduct(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CartItemDTO> getCartItems(User user, Pageable pageable) {
        return cartItemRepository.findByUserWithProduct(user, pageable)
                .map(this::convertToDto);
    }

    @Override
    public CartItemDTO addToCart(User user, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new BusinessException("Số lượng phải lớn hơn 0.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm."));

        if (product.getStockQuantity() < quantity) {
            throw new BusinessException("Sản phẩm chỉ còn " + product.getStockQuantity() + " trong kho.");
        }

        Optional<CartItem> existing = cartItemRepository.findByUserAndProduct_Id(user, productId);
        CartItem cartItem;
        if (existing.isPresent()) {
            cartItem = existing.get();
            int newQty = cartItem.getQuantity() + quantity;
            if (newQty > product.getStockQuantity()) {
                throw new BusinessException("Tổng số lượng trong giỏ vượt quá tồn kho.");
            }
            cartItem.setQuantity(newQty);
        } else {
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }

        return convertToDto(cartItemRepository.save(cartItem));
    }

    @Override
    public CartItemDTO updateCartItem(User user, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new BusinessException("Số lượng phải lớn hơn 0. Dùng API xóa sản phẩm khỏi giỏ hàng.");
        }

        CartItem cartItem = cartItemRepository.findByUserAndProduct_Id(user, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không có trong giỏ hàng."));

        Product product = cartItem.getProduct();
        if (quantity > product.getStockQuantity()) {
            throw new BusinessException("Số lượng vượt quá tồn kho (" + product.getStockQuantity() + ").");
        }

        cartItem.setQuantity(quantity);
        return convertToDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void removeFromCart(User user, Long productId) {
        CartItem cartItem = cartItemRepository.findByUserAndProduct_Id(user, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không có trong giỏ hàng."));
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    private CartItemDTO convertToDto(CartItem cartItem) {
        Product product = cartItem.getProduct();
        return CartItemDTO.builder()
                .id(cartItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImage(product.getCoverImage())
                .price(product.getPrice())
                .quantity(cartItem.getQuantity())
                .stockQuantity(product.getStockQuantity())
                .build();
    }
}
