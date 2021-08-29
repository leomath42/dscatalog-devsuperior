package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductResourceIntegrationTests {
	
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;
	private ProductDTO productDTO;
	
	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
		productDTO = Factory.createProductDTO();
	}
	
	@Test
	void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
		ResultActions result =	mockMvc.perform(get("/products?page=0&size=10&sort=name,asc"));
		
		result.andExpect(
			ResultMatcher.matchAll(
				status().isOk(), 
				jsonPath("$.totalElements").value(countTotalProducts),
				jsonPath("$.content").exists(),
				jsonPath("$.content[0].name").value("Macbook Pro"),
				jsonPath("$.content[1].name").value("PC Gamer"),
				jsonPath("$.content[2].name").value("PC Gamer Alfa")
			));
		
	}
	
	@Test
	void updateShouldReturnProductDTOWhenExistingId() throws Exception {
		String body = objectMapper.writeValueAsString(productDTO);
		
		String expectedName = productDTO.getName();
		String expectedDescription = productDTO.getDescription();

		
		ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
				.content(body)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andExpect(
			ResultMatcher.matchAll(
				status().isOk(), 
				jsonPath("$.id").value(existingId),
				jsonPath("$.name").value(expectedName),
				jsonPath("$.description").value(expectedDescription)
			));
		
	}
	
	@Test
	void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		String body = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId)
				.content(body)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andExpect(
			ResultMatcher.matchAll(
				status().isNotFound()
			));
		
	}
	
}
