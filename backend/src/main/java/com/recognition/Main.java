package com.recognition;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        // 1️⃣ Nạp file .env TRƯỚC khi Spring Boot đọc cấu hình
        Dotenv dotenv = Dotenv.configure()
                .directory("D:/Github/STOCKVIEW/backend")
                .ignoreIfMissing()
                .load();

        Map<String, Object> props = new HashMap<>();
        dotenv.entries().forEach(entry -> props.put(entry.getKey(), entry.getValue()));

        // 2️⃣ Tạo SpringApplication và gán ENV vào
        SpringApplication app = new SpringApplication(Main.class);
        app.setDefaultProperties(props);

        System.out.println("✅ Loaded environment variables successfully.");
        System.out.println("✅ Starting Spring Boot application...");

        // 3️⃣ Chạy app
        app.run(args);
    }
}
