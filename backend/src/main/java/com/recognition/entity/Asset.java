package com.recognition.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "assets",
        indexes = {
                @Index(name = "idx_assets_type", columnList = "type"),
                @Index(name = "idx_assets_symbol", columnList = "symbol"),
                @Index(name = "idx_assets_is_active", columnList = "is_active")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {

  @Id
  @GeneratedValue
  @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
  private UUID id;

  @Column(name = "name", nullable = false, unique = true, length = 100)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 50)
  private AssetType type;

  @Column(name = "symbol", nullable = false, unique = true, length = 20)
  private String symbol;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime updatedAt;

  // Enum cho type để map với constraint trong SQL
  public enum AssetType {
    CRYPTO,
    STOCK,
    METAL,
    FOREX,
    COMMODITY
  }
}
