package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.WishlistItemDTO;
import com.vti.bevtilib.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishlistService {
    Page<WishlistItemDTO> getWishlistItems(User user, Pageable pageable);
    WishlistItemDTO addToWishlist(User user, Long productId);
    void removeFromWishlist(User user, Long productId);
    void clearWishlist(User user);
    boolean isInWishlist(User user, Long productId);
}
