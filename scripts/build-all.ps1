# Build script for all Docker images (PowerShell)

Write-Host "Building Docker images for Farmer Equipment Monitoring Application..." -ForegroundColor Green

# Infrastructure services
Write-Host "Building Eureka Server..." -ForegroundColor Cyan
docker build -t farm/eureka-server:latest ./infrastructure/eureka-server

Write-Host "Building Config Server..." -ForegroundColor Cyan
docker build -t farm/config-server:latest ./infrastructure/config-server

Write-Host "Building API Gateway..." -ForegroundColor Cyan
docker build -t farm/api-gateway:latest ./infrastructure/api-gateway

# Backend microservices
Write-Host "Building Farmers Service..." -ForegroundColor Cyan
docker build -t farm/farmers-service:latest ./backend/farmers-service

Write-Host "Building Equipment Service..." -ForegroundColor Cyan
docker build -t farm/equipment-service:latest ./backend/equipment-service

Write-Host "Building Supervision Service..." -ForegroundColor Cyan
docker build -t farm/supervision-service:latest ./backend/supervision-service

# Frontend
Write-Host "Building Frontend..." -ForegroundColor Cyan
docker build -t farm/frontend:latest ./frontend

Write-Host "`nAll images built successfully!" -ForegroundColor Green
Write-Host "`nTo run the full stack:" -ForegroundColor Yellow
Write-Host "  cd docker; docker-compose up"
