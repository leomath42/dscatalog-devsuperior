package com.devsuperior.dscatalog.resource;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsuperior.dscatalog.entities.Category;

/*
 * Representa um Controlador Rest. 
 * Possui o intuito de disponibilizar os "recursos" de uma entidade
 * e além disso se encontra na "camada" -> (Controladores REST) do projeto.
 * */

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {
	
	@GetMapping
	public ResponseEntity<List<Category>> findAll(){
		List<Category> list = new ArrayList<>();
				
		list.add(new Category(1L, "Books"));
		list.add(new Category(2L, "Eletronics"));
		
		return ResponseEntity.ok().body(list);
	}
}
