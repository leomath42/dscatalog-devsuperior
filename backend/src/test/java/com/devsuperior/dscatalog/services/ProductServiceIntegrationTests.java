package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Transactional
@SpringBootTest
public class ProductServiceIntegrationTests {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductRepository productRepository;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;
	
	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}
	
	
	@Test
	void deleteShouldDeleteResourceWhenIdExist() throws Exception {
		productService.delete(existingId);
		
		Assertions.assertEquals(countTotalProducts - 1, productRepository.count());
	}
	
	@Test
	void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {
		Assertions.assertThrows(ResourceNotFoundException.class, () ->{
			productService.delete(nonExistingId);
		});
	}
	
	@Test
	void findAllPagedShouldReturnPageWhenExistPage() {
		PageRequest pageRequest = PageRequest.of(0, 10);
		
		Page page = productService.findAllPaged(pageRequest);
		
		Assertions.assertFalse(page.isEmpty());
		Assertions.assertEquals(page.getNumber(), 0);
		Assertions.assertEquals(page.getSize(), 10);
		Assertions.assertEquals(countTotalProducts, page.getTotalElements());
	}
	
	@Test
	void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {
		PageRequest pageRequest = PageRequest.of(50, 10);
		
		Page page = productService.findAllPaged(pageRequest);
		
		Assertions.assertTrue(page.isEmpty());
	}
	
	
	@Test
	void findAllPagedShouldReturnSortedPageWhenSortByName() {
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
		
		Page<ProductDTO> result = productService.findAllPaged(pageRequest);
		
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
	}
}
