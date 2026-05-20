package com.vti.bevtilib.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "post_tags",
        indexes = {
                @Index(name = "idx_tag_post", columnList = "post_id"),
                @Index(name = "idx_tag_name", columnList = "tag_name")
        })
@Getter
@Setter
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost post;

    @Column(name = "tag_name", nullable = false, length = 100)
    private String tagName;
}
