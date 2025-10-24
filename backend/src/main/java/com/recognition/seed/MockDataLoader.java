package com.recognition.data;

import com.recognition.entity.*;
import com.recognition.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;

@Component
@Profile("dev")
public class MockDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final PriceRepository priceRepository;
    private final AlertRepository alertRepository;
    private final AlertLogRepository alertLogRepository;
    private final PasswordEncoder passwordEncoder;

    public MockDataLoader(
            UserRepository userRepository,
            AssetRepository assetRepository,
            PriceRepository priceRepository,
            AlertRepository alertRepository,
            AlertLogRepository alertLogRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.priceRepository = priceRepository;
        this.alertRepository = alertRepository;
        this.alertLogRepository = alertLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            System.out.println("✅ Mock data already exists. Skipping...");
            return;
        }

        System.out.println("🚀 Inserting mock data for development...");

        // ===== USERS =====
        // --- ADMIN ---
        Users admin = new Users();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        String adminPlain = "Admin@123";                       // mật khẩu plaintext cho dev
        String adminHash = passwordEncoder.encode(adminPlain); // mã hóa bằng PasswordEncoder inject
        admin.setPasswordHash(adminHash);
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setIsActive(true);
        admin.setIsVerified(true);

        // In ra console chỉ cho admin (dev-only)
        System.out.println("=== MOCK ADMIN CREATED (dev only) ===");
        System.out.println("username : " + admin.getUsername());
        System.out.println("password (plain): " + adminPlain);
        System.out.println("password (hash) : " + adminHash);
        System.out.println("=====================================");

        Users alice = new Users();
        alice.setUsername("alice");
        alice.setEmail("alice@example.com");
        alice.setPasswordHash(passwordEncoder.encode("Alice@123"));
        alice.setFirstName("Alice");
        alice.setLastName("Johnson");

        Users bob = new Users();
        bob.setUsername("bob");
        bob.setEmail("bob@example.com");
        bob.setPasswordHash(passwordEncoder.encode("Bob@123"));
        bob.setFirstName("Bob");
        bob.setLastName("Smith");

        userRepository.saveAll(Arrays.asList(admin, alice, bob));

        // ===== ASSETS =====
        Asset btc = new Asset();
        btc.setName("Bitcoin");
        btc.setSymbol("BTC");
        btc.setDescription("Decentralized digital currency");
        btc.setIsActive(true);

        Asset gold = new Asset();
        gold.setName("Gold");
        gold.setSymbol("XAU");
        gold.setDescription("Precious metal commodity");
        gold.setIsActive(true);

        Asset apple = new Asset();
        apple.setName("Apple Inc.");
        apple.setSymbol("AAPL");
        apple.setDescription("NASDAQ-listed technology stock");
        apple.setIsActive(true);

        assetRepository.saveAll(Arrays.asList(gold, apple));

        // ===== PRICES =====
        Price btcPrice = new Price();
        btcPrice.setAsset(btc);
        btcPrice.setTimestamp(OffsetDateTime.now().minusHours(1));
        btcPrice.setPrice(new BigDecimal("62000.50"));
        btcPrice.setVolume(50000L);
        btcPrice.setChangePercent(new BigDecimal("1.2"));
        btcPrice.setHigh24h(new BigDecimal("63000"));
        btcPrice.setLow24h(new BigDecimal("61000"));
        btcPrice.setMarketCap(new BigDecimal("1200000000"));
        btcPrice.setSource("Binance");

        Price goldPrice = new Price();
        goldPrice.setAsset(gold);
        goldPrice.setTimestamp(OffsetDateTime.now().minusHours(1));
        goldPrice.setPrice(new BigDecimal("1950.75"));
        goldPrice.setVolume(10000L);
        goldPrice.setChangePercent(new BigDecimal("0.3"));
        goldPrice.setHigh24h(new BigDecimal("1960"));
        goldPrice.setLow24h(new BigDecimal("1940"));
        goldPrice.setMarketCap(new BigDecimal("500000000"));
        goldPrice.setSource("MarketWatch");

        Price applePrice = new Price();
        applePrice.setAsset(apple);
        applePrice.setTimestamp(OffsetDateTime.now().minusHours(1));
        applePrice.setPrice(new BigDecimal("175.60"));
        applePrice.setVolume(2000000L);
        applePrice.setChangePercent(new BigDecimal("0.5"));
        applePrice.setHigh24h(new BigDecimal("178"));
        applePrice.setLow24h(new BigDecimal("173"));
        applePrice.setMarketCap(new BigDecimal("300000000"));
        applePrice.setSource("NASDAQ");

        priceRepository.saveAll(Arrays.asList(goldPrice, applePrice));

        // ===== ALERTS =====
        Alert alert1 = new Alert();
        alert1.setUserId(admin.getId());
        alert1.setAssetId(apple.getId());
        alert1.setAlertType("PRICE_ABOVE");
        alert1.setConditionType("GREATER_THAN");
        alert1.setThresholdValue(new BigDecimal("180"));
        alert1.setNotificationMethod("EMAIL");
        alert1.setNotificationTarget("admin@example.com");
        alert1.setIsActive(true);

        Alert alert2 = new Alert();
        alert2.setUserId(alice.getId());
        alert2.setAssetId(apple.getId());
        alert2.setAlertType("PRICE_BELOW");
        alert2.setConditionType("LESS_THAN");
        alert2.setThresholdValue(new BigDecimal("170"));
        alert2.setNotificationMethod("TELEGRAM");
        alert2.setNotificationTarget("@alice_alerts");
        alert2.setIsActive(true);

        alertRepository.saveAll(Arrays.asList(alert1, alert2));

        // ===== ALERT LOGS =====
        AlertLog log1 = new AlertLog();
        log1.setAlertId(alert1.getId());
        log1.setTriggeredPrice(new BigDecimal("62000"));
        log1.setNotificationStatus("SENT");
        log1.setMessageContent("Bitcoin price exceeded $60,000!");
        log1.setRetryCount(0);

        AlertLog log2 = new AlertLog();
        log2.setAlertId(alert2.getId());
        log2.setTriggeredPrice(new BigDecimal("169"));
        log2.setNotificationStatus("FAILED");
        log2.setMessageContent("Apple stock dropped below $170!");
        log2.setErrorMessage("Telegram API timeout");
        log2.setRetryCount(1);

        alertLogRepository.saveAll(Arrays.asList(log1, log2));

        System.out.println("✅ Mock data inserted successfully!");
    }
}
