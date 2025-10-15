package com.mv.api_valentinasvault;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableScheduling
public class ApiValentinasvaultApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiValentinasvaultApplication.class, args);
	}

}
