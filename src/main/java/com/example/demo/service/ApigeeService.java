package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.PostgresEntity;
import com.example.demo.repo.PostRepo;

@Service
public class ApigeeService {

	@Autowired
	PostRepo repo;

	public List<PostgresEntity> get() {
		return repo.findAll();
	}

	public PostgresEntity create(PostgresEntity postcurdentity) {
		return repo.save(postcurdentity);
	}

	public void deleteById(long id) {
		repo.deleteById(id);
	}

	public PostgresEntity update(PostgresEntity postgresEntity) {
		return repo.save(postgresEntity);
	}

	public void delete(PostgresEntity entity) {
		repo.delete(entity);
	}
}
