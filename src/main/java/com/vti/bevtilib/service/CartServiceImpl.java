package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.CartItemDTO;
import com.vti.bevtilib.model.CartItem;
import com.vti.bevtilib.model.Product;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.repository.CartItemRepository;
import com.vti.bevtilib.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
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
        return cartItemRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CartItemDTO addToCart(User user, Long productId, int quantity) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Không tìm thấy sản phẩm."));

        if (product.getStockQuantity() < quantity) {
            throw new Exception("Sản phẩm chỉ còn " + product.getStockQuantity() + " trong kho.");
        }

        Optional<CartItem> existing = cartItemRepository.findByUserAndProduct_Id(user, productId);
        CartItem cartItem;
        if (existing.isPresent()) {
            cartItem = existing.get();
            int newQty = cartItem.getQuantity() + quantity;
            if (newQty > product.getStockQuantity()) {
                throw new Exception("Tổng số lượng trong giỏ vượt quá tồn kho.");
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
    public CartItemDTO updateCartItem(User user, Long productId, int quantity) throws Exception {
        CartItem cartItem = cartItemRepository.findByUserAndProduct_Id(user, productId)
                .orElseThrow(() -> new Exception("Sản phẩm không có trong giỏ hàng."));

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }

        Product product = cartItem.getProduct();
        if (quantity > product.getStockQuantity()) {
            throw new Exception("Số lượng vượt quá tồn kho (" + product.getStockQuantity() + ").");
        }

        cartItem.setQuantity(quantity);
        return convertToDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void removeFromCart(User user, Long productId) throws Exception {
        CartItem cartItem = cartItemRepository.findByUserAndProduct_Id(user, productId)
                .orElseThrow(() -> new Exception("Sản phẩm không có trong giỏ hàng."));
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
