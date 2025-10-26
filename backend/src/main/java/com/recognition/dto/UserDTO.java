package com.recognition.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO representing user information exposed to clients.
 */
public class UserDTO {
  private UUID id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private Boolean isActive;
  private Boolean isVerified;
  private String timezone;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
  private String provider;
  private String role;
  private String avatarUrl;

  // getters / setters
  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }
  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public Boolean getIsVerified() { return isVerified; }
  public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
  public String getTimezone() { return timezone; }
  public void setTimezone(String timezone) { this.timezone = timezone; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
  public String getProvider() { return provider; }
  public void setProvider(String provider) { this.provider = provider; }
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
  public String getAvatarUrl() { return avatarUrl; }
  public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
