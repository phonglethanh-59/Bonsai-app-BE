package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.*;
import com.vti.bevtilib.exception.BusinessException;
import com.vti.bevtilib.exception.ResourceNotFoundException;
import com.vti.bevtilib.model.*;
import com.vti.bevtilib.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Note: This service uses @Transactional(readOnly = true) on class level.
// Write methods override with @Transactional.

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityPostServiceImpl implements CommunityPostService {

    private final CommunityPostRepository postRepository;
    private final PostLikeRepository likeRepository;
    private final PostSaveRepository saveRepository;
    private final UserFollowRepository followRepository;
    private final PostReportRepository reportRepository;
    private final UserRepository userRepository;

    // ──────── FEED ────────────────────────────────────────────────

    @Override
    public Page<PostDTO> getFeed(String tag, String authorId, String currentUserId, Pageable pageable) {
        Page<CommunityPost> page;
        if (tag != null && !tag.isBlank()) {
            page = postRepository.findByTag(tag.toLowerCase().trim(), pageable);
        } else if (authorId != null && !authorId.isBlank()) {
            page = postRepository.findByUserUserIdAndDeletedFalse(authorId, pageable);
        } else {
            page = postRepository.findPublishedFeed(pageable);
        }
        return page.map(post -> toDTO(post, currentUserId));
    }

    @Override
    public Page<PostDTO> getFollowingFeed(String currentUserId, Pageable pageable) {
        return postRepository.findFollowingFeed(currentUserId, pageable)
                .map(post -> toDTO(post, currentUserId));
    }

    @Override
    public Page<PostDTO> getSavedPosts(String currentUserId, Pageable pageable) {
        return postRepository.findSavedPosts(currentUserId, pageable)
                .map(post -> toDTO(post, currentUserId));
    }

    @Override
    public Page<PostDTO> getUserPosts(String userId, String currentUserId, Pageable pageable) {
        return postRepository.findByUserUserIdAndDeletedFalse(userId, pageable)
                .map(post -> toDTO(post, currentUserId));
    }

    @Override
    public PostDTO getPost(Long id, String currentUserId) {
        CommunityPost post = postRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết."));
        return toDTO(post, currentUserId);
    }

    // ──────── CRUD ────────────────────────────────────────────────

    @Override
    @Transactional
    public PostDTO createPost(User user, CreatePostRequest request) {
        CommunityPost post = new CommunityPost();
        post.setUser(user);
        post.setTitle(request.getTitle().trim());
        post.setContent(request.getContent().trim());
        post.setStatus(CommunityPost.PostStatus.PUBLISHED);

        // Images
        if (request.getImageUrls() != null) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                PostImage img = new PostImage();
                img.setPost(post);
                img.setImageUrl(request.getImageUrls().get(i));
                img.setDisplayOrder(i);
                post.getImages().add(img);
            }
        }

        // Tags - normalize: lowercase, trim, remove leading #
        if (request.getTags() != null) {
            request.getTags().stream()
                    .map(t -> t.replaceAll("^#", "").toLowerCase().trim())
                    .filter(t -> !t.isBlank())
                    .distinct()
                    .forEach(t -> {
                        PostTag tag = new PostTag();
                        tag.setPost(post);
                        tag.setTagName(t);
                        post.getTags().add(tag);
                    });
        }

        postRepository.save(post);
        // Fetch lại để tránh LazyInitializationException khi convert DTO
        CommunityPost fresh = postRepository.findByIdAndDeletedFalse(post.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lỗi khi tạo bài viết."));
        return toDTO(fresh, user.getUserId());
    }

    @Override
    @Transactional
    public PostDTO updatePost(Long id, User user, UpdatePostRequest request) {
        CommunityPost post = postRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết."));

        boolean isOwner = post.getUser().getUserId().equals(user.getUserId());
        boolean isAdmin = user.getRole().contains("ADMIN");
        if (!isOwner && !isAdmin) {
            throw new BusinessException("Bạn không có quyền chỉnh sửa bài viết này.");
        }

        if (request.getTitle() != null) post.setTitle(request.getTitle().trim());
        if (request.getContent() != null) post.setContent(request.getContent().trim());

        if (request.getImageUrls() != null) {
            post.getImages().clear();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                PostImage img = new PostImage();
                img.setPost(post);
                img.setImageUrl(request.getImageUrls().get(i));
                img.setDisplayOrder(i);
                post.getImages().add(img);
            }
        }

        if (request.getTags() != null) {
            post.getTags().clear();
            request.getTags().stream()
                    .map(t -> t.replaceAll("^#", "").toLowerCase().trim())
                    .filter(t -> !t.isBlank())
                    .distinct()
                    .forEach(t -> {
                        PostTag tag = new PostTag();
                        tag.setPost(post);
                        tag.setTagName(t);
                        post.getTags().add(tag);
                    });
        }

        postRepository.save(post);
        CommunityPost fresh = postRepository.findByIdAndDeletedFalse(post.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lỗi khi cập nhật bài viết."));
        return toDTO(fresh, user.getUserId());
    }

    @Override
    @Transactional
    public void deletePost(Long id, User user) {
        CommunityPost post = postRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết."));

        boolean isAdmin = user.getRole().contains("ADMIN");
        if (!post.getUser().getUserId().equals(user.getUserId()) && !isAdmin) {
            throw new BusinessException("Bạn không có quyền xóa bài viết này.");
        }

        post.setDeleted(true);
        postRepository.save(post);
    }

    // ──────── INTERACTIONS ────────────────────────────────────────

    @Override
    @Transactional
    public int likePost(Long postId, User user) {
        CommunityPost post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết."));

        if (likeRepository.existsByPostIdAndUserUserId(postId, user.getUserId())) {
            throw new BusinessException("Bạn đã thích bài viết này rồi.");
        }

        PostLike like = new PostLike();
        like.setPost(post);
        like.setUser(user);
        likeRepository.save(like);

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
        return post.getLikeCount();
    }

    @Override
    @Transactional
    public int unlikePost(Long postId, User user) {
        CommunityPost post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết."));

        PostLike like = likeRepository.findByPostIdAndUserUserId(postId, user.getUserId())
                .orElseThrow(() -> new BusinessException("Bạn chưa thích bài viết này."));

        likeRepository.delete(like);
        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        postRepository.save(post);
        return post.getLikeCount();
    }

    @Override
    @Transactional
    public void savePost(Long postId, User user) {
        CommunityPost post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết."));

        if (saveRepository.existsByPostIdAndUserUserId(postId, user.getUserId())) {
            throw new BusinessException("Bạn đã lưu bài viết này rồi.");
        }

        PostSave save = new PostSave();
        save.setPost(post);
        save.setUser(user);
        saveRepository.save(save);

        post.setSaveCount(post.getSaveCount() + 1);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void unsavePost(Long postId, User user) {
        CommunityPost post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết."));

        PostSave save = saveRepository.findByPostIdAndUserUserId(postId, user.getUserId())
                .orElseThrow(() -> new BusinessException("Bạn chưa lưu bài viết này."));

        saveRepository.delete(save);
        post.setSaveCount(Math.max(0, post.getSaveCount() - 1));
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void sharePost(Long postId) {
        CommunityPost post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết."));
        post.setShareCount(post.getShareCount() + 1);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void reportPost(Long postId, User user, ReportPostRequest request) {
        CommunityPost post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết."));

        if (reportRepository.existsByPostIdAndUserUserId(postId, user.getUserId())) {
            throw new BusinessException("Bạn đã báo cáo bài viết này rồi.");
        }

        PostReport report = new PostReport();
        report.setPost(post);
        report.setUser(user);
        report.setReason(request.getReason());
        report.setDescription(request.getDescription());
        reportRepository.save(report);
    }

    // ──────── TAGS ────────────────────────────────────────────────

    @Override
    public List<String> getTrendingTags() {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        Pageable pageable = PageRequest.of(0, 20);
        return postRepository.findTrendingTags(since, pageable).stream()
                .map(row -> (String) row[0])
                .collect(Collectors.toList());
    }

    @Override
    public List<String> searchTags(String q) {
        return postRepository.searchTags(q == null ? "" : q.trim(), PageRequest.of(0, 10));
    }

    // ──────── USER PROFILE ────────────────────────────────────────

    @Override
    public CommunityUserProfileDTO getUserProfile(String userId, String currentUserId) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));

        CommunityUserProfileDTO dto = new CommunityUserProfileDTO();
        dto.setUserId(targetUser.getUserId());
        dto.setUsername(targetUser.getUsername());
        if (targetUser.getUserDetail() != null) {
            dto.setFullName(targetUser.getUserDetail().getFullName());
            dto.setAvatarUrl(targetUser.getUserDetail().getAvatar());
        }

        // Dùng count query thay vì tải toàn bộ dữ liệu
        long postCount = postRepository.countByUserUserIdAndDeletedFalse(userId);
        dto.setPostCount(postCount);
        dto.setFollowerCount(followRepository.countByFollowingUserId(userId));
        dto.setFollowingCount(followRepository.countByFollowerUserId(userId));

        if (currentUserId != null) {
            dto.setFollowing(followRepository.existsByFollowerUserIdAndFollowingUserId(currentUserId, userId));
            dto.setOwnProfile(currentUserId.equals(userId));
        }

        return dto;
    }

    // ──────── HELPER ──────────────────────────────────────────────

    private PostDTO toDTO(CommunityPost post, String currentUserId) {
        boolean liked = false;
        boolean saved = false;
        boolean followingAuthor = false;

        if (currentUserId != null) {
            liked = likeRepository.existsByPostIdAndUserUserId(post.getId(), currentUserId);
            saved = saveRepository.existsByPostIdAndUserUserId(post.getId(), currentUserId);
            followingAuthor = followRepository.existsByFollowerUserIdAndFollowingUserId(
                    currentUserId, post.getUser().getUserId());
        }

        return PostDTO.from(post, currentUserId, liked, saved, followingAuthor);
    }
}
