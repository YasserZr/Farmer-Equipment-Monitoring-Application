#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}Starting Farmer Equipment Monitoring Application${NC}"
echo -e "${GREEN}================================================${NC}"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}Error: Docker is not running. Please start Docker and try again.${NC}"
    exit 1
fi

# Parse command line arguments
REBUILD=false
DETACHED=true

while [[ $# -gt 0 ]]; do
    case $1 in
        --rebuild)
            REBUILD=true
            shift
            ;;
        --foreground)
            DETACHED=false
            shift
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            echo "Usage: ./run.sh [--rebuild] [--foreground]"
            exit 1
            ;;
    esac
done

# Rebuild if requested
if [ "$REBUILD" = true ]; then
    echo -e "\n${YELLOW}Rebuilding Docker images...${NC}"
    ./build.sh
    if [ $? -ne 0 ]; then
        echo -e "${RED}Build failed. Exiting.${NC}"
        exit 1
    fi
fi

# Stop any running containers
echo -e "\n${YELLOW}Stopping any running containers...${NC}"
docker-compose down

# Start services
echo -e "\n${YELLOW}Starting services...${NC}"
if [ "$DETACHED" = true ]; then
    docker-compose up -d
else
    docker-compose up
fi

if [ "$DETACHED" = true ]; then
    echo -e "\n${GREEN}================================================${NC}"
    echo -e "${GREEN}Application Started Successfully!${NC}"
    echo -e "${GREEN}================================================${NC}"

    echo -e "\n${BLUE}Service URLs:${NC}"
    echo -e "${YELLOW}Frontend:${NC}           http://localhost:3000"
    echo -e "${YELLOW}API Gateway:${NC}        http://localhost:8080"
    echo -e "${YELLOW}Eureka Server:${NC}      http://localhost:8761"
    echo -e "${YELLOW}Config Server:${NC}      http://localhost:8888"
    echo -e "${YELLOW}RabbitMQ Management:${NC} http://localhost:15672 (guest/guest)"
    echo -e "${YELLOW}Farmers Service:${NC}    http://localhost:8081"
    echo -e "${YELLOW}Equipment Service:${NC}  http://localhost:8082"
    echo -e "${YELLOW}Supervision Service:${NC} http://localhost:8083"

    echo -e "\n${BLUE}Database Connections:${NC}"
    echo -e "${YELLOW}Farmers DB:${NC}    localhost:5432"
    echo -e "${YELLOW}Equipment DB:${NC}  localhost:5433"
    echo -e "${YELLOW}Supervision DB:${NC} localhost:5434"

    echo -e "\n${BLUE}Useful Commands:${NC}"
    echo -e "${YELLOW}View logs:${NC}         docker-compose logs -f [service-name]"
    echo -e "${YELLOW}Stop services:${NC}     docker-compose down"
    echo -e "${YELLOW}Restart service:${NC}   docker-compose restart [service-name]"
    echo -e "${YELLOW}View status:${NC}       docker-compose ps"

    echo -e "\n${YELLOW}Checking service health...${NC}"
    sleep 5
    docker-compose ps
fi
