# Quick Start: Market Data Caching

## Setup

### 1. Start Redis Server

#### Option A: Docker (Recommended)
```bash
docker run -d -p 6379:6379 --name redis redis:7-alpine
```

#### Option B: Local Installation
```bash
# macOS
brew install redis
redis-server

# Ubuntu/Debian
sudo apt install redis-server
redis-server

# Windows (via WSL)
wsl
sudo apt install redis-server
redis-server
```

### 2. Configure Environment

Create/update `.env` file:
```env
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_DB=0
CACHE_ENABLED=true
CACHE_PRICE_TTL=30
CACHE_CANDLE_TTL=300
```

### 3. Build and Run

```bash
# Build with Maven
mvn clean install

# Run the application
mvn spring-boot:run
```

The cache is now active! ✓

## Verify Cache is Working

### Option 1: Check Logs
```
✓ Cache HIT: price for AAPL
✓ Cached price for AAPL with TTL: 30s
```

### Option 2: Test via API

```bash
# Get latest price (first call = cache miss)
curl http://localhost:8080/api/prices/{assetId}/latest

# Get latest price again (cache hit within 30s)
curl http://localhost:8080/api/prices/{assetId}/latest
```

### Option 3: Redis CLI
```bash
redis-cli
KEYS market:*
GET market:AAPL:price
```

## Cache Admin Commands

### Check Cache Status
```bash
curl -H "Authorization: Bearer {ADMIN_TOKEN}" \
  http://localhost:8080/api/admin/cache/status
```

### Clear Price Cache
```bash
curl -X DELETE -H "Authorization: Bearer {ADMIN_TOKEN}" \
  http://localhost:8080/api/admin/cache/price/AAPL
```

### Clear All Cache (Emergency)
```bash
curl -X DELETE -H "Authorization: Bearer {ADMIN_TOKEN}" \
  http://localhost:8080/api/admin/cache
```

## Performance Metrics

### Typical Response Times
- **Cache HIT**: 5-10ms (Redis)
- **Cache MISS**: 50-100ms (Database)
- **TTL Expiration**: 30s for prices, 300s for candles

### Expected Hit Ratio
- Normal trading hours: 70-85% cache hit ratio
- Off-hours: 40-60% cache hit ratio

## Configuration Tuning

### For High-Frequency Updates
```properties
cache.price.ttl=10          # Shorter TTL = fresher data
cache.candle.ttl=60
```

### For Stability (High Cache Ratio)
```properties
cache.price.ttl=60          # Longer TTL = fewer DB hits
cache.candle.ttl=600
```

### For Development
```properties
cache.enabled=false         # Disable cache for testing
```

## Troubleshooting

### Redis Connection Error
```
ERROR: Connection refused at localhost:6379
```
**Fix:** Start Redis: `redis-server` (or Docker)

### Cache Not Working
Check logs for:
- `cache.enabled=true` in properties
- Redis connection established
- `✓ Cache HIT` messages in logs

### Memory Issues
Reduce TTL values or clear cache:
```bash
redis-cli DEL market:*
```

## Docker Compose (Full Stack)

Create `docker-compose.override.yml`:
```yaml
version: '3.8'
services:
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 5s
      timeout: 3s
      retries: 5

volumes:
  redis-data:
```

Start all services:
```bash
docker-compose up -d
```

## Next Steps

- Review detailed documentation: `CACHE_IMPLEMENTATION.md`
- Run integration tests: `mvn test`
- Monitor cache with Redis CLI: `redis-cli`
- Set up alerts for cache hit ratio
