package com.dexter.cartservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(value = "cart_item")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CartItem {
    @Id
    private String skuCode;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
