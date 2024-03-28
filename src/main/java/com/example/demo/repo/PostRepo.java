package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PostgresEntity;

@Repository
public interface PostRepo extends JpaRepository<PostgresEntity, Long>{

}
