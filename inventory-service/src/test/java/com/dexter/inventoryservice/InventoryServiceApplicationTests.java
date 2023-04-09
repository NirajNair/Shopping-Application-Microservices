package com.dexter.inventoryservice;

import com.dexter.inventoryservice.repository.InventoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class InventoryServiceApplicationTests {
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0.32")
			.withDatabaseName("inventory")
			.withUsername("admin")
			.withPassword("password");

	@BeforeAll
	public static void beforeAll() {
		mySQLContainer.start();
	}

	@AfterAll
	public static void afterAll() {
		mySQLContainer.stop();
	}

	@DynamicPropertySource
	static void setProperty(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasourcse.password", mySQLContainer::getPassword);

	}
	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldCheckIfInStock() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory/{skuCode}", "iphone_13"))
				.andExpect(status().isOk())
				.andExpect(content().string("true"));

		mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory/{skuCode}", "note_22"))
				.andExpect(status().isOk())
				.andExpect(content().string("false"));
	}

}
