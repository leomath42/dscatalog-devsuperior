package com.devsuperior.dscatalog.resources;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.services.CategoryService;

/*
 * Representa um Controlador Rest. 
 * Possui o intuito de disponibilizar os "recursos" de uma entidade
 * e além disso se encontra na "camada" -> (Controladores REST) do projeto.
 * */

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {
	
	@Autowired
	CategoryService categoryService;
	
	@GetMapping
	public ResponseEntity<List<Category>> findAll(){
		List<Category> list = categoryService.findAll();
		
		return ResponseEntity.ok().body(list);
	}
}
