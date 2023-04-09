package com.dexter.orderservice.service;

import com.dexter.orderservice.dto.OrderLineItemsDto;
import com.dexter.orderservice.dto.OrderRequest;
import com.dexter.orderservice.model.Order;
import com.dexter.orderservice.model.OrderLineItems;
import com.dexter.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);
        log.info("Order {} created.", order.getOrderNumber());


        orderRepository.save(order);
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
