package com.dexter.orderservice.service;

import com.dexter.orderservice.dto.InventoryResponse;
import com.dexter.orderservice.dto.OrderLineItemsDto;
import com.dexter.orderservice.dto.OrderRequest;
import com.dexter.orderservice.model.Order;
import com.dexter.orderservice.model.OrderLineItems;
import com.dexter.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;



    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItemsDto> orderLineItemsDtoList = orderRequest.getOrderLineItemsDtoList();
        List<OrderLineItems> orderLineItems = new ArrayList<>(orderLineItemsDtoList.size());
        for (OrderLineItemsDto orderLineItemsDto : orderLineItemsDtoList) {
            OrderLineItems orderLineItem = new OrderLineItems();
            orderLineItem.setSkuCode(orderLineItemsDto.getSkuCode());
            orderLineItem.setPrice(orderLineItemsDto.getPrice());
            orderLineItem.setQuantity(orderLineItemsDto.getQuantity());
            orderLineItems.add(orderLineItem);
        }

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodeList = orderLineItems.stream()
                .map(OrderLineItems::getSkuCode)
                .toList();
        List<Integer> quantity = orderLineItems.stream()
                .map(OrderLineItems::getQuantity)
                .toList();

        // Call inventory-service and place order if item in stock
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodeList)
                                .queryParam("quantity", quantity)
                                .build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        assert inventoryResponseArray != null;
        Boolean allProductInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::getIsInStock);

        if(allProductInStock) {
            log.info("Order {} created.", order.getOrderNumber());
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock currently! Please try again later.");
        }


    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto){
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItems.getPrice());
        orderLineItems.setQuantity(orderLineItems.getQuantity());
        orderLineItems.setName(orderLineItems.getName());
        orderLineItems.setSkuCode(orderLineItems.getSkuCode());
        return orderLineItems;
    }
}
