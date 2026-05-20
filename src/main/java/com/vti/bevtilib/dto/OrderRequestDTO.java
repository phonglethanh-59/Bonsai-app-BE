package com.vti.bevtilib.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDTO {
    @NotBlank(message = "Địa chỉ giao hàng không được để trống.")
    private String shippingAddress;

    @NotBlank(message = "Số điện thoại không được để trống.")
    @Pattern(regexp = "^(0[3|5|7|8|9])[0-9]{8}$", message = "Số điện thoại không hợp lệ. Vui lòng nhập SĐT Việt Nam (VD: 0901234567).")
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
