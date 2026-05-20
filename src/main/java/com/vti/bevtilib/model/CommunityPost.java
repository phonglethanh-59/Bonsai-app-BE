package com.vti.bevtilib.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "community_posts",
        indexes = {
                @Index(name = "idx_post_user", columnList = "user_id"),
                @Index(name = "idx_post_status", columnList = "status"),
                @Index(name = "idx_post_created", columnList = "created_at")
        })
@Getter
@Setter
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @ColumnDefault("'PUBLISHED'")
    private PostStatus status = PostStatus.PUBLISHED;

    @Column(name = "like_count")
    @ColumnDefault("0")
    private int likeCount = 0;

    @Column(name = "comment_count")
    @ColumnDefault("0")
    private int commentCount = 0;

    @Column(name = "save_count")
    @ColumnDefault("0")
    private int saveCount = 0;

    @Column(name = "share_count")
    @ColumnDefault("0")
    private int shareCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ColumnDefault("false")
    private boolean deleted = false;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PostTag> tags = new ArrayList<>();

    public enum PostStatus {
        PUBLISHED, DRAFT, HIDDEN, REMOVED
    }
}
