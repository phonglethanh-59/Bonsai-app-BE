package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.CommunityUserProfileDTO;
import com.vti.bevtilib.exception.BusinessException;
import com.vti.bevtilib.exception.ResourceNotFoundException;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.model.UserFollow;
import com.vti.bevtilib.repository.UserFollowRepository;
import com.vti.bevtilib.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityFollowService {

    private final UserFollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void follow(String followerId, String followingId) {
        if (followerId.equals(followingId)) {
            throw new BusinessException("Không thể tự follow chính mình.");
        }
        if (followRepository.existsByFollowerUserIdAndFollowingUserId(followerId, followingId)) {
            throw new BusinessException("Bạn đã follow người dùng này rồi.");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));

        UserFollow f = new UserFollow();
        f.setFollower(follower);
        f.setFollowing(following);
        followRepository.save(f);
    }

    @Transactional
    public void unfollow(String followerId, String followingId) {
        UserFollow follow = followRepository
                .findByFollowerUserIdAndFollowingUserId(followerId, followingId)
                .orElseThrow(() -> new BusinessException("Bạn chưa follow người dùng này."));
        followRepository.delete(follow);
    }
}
