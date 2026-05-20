package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    // Comment gốc (không phải reply) của bài viết
    @Query("SELECT c FROM PostComment c WHERE c.post.id = :postId AND c.parentComment IS NULL AND c.deleted = false ORDER BY c.createdAt ASC")
    Page<PostComment> findRootCommentsByPostId(@Param("postId") Long postId, Pageable pageable);

    // Replies của một comment
    @Query("SELECT c FROM PostComment c WHERE c.parentComment.id = :parentId AND c.deleted = false ORDER BY c.createdAt ASC")
    Page<PostComment> findRepliesByParentId(@Param("parentId") Long parentId, Pageable pageable);

    Optional<PostComment> findByIdAndDeletedFalse(Long id);
}
