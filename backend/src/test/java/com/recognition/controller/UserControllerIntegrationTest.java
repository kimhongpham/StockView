package com.recognition.controller;

import com.recognition.dto.request.RegisterRequest;
import com.recognition.dto.response.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetMe() {
        // 1. Register
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("userme");
        reg.setEmail("userme@example.com");
        reg.setPassword("123456");

        ResponseEntity<AuthResponse> regResp =
                restTemplate.postForEntity("/api/auth/register", reg, AuthResponse.class);
        String token = regResp.getBody().getToken();

        // 2. Call /users/me with token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> meResp = restTemplate.exchange("/api/users/me",
                HttpMethod.GET, entity, String.class);

        assertTrue(meResp.getBody().contains("userme"));
    }
}
