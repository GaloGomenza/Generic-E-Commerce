package com.generic.e_commerce.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {
    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private Integer quantity;
}
