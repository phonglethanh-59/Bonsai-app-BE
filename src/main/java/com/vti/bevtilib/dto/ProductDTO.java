package com.vti.bevtilib.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String origin;
    private String coverImage;
    private BigDecimal price;
    private int stockQuantity;
    private String careLevel;
    private boolean featured;
    private Double averageRating;
    private Integer reviewCount;
    private CategoryDTO category;
    private LocalDateTime createdAt;
}
