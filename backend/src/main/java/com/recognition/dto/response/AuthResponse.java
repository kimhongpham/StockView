package com.recognition.dto.response;

/**
 * Response containing JWT token and optionally user info.
 */
public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    private Object data;

    public AuthResponse() {}

    public AuthResponse(boolean success, String message, String token, Object data) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.data = data;
    }

    // getters / setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
