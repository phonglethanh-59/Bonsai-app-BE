package com.vti.bevtilib.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDTO {
    private Long id;
    private String username;
    private String customerName;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String phone;
    private String note;
    private String paymentMethod;
    private String status;
    private List<OrderItemDTO> items;
}
