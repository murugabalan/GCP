package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.entity.PostgresEntity;
import com.example.demo.service.ApigeeService;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;

@RestController
@RequestMapping("/apigee")
public class ApigeePostController {

	@Autowired
	ApigeeService postService;
	
	@Autowired
	private SecretManagerTemplate secretManagerTemplate;
	
	@GetMapping("/get")
	public List<PostgresEntity> get() {
		return postService.get();
	}

	@PostMapping("/create")
	public PostgresEntity create(@RequestBody PostgresEntity entity) {
		return postService.create(entity);
	}

	@DeleteMapping("/deleteById/{id}")
	public void delete(@PathVariable Long id) {
		postService.deleteById(id);
	}

	@PutMapping("/update")
	public PostgresEntity update(@RequestBody PostgresEntity entity) {
		return postService.update(entity);
	}

	@DeleteMapping("/delete")
	public void delete(@RequestBody PostgresEntity entity) {
		postService.delete(entity);
	}
}
