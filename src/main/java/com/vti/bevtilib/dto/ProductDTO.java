package com.vti.bevtilib.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDTO {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private String origin;
    private String supplier;
    private String coverImage;
    private BigDecimal price;
    private Integer age;
    private Integer height;
    private String potType;
    private int stockQuantity;
    private String careLevel;
    private boolean featured;
    private Double averageRating;
    private Integer reviewCount;
    private String careGuide;
    private CategoryDTO category;
    private LocalDateTime createdAt;
}
