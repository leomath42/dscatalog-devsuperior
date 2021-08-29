package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService productService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private ProductDTO productDTO;
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;

	private PageImpl<ProductDTO> page;
	
	@BeforeEach
	void setup() {
		
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		
		Mockito.when(productService.findAllPaged(Mockito.any())).thenReturn(page);
		
		Mockito.when(productService.findById(existingId)).thenReturn(productDTO);
		Mockito.when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		
		Mockito.when(productService.update(Mockito.eq(existingId), Mockito.any())).thenReturn(productDTO);
		Mockito.when(productService.update(Mockito.eq(nonExistingId), Mockito.any())).thenThrow(ResourceNotFoundException.class);
		
		Mockito.doNothing().when(productService).delete(existingId);
		Mockito.doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);
		Mockito.doThrow(DataBaseException.class).when(productService).delete(dependentId);
		
		
		Mockito.when(productService.insert(Mockito.any())).thenReturn(productDTO);
		
	}
	
	@Test
	public void findAllPagedShouldReturnPage() throws Exception {
		mockMvc.perform(get("/products")).andExpect(status().isOk());
		Mockito.verify(productService).findAllPaged(Mockito.any());
	}
	
	
	@Test
	public void findByIdShouldReturnProductDTOWhenExistingId() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenNonExistingId() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenExistigId() throws Exception {
		String body = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
				.content(body)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON));
		
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenNonExistigId() throws Exception {
		String body = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId)
				.content(body)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON));
		
		
		result.andExpect(status().isNotFound());
	}
	
	
	@Test
	public void deleteShouldReturnNoContentWhenExistigId() throws Exception {
		
		ResultActions result = mockMvc.perform(delete("/products/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON));
		
		
		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenNonExistigId() throws Exception {
		
		ResultActions result = mockMvc.perform(delete("/products/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON));
		
		
		result.andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deleteShouldReturnBadRequestWhenNonExistigId() throws Exception {
		
		ResultActions result = mockMvc.perform(delete("/products/{id}", dependentId)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON));
		
		
		result.andExpect(status().isBadRequest());
		
	}
	
	
	@Test
	public void insertShouldReturnProductDTO() throws Exception {
		String body = objectMapper.writeValueAsString(productDTO);
		
		ResultActions resultActions = mockMvc.perform(post("/products")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(body));
		
		resultActions.andExpect(status().isCreated());
		resultActions.andExpect(jsonPath("$.id").exists());
	}
}
