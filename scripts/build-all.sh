#!/bin/bash

# Build script for all Docker images

set -e

echo "Building Docker images for Farmer Equipment Monitoring Application..."

# Infrastructure services
echo "Building Eureka Server..."
docker build -t farm/eureka-server:latest ./infrastructure/eureka-server

echo "Building Config Server..."
docker build -t farm/config-server:latest ./infrastructure/config-server

echo "Building API Gateway..."
docker build -t farm/api-gateway:latest ./infrastructure/api-gateway

# Backend microservices
echo "Building Farmers Service..."
docker build -t farm/farmers-service:latest ./backend/farmers-service

echo "Building Equipment Service..."
docker build -t farm/equipment-service:latest ./backend/equipment-service

echo "Building Supervision Service..."
docker build -t farm/supervision-service:latest ./backend/supervision-service

# Frontend
echo "Building Frontend..."
docker build -t farm/frontend:latest ./frontend

echo "All images built successfully!"
echo ""
echo "To run the full stack:"
echo "  cd docker && docker-compose up"
