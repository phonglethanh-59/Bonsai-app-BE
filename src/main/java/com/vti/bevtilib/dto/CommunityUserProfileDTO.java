package com.vti.bevtilib.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityUserProfileDTO {

    private String userId;
    private String username;
    private String fullName;
    private String avatarUrl;
    private String bio;

    private long postCount;
    private long followerCount;
    private long followingCount;

    private boolean isFollowing; // người dùng hiện tại có đang follow không
    private boolean isOwnProfile; // đây có phải trang cá nhân của chính mình không
}
