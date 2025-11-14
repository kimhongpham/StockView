#!/usr/bin/env bash
# start.sh - Start StockView stack with Docker Compose

set -e

echo "Starting StockView services..."
echo

###############################################
# 1. CHECK DOCKER ENGINE STATUS
###############################################
if ! docker info >/dev/null 2>&1; then
  echo "======================================================================================="
  echo "> ERROR: Docker Desktop / Docker Engine is NOT running."
  echo "> Please start Docker before running ./start.sh"
  echo "======================================================================================="
  exit 1
fi

###############################################
# 2. CHECK .env FILE
###############################################
if [ ! -f ".env" ]; then
  echo "======================================================================================="
  echo "> ERROR: .env file is missing."
  echo "> Creating .env from .env.example..."
  cp .env.example .env
  echo "> Please edit .env with correct settings (DB_HOST, API Keys, etc.)"
  echo "> After editing, run: ./start.sh"
  echo "======================================================================================="
  exit 0
fi

###############################################
# 3. START DOCKER COMPOSE SERVICES
###############################################
echo "======================================================================================="
echo "> Starting StockView (app, db, redis)..."
echo "======================================================================================="

if ! docker compose up -d --build; then
  echo
  echo "======================================================================================="
  echo "> ERROR: Failed to start Docker Compose."
  echo "> Try running it manually:"
  echo "> docker compose up -d --build"
  echo "======================================================================================="
  exit 1
fi

###############################################
# 4. SUCCESS MESSAGE
###############################################
echo
echo "======================================================================================="
echo "> SERVICES ARE RUNNING!"
echo "> Backend API: http://localhost:8080"
echo "> View logs with: docker compose logs -f app"
echo "======================================================================================="
