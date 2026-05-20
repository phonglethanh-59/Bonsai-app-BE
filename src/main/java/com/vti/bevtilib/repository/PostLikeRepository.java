package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostIdAndUserUserId(Long postId, String userId);
    Optional<PostLike> findByPostIdAndUserUserId(Long postId, String userId);
}
