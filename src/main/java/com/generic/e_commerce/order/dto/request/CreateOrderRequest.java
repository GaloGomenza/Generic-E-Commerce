package com.generic.e_commerce.order.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {
    private List<Long> cartItemIds;
}
