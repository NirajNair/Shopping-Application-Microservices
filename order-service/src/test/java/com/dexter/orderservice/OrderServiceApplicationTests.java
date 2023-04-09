package com.dexter.orderservice;

import com.dexter.orderservice.dto.OrderLineItemsDto;
import com.dexter.orderservice.dto.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OrderServiceApplicationTests {


	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0.32")
			.withDatabaseName("orders")
			.withUsername("admin")
			.withPassword("password");

	@BeforeAll
	static void beforeAll(){
		mySQLContainer.start();
	}

	@AfterAll
	static void afterAll(){
		mySQLContainer.stop();
	}

	@DynamicPropertySource
	static void setProperty(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
	}


	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void shouldPlaceOrder() throws Exception {
		OrderRequest orderRequest = getOrderRequest();
		String orderRequestString = objectMapper.writeValueAsString(orderRequest);
		System.out.println(orderRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
						.contentType(MediaType.APPLICATION_JSON)
						.content(orderRequestString)
						.characterEncoding("utf-8"))
				.andExpect(status().isCreated());

//		Assertions.assertEquals(1, orderRepository.findAll().size());

	}

	private OrderRequest getOrderRequest() {
		List<OrderLineItemsDto> orderLineItemsDtoList = new ArrayList<OrderLineItemsDto>();
		OrderLineItemsDto orderLineItemsDto = OrderLineItemsDto.builder()
				.name("Iphone 13")
				.skuCode("iphone_13")
				.price(BigDecimal.valueOf(1000))
				.quantity(1)
				.build();

		orderLineItemsDtoList.add(0, orderLineItemsDto);

		return OrderRequest.builder()
				.orderLineItemsDtoList(orderLineItemsDtoList)
				.build();
	}

}