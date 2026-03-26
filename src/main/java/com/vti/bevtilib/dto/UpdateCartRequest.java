package com.vti.bevtilib.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartRequest {
    @NotNull(message = "productId không được để trống")
    private Long productId;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0. Dùng DELETE /api/cart/{productId} để xóa.")
    private int quantity;
}
