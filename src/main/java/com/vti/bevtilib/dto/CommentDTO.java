package com.vti.bevtilib.dto;

import com.vti.bevtilib.model.PostComment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CommentDTO {

    private Long id;
    private Long parentCommentId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private AuthorDTO author;
    private List<CommentDTO> replies = new ArrayList<>();

    @Getter
    @Setter
    public static class AuthorDTO {
        private String userId;
        private String username;
        private String fullName;
        private String avatarUrl;
    }

    public static CommentDTO from(PostComment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        if (comment.getParentComment() != null) {
            dto.setParentCommentId(comment.getParentComment().getId());
        }

        AuthorDTO author = new AuthorDTO();
        author.setUserId(comment.getUser().getUserId());
        author.setUsername(comment.getUser().getUsername());
        if (comment.getUser().getUserDetail() != null) {
            author.setFullName(comment.getUser().getUserDetail().getFullName());
            author.setAvatarUrl(comment.getUser().getUserDetail().getAvatar());
        }
        dto.setAuthor(author);

        return dto;
    }
}
