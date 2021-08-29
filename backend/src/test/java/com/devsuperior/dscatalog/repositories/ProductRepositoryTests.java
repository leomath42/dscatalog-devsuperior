package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository productRepository;
	
	private long existingId;
	private long nonExistingId;

	private long countTotalProducts;
	
	@BeforeEach
	public void setUp() throws Exception {
		this.existingId = 1L;
		this.nonExistingId = 1000L;
		this.countTotalProducts = 25L;		
	}
	
	@Test
	public void findByIdShouldReturnANonEmptyOptionalWhenIdExist() {
		Optional<Product> optional = productRepository.findById(existingId);
		Assertions.assertTrue(optional.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnAEmptyOptionalWhenIdNotExist() {
		Optional<Product> optional = productRepository.findById(nonExistingId);
		Assertions.assertFalse(optional.isPresent());
	}
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		Product product = Factory.createProduct();
		product.setId(null);
		
		product = productRepository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
	}
	
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		
		productRepository.deleteById(existingId);
		Optional<Product> entity = productRepository.findById(existingId);
		
		Assertions.assertFalse(entity.isPresent());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, ()->{
			productRepository.deleteById(nonExistingId);
		});
	}
}
