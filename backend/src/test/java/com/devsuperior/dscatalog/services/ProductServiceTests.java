package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private Long existingId;
	private	Long nonExistingId;
	private	Long dependentId;
	private	Product product;
	private Category category;
	private PageImpl<Product> page;
	
	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		
		product = Factory.createProduct();
		category = Factory.createCategory();
		page = new PageImpl<>(List.of(product));
		
		
		Mockito.doThrow(EntityNotFoundException.class).when(repository).getOne(nonExistingId);
		Mockito.doThrow(IllegalArgumentException.class).when(repository).getOne(ArgumentMatchers.isNull());
		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		
		
		Mockito.doThrow(EntityNotFoundException.class).when(categoryRepository).getOne(nonExistingId);
		Mockito.doThrow(IllegalArgumentException.class).when(categoryRepository).getOne(ArgumentMatchers.isNull());
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		
		Mockito.doThrow(IllegalArgumentException.class).when(repository).findById(ArgumentMatchers.isNull());
		Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(Mockito.anyLong());
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		Mockito.doNothing().when(repository).deleteById(existingId);
	}
	
	@Test
	void updateShouldReturnProductDTOWhenExistingId() {
		ProductDTO productDTO = Factory.createProductDTO();
		productDTO.setName("name");
		ProductDTO result = service.update(existingId, productDTO);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(productDTO.getName(), result.getName());
		
		Mockito.verify(repository).getOne(existingId);
		Mockito.verify(categoryRepository).getOne(existingId);
		Mockito.verify(repository).save(ArgumentMatchers.any());
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () ->{
			ProductDTO productDTO = Factory.createProductDTO();
			productDTO.setName("name");
			ProductDTO updatedProductDTO = service.update(nonExistingId, productDTO);
		});
		
		Mockito.verify(repository).getOne(nonExistingId);
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenExistingId() {
		ProductDTO productDTO = service.findById(existingId);
		
		Assertions.assertNotNull(productDTO);
		Assertions.assertEquals(productDTO.getId(), existingId);
		
		Mockito.verify(repository).findById(existingId);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptioWhenNonExistingId() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			ProductDTO productDTO = service.findById(nonExistingId);			
		});
		Mockito.verify(repository).findById(nonExistingId);
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> page = service.findAllPaged(pageable);
		
		Assertions.assertNotNull(page);
		Mockito.verify(repository).findAll(pageable);
	}
	@Test
	public void insertShouldSaveProductAndReturnProductDTOWhenPassingProductDTO() {
		ProductDTO dto = new ProductDTO(product);
		
		dto = service.insert(dto);
		Assertions.assertNotNull(dto);
		Assertions.assertNotNull(dto.getId());
		Mockito.verify(repository).save(ArgumentMatchers.any());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenDeletingNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		
		Mockito.verify(repository).deleteById(nonExistingId);
	}	
	
	@Test
	public void deleteShouldThrowDataIntegrityViolationExceptionWhenDeletingDependentId() {
		Assertions.assertThrows(DataBaseException.class, () -> {
			service.delete(dependentId);
		});
		
		Mockito.verify(repository).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldDeleteProductWhenDeletingExistingId() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		
		Mockito.verify(repository).deleteById(existingId);
	}
}
