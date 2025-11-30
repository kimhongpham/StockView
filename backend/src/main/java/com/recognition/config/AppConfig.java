package com.recognition.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Centralized application configuration for URLs and endpoints.
 * This class consolidates all URL configurations to avoid hardcoding them in
 * multiple places.
 */
@Configuration
@Getter
public class AppConfig {

  @Value("${app.base-url:http://localhost:8080}")
  private String baseUrl;

  @Value("${app.frontend-url:http://localhost:5173}")
  private String frontendUrl;

  @Value("${app.cors-origins:http://localhost:5173,http://localhost:8080}")
  private String corsOrigins;

  /**
   * Get CORS allowed origins as an array
   */
  public String[] getCorsOriginsArray() {
    return corsOrigins.split(",");
  }

  /**
   * Get OAuth2 authorization endpoint
   */
  public String getOAuth2AuthorizationEndpoint() {
    return baseUrl + "/oauth2/authorization";
  }

  /**
   * Get API base URL
   */
  public String getApiBaseUrl() {
    return baseUrl + "/api";
  }
}
