package com.mv.api_valentinasvault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EnvDebugRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(EnvDebugRunner.class);

    @Override
    public void run(String... args) throws Exception {
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASSWORD");
        String jwtSecret = System.getenv("JWT_SECRET");

        logger.info("===== ENV VARIABLES CHECK =====");
        logger.info("DB_URL: {}", dbUrl);
        logger.info("DB_USER: {}", dbUser);
        logger.info("DB_PASSWORD: {}", (dbPass != null ? "*****" : null));
        logger.info("JWT_SECRET: {}", (jwtSecret != null ? "*****" : null));
        logger.info("================================");
    }
}
