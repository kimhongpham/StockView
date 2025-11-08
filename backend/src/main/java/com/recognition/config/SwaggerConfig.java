package com.recognition.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
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

        // http://localhost:8080/swagger-ui/index.html
        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                        .components(new io.swagger.v3.oas.models.Components()
                                .addSecuritySchemes("Bearer Authentication",
                                        new SecurityScheme()
                                                .type(Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .in(In.HEADER)
                                                .name("Authorization")));
        }
}
