package com.recognition;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        // 1. NẠP FILE .ENV VÀO HỆ THỐNG
        // Bước này cần thiết để Spring Boot có thể đọc các cấu hình
        // như spring.datasource.url, spring.datasource.username, v.v.
        try {
            Dotenv dotenv = Dotenv.load();
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
            System.out.println("INFO: Loaded environment variables successfully.");
        } catch (io.github.cdimascio.dotenv.DotenvException e) {
            System.err.println("WARNING: .env file not found or failed to load. Ensure it's in the working directory.");
        }

        // 2. KHỞI CHẠY ỨNG DỤNG SPRING BOOT
        SpringApplication.run(Main.class, args);

        System.out.println("INFO: Application started successfully. Hibernate is attempting to update the database schema.");
    }
}