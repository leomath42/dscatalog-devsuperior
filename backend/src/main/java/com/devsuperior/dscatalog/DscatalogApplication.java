package com.devsuperior.dscatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class DscatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(DscatalogApplication.class, args);
	}

}
