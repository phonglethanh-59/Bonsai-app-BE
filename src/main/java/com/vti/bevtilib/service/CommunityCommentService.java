package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.*;
import com.vti.bevtilib.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommunityCommentService {

    Page<CommentDTO> getComments(Long postId, Pageable pageable);

    CommentDTO addComment(Long postId, User user, CreateCommentRequest request);

    void deleteComment(Long commentId, User user);
}
