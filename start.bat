@echo off
title StockView - Startup

REM ============================
REM 1. KIỂM TRA DOCKER ĐÃ CHẠY CHƯA
REM ============================
echo Checking Docker Desktop status...
docker info >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    echo/
    echo =======================================================================================
    echo ^> ERROR: Docker Desktop chua chay hoac Docker Engine khong the ket noi.
    echo ^> Vui long bat Docker Desktop truoc khi chay start.bat.
    echo =======================================================================================
    echo/
    pause
    exit /b
)

REM ============================
REM 2. KIỂM TRA FILE .env
REM ============================
IF NOT EXIST .env (
    copy .env.example .env >nul
    echo/
    echo =======================================================================================
    echo ^> ERROR: File .env chua duoc cau hinh.
    echo ^> Da tao file .env tu .env.example.
    echo ^> Vui long mo va chinh sua file .env voi cac thong so DB, API Keys...
    echo =======================================================================================
    echo/
    pause
    exit /b
)

REM ============================
REM 3. KHỞI ĐỘNG DOCKER COMPOSE
REM ============================
echo/
echo =======================================================================================
echo ^> Khoi dong StockView services (app, db, redis)...
echo =======================================================================================

docker compose up -d --build
IF %ERRORLEVEL% NEQ 0 (
    echo/
    echo =======================================================================================
    echo ^> ERROR: Khong the khoi dong cac service qua Docker Compose.
    echo ^> Vui long kiem tra lai docker-compose.yml hoac thu chay thu cong: 
    echo ^> docker compose up -d --build
    echo =======================================================================================
    echo/
    pause
    exit /b
)

REM ============================
REM 4. DONE
REM ============================
echo/
echo =======================================================================================
echo ^> CAC SERVICE DANG CHAY!
echo ^> Backend API: http://localhost:8080
echo ^> Xem logs: docker compose logs -f app
echo =======================================================================================
echo/
pause
