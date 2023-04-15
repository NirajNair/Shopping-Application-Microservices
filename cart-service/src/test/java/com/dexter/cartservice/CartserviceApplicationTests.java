package com.dexter.cartservice;

import com.dexter.cartservice.dto.CartRequest;
import com.dexter.cartservice.model.Cart;
import com.dexter.cartservice.model.CartItem;
import com.dexter.cartservice.repository.CartRepository;
import com.dexter.cartservice.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.el.parser.AstSetData;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@Transactional
class CartserviceApplicationTests {

	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

	static {
		mongoDBContainer.start();
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartService cartService;

	@DynamicPropertySource
	static void setProperty(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

	}

	@BeforeAll
	public void setup() {
		Cart cart1 = new Cart();
		cart1.setUserId("12345");
		List<CartItem> cartItemList1 = new ArrayList<>();
		cartItemList1.add(new CartItem(
				"product-1",
				"Product 1",
				new BigDecimal(1000),
				1
		));

		cart1.setCartItems(cartItemList1);
		cartRepository.save(cart1);

	}

	@Test
	void shouldAddItemToCart() throws Exception {
		CartRequest cartRequest =  getCartRequest("product-1", "Product 1");
		String cartRequestString = objectMapper.writeValueAsString(cartRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/cart")
				.contentType(MediaType.APPLICATION_JSON)
				.content(cartRequestString))
				.andExpect(status().isCreated());

		Optional<Cart> opitonalCart = cartRepository.findByUserId("12345");
		int qty = getQuantityOfItem(opitonalCart, "product-1");
//		Assertions.assertEquals(2, qty);

		CartRequest cartRequest1 =  getCartRequest("product-2", "Product 2");
		String cartRequestString1 = objectMapper.writeValueAsString(cartRequest1);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/cart")
						.contentType(MediaType.APPLICATION_JSON)
						.content(cartRequestString1))
				.andExpect(status().isCreated());

//		Assertions.assertEquals(1, getQuantityOfItem(cartRepository.findByUserId("12345"), "product-2"));
	}

	@Test
	void shouldDeleteCartItem() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/cart?userId=12345&item=product-1"))
				.andExpect(status().isOk());
//		Assertions.assertEquals(1, getQuantityOfItem(cartRepository.findByUserId("12345"), "product-1"));

		mockMvc.perform(MockMvcRequestBuilders.get("/api/cart?userId=12345&item=product-1"))
				.andExpect(status().isOk());
//		Assertions.assertEquals(0, getQuantityOfItem(cartRepository.findByUserId("12345"), "product-1"));

		mockMvc.perform(MockMvcRequestBuilders.get("/api/cart?userId=12345&item=product-2"))
				.andExpect(status().isOk());
//		Assertions.assertNull(cartRepository.findByUserId("12345").get().getCartItems());
	}

	private int getQuantityOfItem(Optional<Cart> cart, String skuCode) {
		if(cart.isPresent()) {
			List<CartItem> cartItemList = cart.get().getCartItems();
			Optional<CartItem> targetCartItem = cartItemList.stream()
					.filter(cartItem -> cartItem != null && cartItem.getSkuCode().equals(skuCode))
					.findFirst();
			if(targetCartItem.isPresent()){
				return targetCartItem.get().getQuantity();
			}
		}
		return 10;
	}

	private CartRequest getCartRequest(String skuCode, String name) {
		return CartRequest.builder()
				.userId("12345")
				.skuCode(skuCode)
				.name(name)
				.price(new BigDecimal(1000))
				.quantity(1)
				.build();
	}

}
