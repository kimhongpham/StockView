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
                        name = "uq_price_asset_timestamp_source",
                        columnNames = {"asset_id", "timestamp", "source"}
                )
        },
        indexes = {
                @Index(name = "idx_price_asset_timestamp", columnList = "asset_id, timestamp DESC")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Price {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "asset_id", foreignKey = @ForeignKey(name = "fk_price_asset"))
  private Asset asset;

  @Column(nullable = false, precision = 18, scale = 8)
  private BigDecimal price;

  @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime timestamp;

  @Column(name = "source", nullable = false, length = 50)
  private String source; // e.g. "Finnhub", "Manual", "Yahoo"

  // 🔹 Trường mở rộng
  @Column(name = "change_percent", precision = 10, scale = 4)
  private BigDecimal changePercent;

  @Column(name = "high_24h", precision = 18, scale = 8)
  private BigDecimal high24h;

  @Column(name = "low_24h", precision = 18, scale = 8)
  private BigDecimal low24h;

  @Column(name = "volume", precision = 20, scale = 2)
  private BigDecimal volume;

  @Column(name = "market_cap", precision = 20, scale = 2)
  private BigDecimal marketCap;

  @CreationTimestamp
  private OffsetDateTime createdAt;
}
