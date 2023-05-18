package com.dexter.cartservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document(value = "cart")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Cart {
    @Id
    private String userId;

    @DBRef(lazy = false)
    private List<CartItem> cartItems;
}
