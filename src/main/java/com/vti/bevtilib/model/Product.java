package com.vti.bevtilib.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Lob
    private String description;

    @Column(nullable = false, precision = 12, scale = 0)
    private BigDecimal price;

    @Column(length = 100)
    private String origin;

    @Column(length = 100)
    private String supplier;

    @Column(name = "cover_image_url")
    private String coverImage;

    @Column
    private Integer age;

    @Column
    private Integer height;

    @Column(name = "pot_type", length = 100)
    private String potType;

    @Enumerated(EnumType.STRING)
    @Column(name = "care_level", length = 20)
    private CareLevel careLevel;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean featured = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @Column(name = "average_rating")
    @ColumnDefault("0.0")
    private Double averageRating = 0.0;

    @Column(name = "review_count")
    @ColumnDefault("0")
    private Integer reviewCount = 0;

    @Lob
    @Column(name = "care_guide")
    private String careGuide;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean deleted = false;
}
