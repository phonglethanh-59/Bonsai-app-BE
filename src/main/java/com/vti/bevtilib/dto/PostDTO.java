package com.vti.bevtilib.dto;

import com.vti.bevtilib.model.CommunityPost;
import com.vti.bevtilib.model.PostImage;
import com.vti.bevtilib.model.PostTag;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class PostDTO {

    private Long id;
    private String title;
    private String content;
    private String status;

    private AuthorDTO author;

    private List<PostImageDTO> images;
    private List<String> tags;

    private int likeCount;
    private int commentCount;
    private int saveCount;
    private int shareCount;

    // Trạng thái tương tác của người dùng đang đăng nhập
    private boolean liked;
    private boolean saved;
    private boolean followingAuthor;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ──────── nested DTOs ────────
    @Getter
    @Setter
    public static class AuthorDTO {
        private String userId;
        private String username;
        private String fullName;
        private String avatarUrl;
    }

    @Getter
    @Setter
    public static class PostImageDTO {
        private Long id;
        private String imageUrl;
        private int displayOrder;
    }

    // ──────── factory methods ────────
    public static PostDTO from(CommunityPost post, String currentUserId,
                               boolean liked, boolean saved, boolean followingAuthor) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setStatus(post.getStatus().name());
        dto.setLikeCount(post.getLikeCount());
        dto.setCommentCount(post.getCommentCount());
        dto.setSaveCount(post.getSaveCount());
        dto.setShareCount(post.getShareCount());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setLiked(liked);
        dto.setSaved(saved);
        dto.setFollowingAuthor(followingAuthor);

        // Author
        AuthorDTO author = new AuthorDTO();
        author.setUserId(post.getUser().getUserId());
        author.setUsername(post.getUser().getUsername());
        if (post.getUser().getUserDetail() != null) {
            author.setFullName(post.getUser().getUserDetail().getFullName());
            author.setAvatarUrl(post.getUser().getUserDetail().getAvatar());
        }
        dto.setAuthor(author);

        // Images
        dto.setImages(post.getImages().stream()
                .map(img -> {
                    PostImageDTO imgDto = new PostImageDTO();
                    imgDto.setId(img.getId());
                    imgDto.setImageUrl(img.getImageUrl());
                    imgDto.setDisplayOrder(img.getDisplayOrder());
                    return imgDto;
                })
                .collect(Collectors.toList()));

        // Tags
        dto.setTags(post.getTags().stream()
                .map(PostTag::getTagName)
                .collect(Collectors.toList()));

        return dto;
    }
}
