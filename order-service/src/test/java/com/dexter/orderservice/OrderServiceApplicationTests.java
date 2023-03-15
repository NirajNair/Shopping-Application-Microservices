package com.dexter.orderservice;

import com.dexter.orderservice.dto.OrderLineItemsDto;
import com.dexter.orderservice.dto.OrderRequest;
import com.dexter.orderservice.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OrderServiceApplicationTests.class)
@AutoConfigureMockMvc
@Testcontainers

class OrderServiceApplicationTests {

	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0.32");

	static {
		mySQLContainer.start();
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private OrderRepository orderRepository;

	@DynamicPropertySource
	static void setProperty(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.datasource.url",
				() -> String.format("jdbc:mysql://localhost:%d/orders", mySQLContainer.getFirstMappedPort()));
		dynamicPropertyRegistry.add("spring.datasource.user", () -> "admin");
		dynamicPropertyRegistry.add("spring.datasource.password", () -> "password");
	}

	@Test
	void shouldPlaceOrder() throws Exception {
		OrderRequest orderRequest = getOrderRequest();
		String orderRequestString = objectMapper.writeValueAsString(orderRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
				.contentType(MediaType.APPLICATION_JSON)
				.content(orderRequestString))
				.andExpect(status().isCreated());

		Assertions.assertEquals(1, orderRepository.findAll().size());

	}

	private OrderRequest getOrderRequest() {
		List<OrderLineItemsDto> orderLineItemsDtoList = new ArrayList<OrderLineItemsDto>();
		OrderLineItemsDto orderLineItemsDto = OrderLineItemsDto.builder()
				.name("Iphone")
				.price(BigDecimal.valueOf(1000))
				.quantity(1)
				.build();

		orderLineItemsDtoList.add(0, orderLineItemsDto);

		return OrderRequest.builder()
				.orderLineItemsDtoList(orderLineItemsDtoList)
				.build();
	}

}
