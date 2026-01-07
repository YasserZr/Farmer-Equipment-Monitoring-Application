@echo off
REM Windows batch script to build Docker images

echo ================================================
echo Building Farmer Equipment Monitoring Application
echo ================================================

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo Error: Docker is not running. Please start Docker and try again.
    exit /b 1
)

echo.
echo Step 1: Building Backend Services...

echo Building Eureka Server...
docker build -t farm-monitoring/eureka-server:latest ./backend/eureka-server

echo Building Config Server...
docker build -t farm-monitoring/config-server:latest ./backend/config-server

echo Building API Gateway...
docker build -t farm-monitoring/api-gateway:latest ./backend/api-gateway

echo Building Farmers Service...
docker build -t farm-monitoring/farmers-service:latest ./backend/farmers-service

echo Building Equipment Service...
docker build -t farm-monitoring/equipment-service:latest ./backend/equipment-service

echo Building Supervision Service...
docker build -t farm-monitoring/supervision-service:latest ./backend/supervision-service

echo.
echo Step 2: Building Frontend...

echo Building Next.js Frontend...
docker build -t farm-monitoring/frontend:latest ./frontend

echo.
echo ================================================
echo Build Complete!
echo ================================================

echo.
echo Built Docker Images:
docker images | findstr "farm-monitoring"

echo.
echo To start the application, run:
echo run.bat
echo.
echo Or manually with:
echo docker-compose up -d

pause
