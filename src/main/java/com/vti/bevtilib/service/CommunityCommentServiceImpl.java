package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.CommentDTO;
import com.vti.bevtilib.dto.CreateCommentRequest;
import com.vti.bevtilib.exception.BusinessException;
import com.vti.bevtilib.exception.ResourceNotFoundException;
import com.vti.bevtilib.model.CommunityPost;
import com.vti.bevtilib.model.PostComment;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.repository.CommunityPostRepository;
import com.vti.bevtilib.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityCommentServiceImpl implements CommunityCommentService {

    private final PostCommentRepository commentRepository;
    private final CommunityPostRepository postRepository;

    @Override
    public Page<CommentDTO> getComments(Long postId, Pageable pageable) {
        // Lấy root comments, mỗi comment kèm replies (tối đa 5 replies)
        Page<PostComment> rootComments = commentRepository.findRootCommentsByPostId(postId, pageable);

        return rootComments.map(comment -> {
            CommentDTO dto = CommentDTO.from(comment);
            // Lấy replies
            List<CommentDTO> replies = commentRepository
                    .findRepliesByParentId(comment.getId(), PageRequest.of(0, 50))
                    .stream()
                    .map(CommentDTO::from)
                    .collect(Collectors.toList());
            dto.setReplies(replies);
            return dto;
        });
    }

    @Override
    @Transactional
    public CommentDTO addComment(Long postId, User user, CreateCommentRequest request) {
        CommunityPost post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết."));

        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(request.getContent().trim());

        if (request.getParentCommentId() != null) {
            PostComment parent = commentRepository.findByIdAndDeletedFalse(request.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bình luận gốc."));
            // Chỉ reply tối đa 1 cấp
            if (parent.getParentComment() != null) {
                comment.setParentComment(parent.getParentComment());
            } else {
                comment.setParentComment(parent);
            }
        }

        PostComment saved = commentRepository.save(comment);

        // Cập nhật comment count
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return CommentDTO.from(saved);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, User user) {
        PostComment comment = commentRepository.findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bình luận."));

        boolean isAdmin = user.getRole().contains("ADMIN");
        if (!comment.getUser().getUserId().equals(user.getUserId()) && !isAdmin) {
            throw new BusinessException("Bạn không có quyền xóa bình luận này.");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);

        // Giảm comment count
        CommunityPost post = comment.getPost();
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
        postRepository.save(post);
    }
}
