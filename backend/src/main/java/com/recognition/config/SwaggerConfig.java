package com.recognition.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "StockView API Documentation",
                version = "1.0",
                description = "API backend cho hệ thống phân tích thị trường chứng khoán"
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Server")
        }
)
public class SwaggerConfig {
    // http://localhost:8080/swagger-ui.html
}
