package com.vti.bevtilib.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToWishlistRequest {
    @NotNull(message = "productId không được để trống")
    private Long productId;
}
