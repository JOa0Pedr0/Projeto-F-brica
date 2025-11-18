package com.fabrica.fabrica_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"entities"})
@ComponentScan(basePackages = { "service", "dao", "entities", "util", "com.fabrica.fabrica_api", "controller" })
@EnableJpaRepositories(basePackages = {"dao"})
@SpringBootApplication
public class FabricaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FabricaApiApplication.class, args);
	}

}
