package com.dexter.cartservice.service;

import com.dexter.cartservice.dto.CartRequest;
import com.dexter.cartservice.model.Cart;
import com.dexter.cartservice.model.CartItem;
import com.dexter.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public void addItemsToCart(CartRequest cartRequest) {
       Optional<Cart> userCart = cartRepository.findByUserIdWithCartItems(cartRequest.getUserId());
       if(userCart.isPresent()) {
           Cart cart = userCart.get();
           List<CartItem> cartItemList = cart.getCartItems();

           if(cartItemList != null) {
               Optional<CartItem> optionalCartItem = cartItemList.stream()
                       .filter(cartItem -> cartItem != null && cartItem.getSkuCode().equals(cartRequest.getSkuCode()))
                       .findFirst();

               if(optionalCartItem.isPresent()) {
                   CartItem existingCartItem = optionalCartItem.get();
                   existingCartItem.setQuantity(existingCartItem.getQuantity()+1);
               } else {
                   CartItem newCartItem = new CartItem(
                           cartRequest.getSkuCode(),
                           cartRequest.getName(),
                           cartRequest.getPrice(),
                           cartRequest.getQuantity()
                   );
                   cartItemList.add(newCartItem);
                   cart.setCartItems(cartItemList);
               }
           } else {
               CartItem newCartItem = new CartItem(
                       cartRequest.getSkuCode(),
                       cartRequest.getName(),
                       cartRequest.getPrice(),
                       cartRequest.getQuantity()
               );
               List<CartItem> newCartItemList = new ArrayList<>();
               newCartItemList.add(newCartItem);
               cart.setCartItems(newCartItemList);
           }
           cartRepository.save(cart);
       } else {
           Cart cart = new Cart();
           cart.setUserId(cartRequest.getUserId());
           CartItem cartItem = new CartItem(
                   cartRequest.getSkuCode(),
                   cartRequest.getName(),
                   cartRequest.getPrice(),
                   cartRequest.getQuantity()
           );
           List<CartItem> cartItemList = new ArrayList<>();
           cartItemList.add(cartItem);
           cartRepository.save(cart);
       }
    }

    public void deleteCartItem(String userId, String skuCode) {
        Optional<Cart> userCart = cartRepository.findByUserId(userId);
        if(userCart.isPresent()) {
            Cart cart = userCart.get();
            List<CartItem> cartItemList = cart.getCartItems();

            if(cartItemList.size()>0) {
                cartItemList.stream()
                        .filter(cartItem -> cartItem != null && cartItem.getSkuCode().equals(skuCode))
                        .findFirst()
                        .ifPresent(cartItem -> {
                            int newQuantity = cartItem.getQuantity()-1;
                            if(newQuantity <= 0) {
                                cartItemList.remove(cartItem);
                            } else {
                                cartItem.setQuantity(newQuantity);
                            }
                            cart.setCartItems(cartItemList);
                            cartRepository.save(cart);
                        });
            }


        }
    }
}
