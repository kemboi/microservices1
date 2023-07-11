package com.kemboishoppingcartapp.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kemboishoppingcartapp.productservice.dto.ProductRequest;
import com.kemboishoppingcartapp.productservice.dto.ProductResponse;
import com.kemboishoppingcartapp.productservice.entity.Product;
import com.kemboishoppingcartapp.productservice.repository.ProductRepository;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {
	@Container
	static
	MySQLContainer mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"));
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.datasource.url",mySQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username",mySQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password",mySQLContainer::getPassword);
	}
	@Autowired
	private ProductRepository productRepository;
	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString)
		).andExpect(status().isCreated());
		assertEquals(1, productRepository.findAll().size());
	}
	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("iPhone 13")
				.description("iPhone 13 128GB")
				.price(BigDecimal.valueOf(1200))
				.build();
	}
	@Test
	void shouldGetAllProducts() throws Exception {
		List<ProductResponse> productResponseList = getAllProducts();
		String productResponseString = objectMapper.writeValueAsString(productResponseList);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
				.content(productResponseString)).andExpect(status().isOk());
		assertEquals(
				1,productResponseList.size()
		);
	}

	private List<ProductResponse> getAllProducts() {
		List<Product> productResponseList = productRepository.findAll();
		return productResponseList.stream().map(product -> mapToProductResponse(product)).toList();
	}

	private ProductResponse mapToProductResponse(Product product) {
		return ProductResponse.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.build();

	}

}
