#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}Building Farmer Equipment Monitoring Application${NC}"
echo -e "${GREEN}================================================${NC}"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}Error: Docker is not running. Please start Docker and try again.${NC}"
    exit 1
fi

echo -e "\n${YELLOW}Step 1: Building Backend Services...${NC}"

# Build Eureka Server
echo -e "${YELLOW}Building Eureka Server...${NC}"
docker build -t farm-monitoring/eureka-server:latest ./backend/eureka-server

# Build Config Server
echo -e "${YELLOW}Building Config Server...${NC}"
docker build -t farm-monitoring/config-server:latest ./backend/config-server

# Build API Gateway
echo -e "${YELLOW}Building API Gateway...${NC}"
docker build -t farm-monitoring/api-gateway:latest ./backend/api-gateway

# Build Farmers Service
echo -e "${YELLOW}Building Farmers Service...${NC}"
docker build -t farm-monitoring/farmers-service:latest ./backend/farmers-service

# Build Equipment Service
echo -e "${YELLOW}Building Equipment Service...${NC}"
docker build -t farm-monitoring/equipment-service:latest ./backend/equipment-service

# Build Supervision Service
echo -e "${YELLOW}Building Supervision Service...${NC}"
docker build -t farm-monitoring/supervision-service:latest ./backend/supervision-service

echo -e "\n${YELLOW}Step 2: Building Frontend...${NC}"

# Build Frontend
echo -e "${YELLOW}Building Next.js Frontend...${NC}"
docker build -t farm-monitoring/frontend:latest ./frontend

echo -e "\n${GREEN}================================================${NC}"
echo -e "${GREEN}Build Complete!${NC}"
echo -e "${GREEN}================================================${NC}"

echo -e "\n${YELLOW}Built Docker Images:${NC}"
docker images | grep "farm-monitoring"

echo -e "\n${GREEN}To start the application, run:${NC}"
echo -e "${YELLOW}./run.sh${NC}"
echo -e "\n${GREEN}Or manually with:${NC}"
echo -e "${YELLOW}docker-compose up -d${NC}"
