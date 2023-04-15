package com.dexter.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CartRequest {
    private String userId;
    private String name;
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
}
