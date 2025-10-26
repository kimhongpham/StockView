package com.recognition.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "prices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "idx_prices_asset_timestamp_source",
                        columnNames = {"asset_id", "timestamp", "source"}
                )
        },
        indexes = {
                @Index(name = "idx_prices_asset_id", columnList = "asset_id"),
                @Index(name = "idx_prices_timestamp", columnList = "timestamp"),
                @Index(name = "idx_prices_asset_timestamp", columnList = "asset_id, timestamp"),
                @Index(name = "idx_prices_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Price {

  @Id
  @GeneratedValue
  @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
          name = "asset_id",
          nullable = false,
          foreignKey = @ForeignKey(name = "fk_price_asset")
  )
  private Asset asset;

  @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime timestamp;

  @Column(nullable = false, precision = 18, scale = 8)
  private BigDecimal price;

  @Column
  private Long volume;

  @Column(name = "change_percent", precision = 10, scale = 4)
  private BigDecimal changePercent;

  @Column(name = "high_24h", precision = 18, scale = 8)
  private BigDecimal high24h;

  @Column(name = "low_24h", precision = 18, scale = 8)
  private BigDecimal low24h;

  @Column(name = "market_cap", precision = 20, scale = 2)
  private BigDecimal marketCap;

  @Column(nullable = false, length = 100)
  private String source;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime createdAt;
}
