package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		
		return categoryRepository.findAll().stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
	}

	public CategoryDTO findById(Long id) {
		Optional<Category> obj = categoryRepository.findById(id);
		
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		
		return new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		// TODO Auto-generated method stub
		
		Category entity = new Category();
		entity.setName(dto.getName());
		
		entity = categoryRepository.save(entity);
		
		return new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		
		try {
			Category entity = categoryRepository.getOne(id);
			
			entity.setName(dto.getName());
			
			return new CategoryDTO(entity);
			
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
		
	}
}