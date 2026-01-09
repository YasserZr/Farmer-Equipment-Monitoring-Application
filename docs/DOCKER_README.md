# Docker Deployment Guide

This document provides comprehensive instructions for deploying the Farmer Equipment Monitoring Application using Docker and Docker Compose.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Architecture Overview](#architecture-overview)
- [Quick Start](#quick-start)
- [Building Docker Images](#building-docker-images)
- [Running the Application](#running-the-application)
- [Service Configuration](#service-configuration)
- [Docker Compose Services](#docker-compose-services)
- [Environment Variables](#environment-variables)
- [Networking](#networking)
- [Data Persistence](#data-persistence)
- [Health Checks](#health-checks)
- [Troubleshooting](#troubleshooting)
- [Production Considerations](#production-considerations)

## Prerequisites

Before you begin, ensure you have the following installed:

- **Docker**: Version 20.10 or higher
- **Docker Compose**: Version 2.0 or higher
- **Git**: For cloning the repository
- **Minimum System Requirements**:
  - RAM: 8GB (16GB recommended)
  - CPU: 4 cores (8 cores recommended)
  - Disk Space: 20GB free space

### Installing Docker

**Windows:**
Download and install Docker Desktop from [docker.com](https://www.docker.com/products/docker-desktop)

**macOS:**
```bash
brew install --cask docker
```

**Linux:**
```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
```

## Architecture Overview

The application consists of the following containerized services:

### Infrastructure Services
- **PostgreSQL (x3)**: Separate databases for Farmers, Equipment, and Supervision services
- **RabbitMQ**: Message broker for asynchronous communication
- **Eureka Server**: Service discovery and registration
- **Config Server**: Centralized configuration management
- **API Gateway**: Single entry point for all microservices

### Business Services
- **Farmers Service**: Manages farmer information (Port 8081)
- **Equipment Service**: Handles equipment monitoring (Port 8082)
- **Supervision Service**: Processes events and supervision logic (Port 8083)

### Frontend
- **Next.js Application**: Web interface for monitoring (Port 3000)

## Quick Start

### Windows

1. **Clone the repository:**
```powershell
git clone https://github.com/your-repo/farmer-equipment-monitoring.git
cd farmer-equipment-monitoring
```

2. **Build all Docker images:**
```powershell
.\build.bat
```

3. **Start the application:**
```powershell
.\run.bat
```

4. **Access the application:**
- Frontend: http://localhost:3000
- API Gateway: http://localhost:8080
- Eureka Dashboard: http://localhost:8761

### Linux/macOS

1. **Clone the repository:**
```bash
git clone https://github.com/your-repo/farmer-equipment-monitoring.git
cd farmer-equipment-monitoring
```

2. **Make scripts executable:**
```bash
chmod +x build.sh run.sh
```

3. **Build all Docker images:**
```bash
./build.sh
```

4. **Start the application:**
```bash
./run.sh
```

5. **Access the application:**
- Frontend: http://localhost:3000
- API Gateway: http://localhost:8080
- Eureka Dashboard: http://localhost:8761

## Building Docker Images

### Build All Services

**Windows:**
```powershell
.\build.bat
```

**Linux/macOS:**
```bash
./build.sh
```

### Build Individual Services

**Backend Services:**
```bash
# Eureka Server
docker build -t farm-monitoring/eureka-server:latest ./backend/eureka-server

# Config Server
docker build -t farm-monitoring/config-server:latest ./backend/config-server

# API Gateway
docker build -t farm-monitoring/api-gateway:latest ./backend/api-gateway

# Farmers Service
docker build -t farm-monitoring/farmers-service:latest ./backend/farmers-service

# Equipment Service
docker build -t farm-monitoring/equipment-service:latest ./backend/equipment-service

# Supervision Service
docker build -t farm-monitoring/supervision-service:latest ./backend/supervision-service
```

**Frontend:**
```bash
docker build -t farm-monitoring/frontend:latest ./frontend
```

### Multi-Stage Build Process

Each Dockerfile uses multi-stage builds for optimization:

**Spring Boot Services:**
1. **Build Stage**: Uses Maven to compile and package the application
2. **Runtime Stage**: Uses lightweight JRE image with compiled JAR

**Next.js Frontend:**
1. **Dependencies Stage**: Installs npm packages
2. **Builder Stage**: Builds the Next.js application
3. **Runner Stage**: Serves the optimized production build

## Running the Application

### Start All Services

**Windows:**
```powershell
# Start in background (detached mode)
.\run.bat

# Start with rebuild
.\run.bat --rebuild

# Start in foreground (see logs)
.\run.bat --foreground
```

**Linux/macOS:**
```bash
# Start in background (detached mode)
./run.sh

# Start with rebuild
./run.sh --rebuild

# Start in foreground (see logs)
./run.sh --foreground
```

### Using Docker Compose Directly

```bash
# Start all services in background
docker-compose up -d

# Start specific service
docker-compose up -d farmers-service

# View logs
docker-compose logs -f

# View logs for specific service
docker-compose logs -f farmers-service

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Restart specific service
docker-compose restart farmers-service
```

### Service Startup Order

The application starts services in the following order (managed by health checks):

1. **Databases** (farmers-db, equipment-db, supervision-db)
2. **RabbitMQ**
3. **Eureka Server**
4. **Config Server**
5. **Business Services** (farmers-service, equipment-service, supervision-service)
6. **API Gateway**
7. **Frontend**

## Service Configuration

### Port Mappings

| Service | Container Port | Host Port | Description |
|---------|---------------|-----------|-------------|
| Frontend | 3000 | 3000 | Next.js web interface |
| API Gateway | 8080 | 8080 | Main API endpoint |
| Farmers Service | 8081 | 8081 | Farmers microservice |
| Equipment Service | 8082 | 8082 | Equipment microservice |
| Supervision Service | 8083 | 8083 | Supervision microservice |
| Eureka Server | 8761 | 8761 | Service discovery |
| Config Server | 8888 | 8888 | Configuration server |
| RabbitMQ AMQP | 5672 | 5672 | Message broker |
| RabbitMQ Management | 15672 | 15672 | RabbitMQ UI |
| Farmers DB | 5432 | 5432 | PostgreSQL |
| Equipment DB | 5432 | 5433 | PostgreSQL |
| Supervision DB | 5432 | 5434 | PostgreSQL |

### Access Points

**Web Interfaces:**
- Application Dashboard: http://localhost:3000
- Eureka Dashboard: http://localhost:8761
- RabbitMQ Management: http://localhost:15672 (guest/guest)

**API Endpoints:**
- API Gateway: http://localhost:8080
- Farmers API: http://localhost:8080/farmers
- Equipment API: http://localhost:8080/equipment
- Supervision API: http://localhost:8080/supervision

**Direct Service Access (for development):**
- Farmers Service: http://localhost:8081
- Equipment Service: http://localhost:8082
- Supervision Service: http://localhost:8083

## Docker Compose Services

### Database Services

**farmers-db:**
```yaml
- Image: postgres:15-alpine
- Database: farmers_db
- User: postgres
- Password: postgres
- Port: 5432 → 5432
- Volume: farmers-db-data
```

**equipment-db:**
```yaml
- Image: postgres:15-alpine
- Database: equipment_db
- User: postgres
- Password: postgres
- Port: 5432 → 5433
- Volume: equipment-db-data
```

**supervision-db:**
```yaml
- Image: postgres:15-alpine
- Database: supervision_db
- User: postgres
- Password: postgres
- Port: 5432 → 5434
- Volume: supervision-db-data
```

### Message Broker

**rabbitmq:**
```yaml
- Image: rabbitmq:3.12-management-alpine
- User: guest
- Password: guest
- AMQP Port: 5672
- Management Port: 15672
- Volume: rabbitmq-data
```

### Infrastructure Services

**eureka-server:**
```yaml
- Service Discovery
- Port: 8761
- Health Check: /actuator/health
- Profile: docker
```

**config-server:**
```yaml
- Centralized Configuration
- Port: 8888
- Depends on: eureka-server
- Health Check: /actuator/health
- Profile: docker
```

**api-gateway:**
```yaml
- API Gateway
- Port: 8080
- Depends on: eureka-server, config-server
- Health Check: /actuator/health
- Profile: docker
```

### Business Services

**farmers-service:**
```yaml
- Port: 8081
- Database: farmers-db
- Depends on: farmers-db, rabbitmq, eureka-server, config-server
- Health Check: /actuator/health
```

**equipment-service:**
```yaml
- Port: 8082
- Database: equipment-db
- Depends on: equipment-db, rabbitmq, eureka-server, config-server
- Health Check: /actuator/health
```

**supervision-service:**
```yaml
- Port: 8083
- Database: supervision-db
- Depends on: supervision-db, rabbitmq, eureka-server, config-server
- Health Check: /actuator/health
```

### Frontend Service

**frontend:**
```yaml
- Port: 3000
- Depends on: api-gateway
- Health Check: /api/health
- API URL: http://localhost:8080
```

## Environment Variables

### Backend Services

All Spring Boot services use the following environment variables:

```yaml
SPRING_PROFILES_ACTIVE: docker
SPRING_DATASOURCE_URL: jdbc:postgresql://[db-host]:5432/[db-name]
SPRING_DATASOURCE_USERNAME: postgres
SPRING_DATASOURCE_PASSWORD: postgres
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
SPRING_CLOUD_CONFIG_URI: http://config-server:8888
SPRING_RABBITMQ_HOST: rabbitmq
SPRING_RABBITMQ_PORT: 5672
SPRING_RABBITMQ_USERNAME: guest
SPRING_RABBITMQ_PASSWORD: guest
```

### Frontend Service

```yaml
NEXT_PUBLIC_API_URL: http://localhost:8080
NODE_ENV: production
```

### Customizing Environment Variables

Create a `.env` file in the root directory:

```env
# Database Credentials
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_secure_password

# RabbitMQ Credentials
RABBITMQ_USER=admin
RABBITMQ_PASSWORD=your_secure_password

# API Configuration
API_GATEWAY_PORT=8080
FRONTEND_PORT=3000

# Database Ports
FARMERS_DB_PORT=5432
EQUIPMENT_DB_PORT=5433
SUPERVISION_DB_PORT=5434
```

Then update `docker-compose.yml` to use environment variables:

```yaml
environment:
  - POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
```

## Networking

All services are connected through a custom bridge network named `farm-network`.

**Benefits:**
- Service discovery by name
- Isolated network environment
- Secure inter-service communication

**DNS Resolution:**
Services can communicate using container names:
- `http://eureka-server:8761`
- `http://api-gateway:8080`
- `jdbc:postgresql://farmers-db:5432/farmers_db`

## Data Persistence

### Docker Volumes

The application uses named volumes for data persistence:

```yaml
volumes:
  farmers-db-data:      # Farmers database data
  equipment-db-data:    # Equipment database data
  supervision-db-data:  # Supervision database data
  rabbitmq-data:        # RabbitMQ messages and config
```

### Backing Up Data

**Backup PostgreSQL Database:**
```bash
# Farmers DB
docker exec farmers-db pg_dump -U postgres farmers_db > farmers_db_backup.sql

# Equipment DB
docker exec equipment-db pg_dump -U postgres equipment_db > equipment_db_backup.sql

# Supervision DB
docker exec supervision-db pg_dump -U postgres supervision_db > supervision_db_backup.sql
```

**Restore PostgreSQL Database:**
```bash
# Farmers DB
docker exec -i farmers-db psql -U postgres farmers_db < farmers_db_backup.sql
```

**Backup RabbitMQ:**
```bash
docker exec rabbitmq rabbitmqctl export_definitions rabbitmq_backup.json
```

### Volume Management

```bash
# List volumes
docker volume ls

# Inspect volume
docker volume inspect farmers-db-data

# Remove unused volumes
docker volume prune

# Remove specific volume (data will be lost!)
docker volume rm farmers-db-data
```

## Health Checks

Each service includes health checks to ensure proper startup and operation.

### Health Check Configuration

**Spring Boot Services:**
```dockerfile
HEALTHCHECK --interval=30s --timeout=10s --retries=5 --start-period=90s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health
```

**Frontend:**
```dockerfile
HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=30s \
  CMD node -e "require('http').get('http://localhost:3000/api/health')"
```

### Checking Service Health

```bash
# Check all services
docker-compose ps

# Check specific service health
docker inspect --format='{{.State.Health.Status}}' farmers-service

# View health check logs
docker inspect --format='{{json .State.Health}}' farmers-service | jq
```

## Troubleshooting

### Common Issues

**1. Services not starting:**
```bash
# Check logs
docker-compose logs -f [service-name]

# Check service status
docker-compose ps

# Restart specific service
docker-compose restart [service-name]
```

**2. Database connection errors:**
```bash
# Verify database is healthy
docker-compose ps farmers-db

# Check database logs
docker-compose logs farmers-db

# Connect to database manually
docker exec -it farmers-db psql -U postgres -d farmers_db
```

**3. Port conflicts:**
```bash
# Check which ports are in use
netstat -an | grep LISTEN   # Linux/macOS
netstat -an | findstr LISTENING  # Windows

# Change port mappings in docker-compose.yml
```

**4. Memory issues:**
```bash
# Increase Docker memory limit in Docker Desktop settings
# Or use docker-compose resource limits:
services:
  farmers-service:
    deploy:
      resources:
        limits:
          memory: 1G
```

**5. Network issues:**
```bash
# Recreate network
docker-compose down
docker network rm farm-network
docker-compose up -d
```

### Debugging Commands

```bash
# View resource usage
docker stats

# Execute command in container
docker exec -it farmers-service sh

# View container details
docker inspect farmers-service

# Check container networks
docker network inspect farm-network

# Clean up everything (WARNING: removes all data)
docker-compose down -v
docker system prune -a
```

## Production Considerations

### Security Best Practices

**1. Change Default Credentials:**
```yaml
environment:
  - POSTGRES_PASSWORD=${SECURE_PASSWORD}
  - RABBITMQ_DEFAULT_USER=admin
  - RABBITMQ_DEFAULT_PASS=${SECURE_RABBITMQ_PASSWORD}
```

**2. Use Secrets Management:**
```bash
# Use Docker secrets (Swarm mode)
docker secret create postgres_password postgres_password.txt
```

**3. Enable TLS/SSL:**
- Configure HTTPS for frontend
- Enable SSL for PostgreSQL connections
- Use secure RabbitMQ connections

**4. Limit Container Resources:**
```yaml
deploy:
  resources:
    limits:
      cpus: '1.0'
      memory: 1G
    reservations:
      cpus: '0.5'
      memory: 512M
```

### Performance Optimization

**1. Adjust JVM Memory:**
```yaml
environment:
  - JAVA_OPTS=-Xms512m -Xmx1g
```

**2. Database Connection Pooling:**
```yaml
environment:
  - SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=20
  - SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=5
```

**3. Enable Caching:**
```yaml
environment:
  - SPRING_CACHE_TYPE=redis
  - SPRING_REDIS_HOST=redis
```

### Monitoring and Logging

**1. Centralized Logging:**
```yaml
logging:
  driver: "json-file"
  options:
    max-size: "10m"
    max-file: "3"
```

**2. Add Monitoring Stack:**
```yaml
# docker-compose.monitoring.yml
services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
  
  grafana:
    image: grafana/grafana
    ports:
      - "3001:3000"
```

### High Availability

**1. Scale Services:**
```bash
# Scale business services
docker-compose up -d --scale farmers-service=3
docker-compose up -d --scale equipment-service=3
```

**2. Load Balancer:**
Add nginx or traefik for load balancing multiple instances.

**3. Database Replication:**
Configure PostgreSQL streaming replication for high availability.

### Deployment

**1. CI/CD Integration:**
```yaml
# .github/workflows/deploy.yml
- name: Build Docker images
  run: ./build.sh

- name: Push to registry
  run: |
    docker tag farm-monitoring/frontend:latest registry.example.com/frontend:latest
    docker push registry.example.com/frontend:latest
```

**2. Docker Registry:**
```bash
# Login to registry
docker login registry.example.com

# Tag images
docker tag farm-monitoring/frontend:latest registry.example.com/frontend:latest

# Push images
docker push registry.example.com/frontend:latest
```

**3. Docker Swarm/Kubernetes:**
Consider orchestration platforms for production deployments.

## Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)
- [Next.js Docker Documentation](https://nextjs.org/docs/deployment#docker-image)

## Support

For issues or questions:
- GitHub Issues: [Create an issue](https://github.com/your-repo/farmer-equipment-monitoring/issues)
- Documentation: [Project README](../README.md)
