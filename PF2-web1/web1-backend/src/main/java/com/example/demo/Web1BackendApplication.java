package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.example.demo.service.AddressService;

@SpringBootApplication
@EnableConfigurationProperties(AddressService.class)
public class Web1BackendApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(Web1BackendApplication.class, args);
	}

}

