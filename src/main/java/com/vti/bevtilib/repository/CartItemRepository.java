package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.CartItem;
import com.vti.bevtilib.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT c FROM CartItem c JOIN FETCH c.product WHERE c.user = :user ORDER BY c.createdAt DESC")
    List<CartItem> findByUserWithProduct(@Param("user") User user);

    @Query(value = "SELECT c FROM CartItem c JOIN FETCH c.product WHERE c.user = :user ORDER BY c.createdAt DESC",
           countQuery = "SELECT COUNT(c) FROM CartItem c WHERE c.user = :user")
    Page<CartItem> findByUserWithProduct(@Param("user") User user, Pageable pageable);

    Optional<CartItem> findByUserAndProduct_Id(User user, Long productId);
    void deleteByUser(User user);
    void deleteByUserAndProduct_IdIn(User user, java.util.Collection<Long> productIds);
}
