package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.*;
import com.vti.bevtilib.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommunityPostService {

    Page<PostDTO> getFeed(String tag, String authorId, String currentUserId, Pageable pageable);

    Page<PostDTO> getFollowingFeed(String currentUserId, Pageable pageable);

    Page<PostDTO> getSavedPosts(String currentUserId, Pageable pageable);

    Page<PostDTO> getUserPosts(String userId, String currentUserId, Pageable pageable);

    PostDTO getPost(Long id, String currentUserId);

    PostDTO createPost(User user, CreatePostRequest request);

    PostDTO updatePost(Long id, User user, UpdatePostRequest request);

    void deletePost(Long id, User user);

    // Interactions
    int likePost(Long postId, User user);

    int unlikePost(Long postId, User user);

    void savePost(Long postId, User user);

    void unsavePost(Long postId, User user);

    void sharePost(Long postId);

    void reportPost(Long postId, User user, ReportPostRequest request);

    // Tags
    List<String> getTrendingTags();

    List<String> searchTags(String q);

    // User profile
    CommunityUserProfileDTO getUserProfile(String userId, String currentUserId);
}
