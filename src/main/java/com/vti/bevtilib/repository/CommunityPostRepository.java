package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    // Feed chính — tất cả bài published, chưa xoá
    @Query("SELECT p FROM CommunityPost p WHERE p.status = 'PUBLISHED' AND p.deleted = false ORDER BY p.createdAt DESC")
    Page<CommunityPost> findPublishedFeed(Pageable pageable);

    // Feed theo tag
    @Query("SELECT p FROM CommunityPost p JOIN p.tags t WHERE t.tagName = :tag AND p.status = 'PUBLISHED' AND p.deleted = false")
    Page<CommunityPost> findByTag(@Param("tag") String tag, Pageable pageable);

    // Feed của các user đang follow
    @Query("""
            SELECT p FROM CommunityPost p
            WHERE p.user.userId IN (
                SELECT f.following.userId FROM UserFollow f WHERE f.follower.userId = :userId
            )
            AND p.status = 'PUBLISHED' AND p.deleted = false
            ORDER BY p.createdAt DESC
            """)
    Page<CommunityPost> findFollowingFeed(@Param("userId") String userId, Pageable pageable);

    // Bài của một user cụ thể
    Page<CommunityPost> findByUserUserIdAndDeletedFalse(String userId, Pageable pageable);

    // Count query cho profile (thay cho unpaged load)
    long countByUserUserIdAndDeletedFalse(String userId);


    // Bài đã lưu
    @Query("""
            SELECT p FROM CommunityPost p
            JOIN PostSave s ON s.post.id = p.id
            WHERE s.user.userId = :userId AND p.deleted = false
            ORDER BY s.createdAt DESC
            """)
    Page<CommunityPost> findSavedPosts(@Param("userId") String userId, Pageable pageable);

    // Bài cụ thể, chưa xoá
    Optional<CommunityPost> findByIdAndDeletedFalse(Long id);

    // Trending tags (trong 7 ngày gần nhất)
    @Query("""
            SELECT t.tagName, COUNT(t) as cnt FROM PostTag t
            WHERE t.post.createdAt >= :since AND t.post.deleted = false AND t.post.status = 'PUBLISHED'
            GROUP BY t.tagName ORDER BY cnt DESC
            """)
    List<Object[]> findTrendingTags(@Param("since") LocalDateTime since, Pageable pageable);

    // Tìm kiếm tag
    @Query("SELECT DISTINCT t.tagName FROM PostTag t WHERE t.tagName LIKE %:q% ORDER BY t.tagName")
    List<String> searchTags(@Param("q") String q, Pageable pageable);
}
