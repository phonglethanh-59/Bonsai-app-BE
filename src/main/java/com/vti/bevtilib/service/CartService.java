package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.CartItemDTO;
import com.vti.bevtilib.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartService {
    List<CartItemDTO> getCartItems(User user);
    Page<CartItemDTO> getCartItems(User user, Pageable pageable);
    CartItemDTO addToCart(User user, Long productId, int quantity);
    CartItemDTO updateCartItem(User user, Long productId, int quantity);
    void removeFromCart(User user, Long productId);
    void clearCart(User user);
}
