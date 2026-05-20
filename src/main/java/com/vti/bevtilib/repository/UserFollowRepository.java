package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    boolean existsByFollowerUserIdAndFollowingUserId(String followerId, String followingId);

    Optional<UserFollow> findByFollowerUserIdAndFollowingUserId(String followerId, String followingId);

    // Followers của một user
    @Query("SELECT f FROM UserFollow f WHERE f.following.userId = :userId")
    Page<UserFollow> findFollowersByUserId(@Param("userId") String userId, Pageable pageable);

    // Danh sách đang follow
    @Query("SELECT f FROM UserFollow f WHERE f.follower.userId = :userId")
    Page<UserFollow> findFollowingByUserId(@Param("userId") String userId, Pageable pageable);

    long countByFollowingUserId(String userId);
    long countByFollowerUserId(String userId);
}
