package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.CartItem;
import com.vti.bevtilib.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserOrderByCreatedAtDesc(User user);
    Optional<CartItem> findByUserAndProduct_Id(User user, Long productId);
    void deleteByUser(User user);
}
