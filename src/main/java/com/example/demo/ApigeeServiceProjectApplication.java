package com.example.demo;

import io.unlogged.Unlogged;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApigeeServiceProjectApplication {
	@Unlogged
	public static void main(String[] args) {
		SpringApplication.run(ApigeeServiceProjectApplication.class, args);
	}

}
