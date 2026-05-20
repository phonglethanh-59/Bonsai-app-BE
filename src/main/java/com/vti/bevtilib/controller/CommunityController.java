package com.vti.bevtilib.controller;

import com.vti.bevtilib.dto.*;
import com.vti.bevtilib.exception.ResourceNotFoundException;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.service.CommunityCommentService;
import com.vti.bevtilib.service.CommunityFollowService;
import com.vti.bevtilib.service.CommunityPostService;
import com.vti.bevtilib.service.FileStorageService;
import com.vti.bevtilib.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityPostService postService;
    private final CommunityCommentService commentService;
    private final CommunityFollowService followService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    private static final Set<String> ALLOWED_SORT = Set.of("createdAt", "likeCount", "commentCount");

    // ════════════════════════════════════════════════
    //  FEED & POSTS
    // ════════════════════════════════════════════════

    @GetMapping("/posts")
    public ResponseEntity<Page<PostDTO>> getFeed(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            Authentication auth) {

        if (!ALLOWED_SORT.contains(sortBy)) sortBy = "createdAt";
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.DESC, sortBy));

        String currentUserId = getCurrentUserId(auth);
        return ResponseEntity.ok(postService.getFeed(tag, userId, currentUserId, pageable));
    }

    @GetMapping("/posts/following")
    public ResponseEntity<Page<PostDTO>> getFollowingFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {

        User user = getUser(auth);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(size, 50),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(postService.getFollowingFeed(user.getUserId(), pageable));
    }

    @GetMapping("/posts/saved")
    public ResponseEntity<Page<PostDTO>> getSavedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {

        User user = getUser(auth);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(size, 50));
        return ResponseEntity.ok(postService.getSavedPosts(user.getUserId(), pageable));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(postService.getPost(id, getCurrentUserId(auth)));
    }

    @PostMapping("/posts")
    public ResponseEntity<PostDTO> createPost(
            @Valid @RequestBody CreatePostRequest request,
            Authentication auth) {
        User user = getUser(auth);
        return ResponseEntity.ok(postService.createPost(user, request));
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePostRequest request,
            Authentication auth) {
        User user = getUser(auth);
        return ResponseEntity.ok(postService.updatePost(id, user, request));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication auth) {
        User user = getUser(auth);
        postService.deletePost(id, user);
        return ResponseEntity.ok(Map.of("message", "Đã xóa bài viết."));
    }

    // ════════════════════════════════════════════════
    //  INTERACTIONS
    // ════════════════════════════════════════════════

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable Long id, Authentication auth) {
        User user = getUser(auth);
        int newCount = postService.likePost(id, user);
        return ResponseEntity.ok(Map.of("liked", true, "likeCount", newCount));
    }

    @DeleteMapping("/posts/{id}/like")
    public ResponseEntity<?> unlikePost(@PathVariable Long id, Authentication auth) {
        User user = getUser(auth);
        int newCount = postService.unlikePost(id, user);
        return ResponseEntity.ok(Map.of("liked", false, "likeCount", newCount));
    }

    @PostMapping("/posts/{id}/save")
    public ResponseEntity<?> savePost(@PathVariable Long id, Authentication auth) {
        User user = getUser(auth);
        postService.savePost(id, user);
        return ResponseEntity.ok(Map.of("saved", true, "message", "Đã lưu bài viết."));
    }

    @DeleteMapping("/posts/{id}/save")
    public ResponseEntity<?> unsavePost(@PathVariable Long id, Authentication auth) {
        User user = getUser(auth);
        postService.unsavePost(id, user);
        return ResponseEntity.ok(Map.of("saved", false, "message", "Đã bỏ lưu bài viết."));
    }

    @PostMapping("/posts/{id}/share")
    public ResponseEntity<?> sharePost(@PathVariable Long id) {
        postService.sharePost(id);
        return ResponseEntity.ok(Map.of("message", "Đã ghi nhận chia sẻ."));
    }

    @PostMapping("/posts/{id}/report")
    public ResponseEntity<?> reportPost(
            @PathVariable Long id,
            @Valid @RequestBody ReportPostRequest request,
            Authentication auth) {
        User user = getUser(auth);
        postService.reportPost(id, user, request);
        return ResponseEntity.ok(Map.of("message", "Đã gửi báo cáo. Chúng tôi sẽ xem xét sớm nhất."));
    }

    // ════════════════════════════════════════════════
    //  COMMENTS
    // ════════════════════════════════════════════════

    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<Page<CommentDTO>> getComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(size, 50));
        return ResponseEntity.ok(commentService.getComments(id, pageable));
    }

    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication auth) {
        User user = getUser(auth);
        return ResponseEntity.ok(commentService.addComment(id, user, request));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, Authentication auth) {
        User user = getUser(auth);
        commentService.deleteComment(commentId, user);
        return ResponseEntity.ok(Map.of("message", "Đã xóa bình luận."));
    }

    // ════════════════════════════════════════════════
    //  FOLLOW
    // ════════════════════════════════════════════════

    @PostMapping("/users/{userId}/follow")
    public ResponseEntity<?> followUser(@PathVariable String userId, Authentication auth) {
        User me = getUser(auth);
        followService.follow(me.getUserId(), userId);
        return ResponseEntity.ok(Map.of("following", true, "message", "Đã follow."));
    }

    @DeleteMapping("/users/{userId}/follow")
    public ResponseEntity<?> unfollowUser(@PathVariable String userId, Authentication auth) {
        User me = getUser(auth);
        followService.unfollow(me.getUserId(), userId);
        return ResponseEntity.ok(Map.of("following", false, "message", "Đã unfollow."));
    }

    // ════════════════════════════════════════════════
    //  USER PROFILE
    // ════════════════════════════════════════════════

    @GetMapping("/users/{userId}/profile")
    public ResponseEntity<CommunityUserProfileDTO> getUserProfile(
            @PathVariable String userId,
            Authentication auth) {
        String currentUserId = getCurrentUserId(auth);
        return ResponseEntity.ok(postService.getUserProfile(userId, currentUserId));
    }

    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<Page<PostDTO>> getUserPosts(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Authentication auth) {
        String currentUserId = getCurrentUserId(auth);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(size, 50),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(postService.getUserPosts(userId, currentUserId, pageable));
    }

    // ════════════════════════════════════════════════
    //  TAGS / DISCOVERY
    // ════════════════════════════════════════════════

    @GetMapping("/tags/trending")
    public ResponseEntity<List<String>> getTrendingTags() {
        return ResponseEntity.ok(postService.getTrendingTags());
    }

    @GetMapping("/tags/search")
    public ResponseEntity<List<String>> searchTags(@RequestParam(defaultValue = "") String q) {
        return ResponseEntity.ok(postService.searchTags(q));
    }

    // ════════════════════════════════════════════════
    //  IMAGE UPLOAD
    // ════════════════════════════════════════════════

    @PostMapping("/upload/image")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            Authentication auth) {
        getUser(auth); // ensure authenticated
        try {
            String url = fileStorageService.saveFile(file, "community");
            return ResponseEntity.ok(Map.of("imageUrl", url));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════

    private User getUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new com.vti.bevtilib.exception.AccessDeniedException("Bạn cần đăng nhập để thực hiện thao tác này.");
        }
        return userService.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
    }

    private String getCurrentUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        return userService.findByUsername(auth.getName())
                .map(User::getUserId)
                .orElse(null);
    }
}
