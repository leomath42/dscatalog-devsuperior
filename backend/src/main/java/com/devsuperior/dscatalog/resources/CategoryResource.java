package com.devsuperior.dscatalog.resources;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsuperior.dscatalog.dto.CategoryDTO;
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
	public ResponseEntity<List<CategoryDTO>> findAll(){
		List<CategoryDTO> list = categoryService.findAll();
		
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> findById(@PathVariable Long id){
		CategoryDTO dto = categoryService.findById(id);
		
		return ResponseEntity.ok().body(dto);
	}
	
	@PostMapping
	public ResponseEntity<CategoryDTO> save(@RequestBody CategoryDTO dto){
		
//		ServletServerHttpRequest http2 = new ServletServerHttpRequest(request);
//		System.out.println(http2.getURI());
		
		dto = categoryService.save(dto);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
		
		return ResponseEntity.created(uri).body(dto);
	}
}
