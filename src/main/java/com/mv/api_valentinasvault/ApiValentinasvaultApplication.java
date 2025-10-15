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
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USERNAME");
        String dbPass = System.getenv("DB_PASSWORD");
        String jwtSecret = System.getenv("JWT_SECRET");

        System.out.println("===== ENV VARIABLES CHECK =====");
        System.out.println("DB_URL: " + dbUrl);
        System.out.println("DB_USERNAME: " + dbUser);
        System.out.println("DB_PASSWORD: " + (dbPass != null ? "*****" : null));
        System.out.println("JWT_SECRET: " + (jwtSecret != null ? "*****" : null));
        System.out.println("================================");
	}

}
