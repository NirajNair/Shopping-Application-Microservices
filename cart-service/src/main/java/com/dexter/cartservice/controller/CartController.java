package com.dexter.cartservice.controller;

import com.dexter.cartservice.dto.CartRequest;
import com.dexter.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addItemsToCart(@RequestBody CartRequest cartRequest) {
        cartService.addItemsToCart(cartRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteCartItem(@RequestParam("userId") String userId, @RequestParam("item") String skuCode) {
        cartService.deleteCartItem(userId, skuCode);
    }
}
