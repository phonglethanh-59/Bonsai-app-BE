package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.PostSave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostSaveRepository extends JpaRepository<PostSave, Long> {
    boolean existsByPostIdAndUserUserId(Long postId, String userId);
    Optional<PostSave> findByPostIdAndUserUserId(Long postId, String userId);
}
