# StockView

> Financial market analysis & tracking system including stocks – Backend Spring Boot + Frontend React/TypeScript

## 📌 Description

StockView is a full-stack project that allows users to:

* Register/Login (support OAuth2 with Google)
* Manage assets such as stocks, crypto, metals
* Get real-time price data from 3rd parties (e.g. Finnhub)
* Store price history, calculate statistics, charts
* Manage watchlist
<!-- * Alert when price exceeds threshold -->
* Authorization (user/admin)
* Responsive frontend with modern UI, theme toggle

## 🧱 Architecture

* **Backend**: Java + Spring Boot (3.x)

* Spring Web, Spring Data JPA, PostgreSQL, Redis (cache, session)
* Spring Security + JWT + OAuth2 (Google)
* RESTful API + simplified WebSocket (endpoint `WS_URL`)
* External API integration: Finnhub (stocks)
* Logging, exception handling, Swagger UI (OpenAPI)
* **Frontend**: React + TypeScript + Vite

* Clear component architecture: layout, sidebar, modals, pages, store (zustand/mobx)
* Table, chart, theme toggle, dark/light mode
* UI for user + admin (asset management)
* **Database**: PostgreSQL (runs locally)
<!-- * **Cache / Message**: Redis (data cache, rate-limit)
* **DevOps/CI-CD**: Scalable (Docker, GitHub Actions…) -->

## 🚀 Install & Run

### 1. Create environment variables

Copy the sample file `.env.example` and fill in the appropriate values fit:

```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=stockview
DB_USERNAME=postgres
DB_PASSWORD=<your_password>
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=<your_password>
JWT_SECRET=<your_super_secret_key>
FINNHUB_API_KEY=<your_key>
...
```

### 2. Backend

```bash
cd backend
./mvnw clean package
java -jar target/backend-1.0-SNAPSHOT.jar
```

Or run in the IntelliJ IDE with `Main.java` configuration.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Open browser at `http://localhost:5173` (or other configuration)

### 4. Link backend + frontend

* In `.env` frontend: `VITE_API_URL=http://localhost:8080/api`
* CORS has been enabled in backend (`CorsConfig`) to allow origin from frontend.

* WebSocket endpoint: `WS_URL=ws://localhost:8080/ws` if you use realtime.

## 🧩 Main features

* Register/login & JWT authentication
* OAuth2 with Google
* User management (profile, role)
* Asset management (Assets): add, delete, check existence
* Get and save realtime price + price history
* Stock/crypto/metal charts
* Watchlist
<!-- * Alert when price exceeds threshold, log warnings -->
* Statistics: min, max, average price in time period
* Admin API: add/edit/delete asset, track new market
* API documentation via Swagger UI (`http://localhost:8080/swagger-ui.html`)

## 🧪 Testing

* Backend has test classes: controller integration test, service test (e.g. `AuthControllerIntegrationTest.java`, `UserServiceTest.java`)

## 🔧 Extend & Enhance

* Deploy with Docker Compose: backend, frontend, PostgreSQL, Redis, Nginx
* Deploy to cloud (AWS, Azure, Heroku) + CI/CD
* Add WebSocket Module to push realtime data to client
* Add advanced reporting module: technical analysis, machine learning
* Integrate with Telegram notification system, email available (with environment variable in `.env`)
* Add more powerful admin/role rights
* Add caching with Redis, queue processing (Kafka/RabbitMQ) if volume is large

## 📁 Directory structure

```
/backend  
  ├─ src/main/java/com/recognition/…  
  ├─ src/main/resources/application.properties  
  ├─ .env  
  ├─ pom.xml  
/frontend  
  ├─ src/  
     ├─ components/  
     ├─ pages/  
     ├─ store/  
     ├─ utils/  
  ├─ package.json  
  ├─ vite.config.ts  
.env.example
```

## 📩 Contact

If you have any problems or want to contribute, you can send me an email or open an issue on GitHub.