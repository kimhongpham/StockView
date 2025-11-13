package com.recognition.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "assets",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_asset_symbol", columnNames = {"symbol"})
        },
        indexes = {
                @Index(name = "idx_asset_symbol", columnList = "symbol"),
                @Index(name = "idx_asset_is_active", columnList = "is_active")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asset {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, length = 20)
  private String symbol; // e.g. AAPL, TSLA, BTC

  @Column(nullable = false, length = 100)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @Column(name = "market_cap", precision = 20, scale = 2)
  private BigDecimal marketCap;

  @Column(name = "volume", precision = 20, scale = 2)
  private BigDecimal volume;

  @Column(name = "shares_outstanding", precision = 20, scale = 2)
  private BigDecimal sharesOutstanding;

  @Column(name = "pe_ratio", precision = 10, scale = 4)
  private BigDecimal peRatio;

  @Column(name = "pb_ratio", precision = 10, scale = 4)
  private BigDecimal pbRatio;

  @Column(name = "eps", precision = 10, scale = 4)
  private BigDecimal eps;

  @Column(name = "book_value", precision = 10, scale = 4)
  private BigDecimal bookValue;

  @Column(name = "ev_to_ebitda", precision = 10, scale = 4)
  private BigDecimal evToEbitda;

  @CreationTimestamp
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  private OffsetDateTime updatedAt;
}
