package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.CartItemDTO;
import com.vti.bevtilib.model.User;

import java.util.List;

public interface CartService {
    List<CartItemDTO> getCartItems(User user);
    CartItemDTO addToCart(User user, Long productId, int quantity) throws Exception;
    CartItemDTO updateCartItem(User user, Long productId, int quantity) throws Exception;
    void removeFromCart(User user, Long productId) throws Exception;
    void clearCart(User user);
}
