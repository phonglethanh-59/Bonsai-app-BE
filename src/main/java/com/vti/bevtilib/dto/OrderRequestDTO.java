package com.vti.bevtilib.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDTO {
    @NotBlank(message = "Địa chỉ giao hàng không được để trống.")
    private String shippingAddress;

    @NotBlank(message = "Số điện thoại không được để trống.")
    private String phone;

    private String note;
    private String paymentMethod;

    @NotEmpty(message = "Vui lòng chọn ít nhất một sản phẩm.")
    @Valid
    private List<OrderItemRequestDTO> items;

    @Data
    public static class OrderItemRequestDTO {
        @NotNull(message = "Product ID không được để trống.")
        private Long productId;

        @Positive(message = "Số lượng phải lớn hơn 0.")
        private int quantity;
    }
}
