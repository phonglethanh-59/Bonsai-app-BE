package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.User;
import com.vti.bevtilib.model.WishlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    @Query(value = "SELECT w FROM WishlistItem w JOIN FETCH w.product WHERE w.user = :user ORDER BY w.createdAt DESC",
           countQuery = "SELECT COUNT(w) FROM WishlistItem w WHERE w.user = :user")
    Page<WishlistItem> findByUserWithProduct(@Param("user") User user, Pageable pageable);

    @Query("SELECT w FROM WishlistItem w JOIN FETCH w.product WHERE w.user = :user ORDER BY w.createdAt DESC")
    List<WishlistItem> findByUserWithProduct(@Param("user") User user);

    Optional<WishlistItem> findByUserAndProduct_Id(User user, Long productId);

    boolean existsByUserAndProduct_Id(User user, Long productId);

    void deleteByUser(User user);

    long countByUser(User user);
}
