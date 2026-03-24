package com.vti.bevtilib.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private String productImage;
    private int quantity;
    private BigDecimal price;
}
