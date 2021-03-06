package com.devsuperior.dscatalog.services;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.RoleDTO;
import com.devsuperior.dscatalog.dto.UserDTO;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.entities.Role;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService {
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired 
	RoleRepository roleRepository;
	
	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		return userRepository.findAll(pageable).map(user -> new UserDTO(user));
		
	}
	
	@Transactional(readOnly = true)
	public UserDTO findById(Long id) throws NoSuchElementException {
		User entity = userRepository.findById(id).orElseThrow();
		return new UserDTO(entity);
	}
	
	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		
		copyDtoToEntity(dto, entity);
		entity.setPassword(passwordEncoder.encode(dto.getPassword()));
		entity = userRepository.save(entity);
		return new UserDTO(entity);
	}
	
	@Transactional
	public UserDTO update(Long id, UserInsertDTO dto) {
		try {			
			User entity = userRepository.getOne(id);
			
			copyDtoToEntity(dto, entity);
			entity = userRepository.save(entity);
			
			return new UserDTO(entity);
		}
		catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}	
	
	@Transactional
	public void delete(Long id) {
		try {
			userRepository.deleteById(id);
		}
		catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation");
		}
	}
	
	private void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setEmail(dto.getEmail());
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		
		entity.getRoles().clear();
		
		for(Iterator<RoleDTO> iter = dto.getRoles().iterator(); iter.hasNext();) {
			Long id = iter.next().getId();
			Role role = roleRepository.getOne(id);
			entity.getRoles().add(role);
		}		
	}
}
