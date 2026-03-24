package com.vti.bevtilib.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDTO {
    private String shippingAddress;
    private String phone;
    private String note;
    private String paymentMethod;
    private List<OrderItemRequestDTO> items;

    @Data
    public static class OrderItemRequestDTO {
        private Long productId;
        private int quantity;
    }
}
