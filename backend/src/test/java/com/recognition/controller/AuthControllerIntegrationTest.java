package com.recognition.controller;

import com.recognition.dto.request.LoginRequest;
import com.recognition.dto.request.RegisterRequest;
import com.recognition.dto.response.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testRegisterAndLoginFlow() {
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("inttester");
        reg.setEmail("inttester@example.com");
        reg.setPassword("123456");

        ResponseEntity<AuthResponse> regResp =
                restTemplate.postForEntity("/api/auth/register", reg, AuthResponse.class);

        assertTrue(regResp.getBody().isSuccess());
        assertNotNull(regResp.getBody().getToken());

        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsernameOrEmail("inttester");
        loginReq.setPassword("123456");

        ResponseEntity<AuthResponse> loginResp =
                restTemplate.postForEntity("/api/auth/login", loginReq, AuthResponse.class);

        assertTrue(loginResp.getBody().isSuccess());
        assertNotNull(loginResp.getBody().getToken());
    }
}
