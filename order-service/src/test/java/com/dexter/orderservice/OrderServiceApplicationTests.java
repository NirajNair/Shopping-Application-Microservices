package com.dexter.orderservice;

import com.dexter.orderservice.dto.InventoryResponse;
import com.dexter.orderservice.dto.OrderLineItemsDto;
import com.dexter.orderservice.dto.OrderRequest;
import com.dexter.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;

import static org.mockito.Mockito.mock;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OrderServiceApplicationTests {


	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0.32")
			.withDatabaseName("orders")
			.withUsername("admin")
			.withPassword("password");

	@Autowired
	private TestRestTemplate restTemplate;

	private static WireMockServer wireMockServer = new WireMockServer(options().port(8089));;

	@BeforeAll
	static void beforeAll(WireMockServer wireMockServer){
		mySQLContainer.start();
		wireMockServer.start();
		OrderServiceApplicationTests.wireMockServer = wireMockServer;
		wireMockServer.stubFor(get(urlPathEqualTo("http://inventory-service/api/inventory"))
				.withQueryParam("skuCode", equalTo("iphone_13"))
				.withQueryParam("quantity", equalTo("2"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody("[{\"skuCode\": \"iphone_13\", \"isInStock\": true}]")));
	}

	@AfterAll
	static void afterAll(){
		mySQLContainer.stop();
		wireMockServer.stop();
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

	@Mock
	private WebClient webClient;

	@InjectMocks
	private OrderService orderService;

	@Test
	void shouldPlaceOrder() throws Exception {
		OrderRequest orderRequest = getOrderRequest();
//		String orderRequestString = objectMapper.writeValueAsString(orderRequest);
		System.out.println(orderRequest);
		URI location = restTemplate.postForLocation("/api/order", orderRequest);
		URI locationUri = URI.create(location.toString());
		int statusCode = locationUri.getPath().endsWith("/1") ? HttpStatus.CREATED.value() : HttpStatus.INTERNAL_SERVER_ERROR.value();

		Assertions.assertEquals(HttpStatus.CREATED.value(), statusCode);
//		mockMvc.perform(MockMvcRequestBuilders.post("/api/order?skuCode=iphone_13&quantity=2")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(orderRequestString)
//						.characterEncoding("utf-8"))
//				.andExpect(status().isCreated());

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