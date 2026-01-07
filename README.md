# Farm Equipment Monitoring Application

[![Build Status](https://img.shields.io/github/actions/workflow/status/YasserZr/Farmer-Equipment-Monitoring-Application/ci-cd.yml?branch=main)](https://github.com/YasserZr/Farmer-Equipment-Monitoring-Application/actions)
[![Coverage](https://img.shields.io/codecov/c/github/YasserZr/Farmer-Equipment-Monitoring-Application)](https://codecov.io/gh/YasserZr/Farmer-Equipment-Monitoring-Application)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Next.js-14.2.3-black.svg)](https://nextjs.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-Ready-326CE5.svg)](https://kubernetes.io/)

A comprehensive microservices-based platform for monitoring and managing farm equipment, enabling real-time tracking, maintenance scheduling, and farmer management with enterprise-grade reliability and scalability.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Installation](#installation)
  - [Local Development](#local-development-setup)
  - [Docker Compose](#docker-compose-setup)
  - [Kubernetes](#kubernetes-deployment)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Communication Patterns](#communication-patterns)
- [Monitoring](#monitoring)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## 🌟 Overview

The Farm Equipment Monitoring Application is a production-ready microservices platform designed to help agricultural businesses monitor and manage their equipment fleet. The system provides real-time equipment tracking, automated maintenance scheduling, farmer management, and comprehensive reporting capabilities.

**Key Highlights:**
- 🚀 Microservices architecture with Spring Cloud
- 🎯 Real-time equipment monitoring and alerts
- 📊 Interactive dashboards with analytics
- 🔄 Event-driven communication with RabbitMQ
- 🐳 Docker and Kubernetes ready
- ✅ Comprehensive test coverage (56+ tests)
- 🔒 Production-grade security and observability

## 🏗️ Architecture

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Frontend Layer                                  │
│  ┌────────────────────────────────────────────────────────────────────────┐ │
│  │                    Next.js Application (Port 3000)                      │ │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                 │ │
│  │  │   Farmers    │  │  Equipment   │  │  Dashboard   │                 │ │
│  │  │   Module     │  │    Module    │  │    Module    │                 │ │
│  │  └──────────────┘  └──────────────┘  └──────────────┘                 │ │
│  │            React Query │ Zustand │ shadcn/ui                           │ │
│  └─────────────────────┬──────────────────────────────────────────────────┘ │
└────────────────────────┼──────────────────────────────────────────────────────┘
                         │ HTTPS/REST
                         ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Infrastructure Layer                                │
│  ┌────────────────────┐                    ┌─────────────────────────────┐  │
│  │  API Gateway       │                    │  Eureka Server              │  │
│  │  (Port 8080)       │◄───────────────────┤  (Service Discovery)        │  │
│  │  - Routing         │                    │  (Port 8761)                │  │
│  │  - Load Balancing  │                    └─────────────────────────────┘  │
│  │  - Rate Limiting   │                                                      │
│  └──────┬─────────────┘                    ┌─────────────────────────────┐  │
│         │                                  │  Config Server              │  │
│         │                                  │  (Centralized Config)       │  │
│         │                                  │  (Port 8888)                │  │
│         │                                  └─────────────────────────────┘  │
└─────────┼──────────────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         Business Services Layer                              │
│  ┌─────────────────┐   ┌──────────────────┐   ┌─────────────────────────┐  │
│  │ Farmers Service │   │ Equipment Service│   │ Supervision Service     │  │
│  │   (Port 8081)   │   │   (Port 8082)    │   │     (Port 8083)         │  │
│  │                 │   │                  │   │                         │  │
│  │ - Farmer CRUD   │   │ - Equipment CRUD │   │ - Event Processing      │  │
│  │ - Farm Mgmt     │   │ - Status Updates │   │ - Alert Generation      │  │
│  │ - Permissions   │   │ - Telemetry      │   │ - Report Generation     │  │
│  │                 │   │ - Maintenance    │   │ - Analytics             │  │
│  └────────┬────────┘   └────────┬─────────┘   └───────────┬─────────────┘  │
│           │                     │                          │                │
│           ▼                     ▼                          ▼                │
│  ┌──────────────────┐  ┌──────────────────┐     ┌──────────────────────┐  │
│  │  PostgreSQL      │  │  PostgreSQL      │     │  PostgreSQL          │  │
│  │  farmers_db      │  │  equipment_db    │     │  supervision_db      │  │
│  │  (Port 5432)     │  │  (Port 5433)     │     │  (Port 5434)         │  │
│  └──────────────────┘  └──────────────────┘     └──────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            Messaging Layer                                   │
│  ┌────────────────────────────────────────────────────────────────────────┐ │
│  │                      RabbitMQ (Ports 5672, 15672)                       │ │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                 │ │
│  │  │  Equipment   │  │    Status    │  │    Alert     │                 │ │
│  │  │   Events     │  │    Events    │  │   Events     │                 │ │
│  │  └──────────────┘  └──────────────┘  └──────────────┘                 │ │
│  │         Async Event-Driven Communication                                │ │
│  └────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Architecture Patterns

**Microservices**: Independent, loosely coupled services with separate databases (Database per Service pattern)

**API Gateway**: Single entry point for all client requests with routing, load balancing, and security

**Service Discovery**: Eureka for dynamic service registration and discovery

**Configuration Management**: Centralized configuration with Spring Cloud Config

**Event-Driven**: Asynchronous communication via RabbitMQ for loose coupling

**CQRS**: Separation of read and write operations for better scalability

## 🛠️ Technologies

### Backend
- **Framework**: Spring Boot 3.2.0, Spring Cloud 2023.0.0
- **Language**: Java 17
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Configuration**: Spring Cloud Config
- **Messaging**: RabbitMQ 3.12
- **Database**: PostgreSQL 15
- **ORM**: Spring Data JPA, Hibernate
- **Migration**: Flyway
- **Mapping**: MapStruct
- **Validation**: Jakarta Bean Validation
- **Documentation**: SpringDoc OpenAPI 3 (Swagger)
- **Monitoring**: Spring Boot Actuator, Micrometer, Prometheus

### Frontend
- **Framework**: Next.js 14.2.3, React 18.3.1
- **Language**: TypeScript 5.4
- **State Management**: Zustand, React Query (TanStack Query)
- **Forms**: React Hook Form, Zod validation
- **UI Components**: shadcn/ui, Radix UI
- **Styling**: Tailwind CSS 3.4
- **Charts**: Recharts
- **HTTP Client**: Axios
- **Date Handling**: date-fns

### Infrastructure
- **Containerization**: Docker 24+, Docker Compose
- **Orchestration**: Kubernetes 1.25+
- **Web Server**: NGINX (Ingress)
- **SSL/TLS**: cert-manager, Let's Encrypt
- **Build Tools**: Maven 3.9+, npm

### DevOps & Testing
- **CI/CD**: GitHub Actions
- **Testing (Backend)**: JUnit 5, Mockito, TestContainers, MockMvc, WireMock
- **Testing (Frontend)**: Vitest, React Testing Library, jsdom
- **Code Quality**: SonarQube, Codecov
- **Security**: Trivy vulnerability scanner
- **Monitoring**: Prometheus, Grafana
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)

## ✨ Features

### Farmer Management
- ✅ Complete CRUD operations for farmers
- ✅ Farm profile management
- ✅ Multi-farm support per farmer
- ✅ Contact information management
- ✅ Permission and access control

### Equipment Monitoring
- ✅ Real-time equipment tracking
- ✅ GPS location tracking with map visualization
- ✅ Equipment status monitoring (Active, Maintenance, Inactive)
- ✅ Telemetry data collection and analysis
- ✅ Equipment assignment to farmers and farms
- ✅ Equipment history and audit trail

### Maintenance Management
- ✅ Scheduled maintenance tracking
- ✅ Maintenance alerts and notifications
- ✅ Maintenance history logs
- ✅ Automatic maintenance recommendations
- ✅ Service provider management

### Supervision & Analytics
- ✅ Real-time event processing
- ✅ Automated alert generation
- ✅ Equipment performance analytics
- ✅ Custom report generation
- ✅ Dashboard with key metrics
- ✅ Equipment utilization reports
- ✅ Predictive maintenance insights

### System Features
- ✅ RESTful APIs with OpenAPI documentation
- ✅ Async event-driven communication
- ✅ Horizontal auto-scaling (HPA)
- ✅ Service discovery and load balancing
- ✅ Centralized configuration management
- ✅ Health checks and monitoring
- ✅ Distributed logging and tracing
- ✅ Containerized deployment (Docker)
- ✅ Kubernetes orchestration
- ✅ CI/CD pipeline with automated testing

## 📋 Prerequisites

### Required Software

| Software | Version | Purpose |
|----------|---------|---------|
| Java JDK | 17+ | Backend services |
| Node.js | 18+ | Frontend application |
| Maven | 3.9+ | Backend build tool |
| Docker | 24+ | Containerization |
| Docker Compose | 2.20+ | Local orchestration |
| Kubernetes | 1.25+ | Production orchestration |
| kubectl | 1.25+ | Kubernetes CLI |
| PostgreSQL | 15+ | Database (local dev) |
| RabbitMQ | 3.12+ | Message broker (local dev) |
| Git | 2.40+ | Version control |

### Optional Tools

- **Minikube**: For local Kubernetes testing
- **Postman/Insomnia**: API testing
- **pgAdmin**: PostgreSQL management
- **DBeaver**: Universal database tool

### System Requirements

**Minimum:**
- CPU: 4 cores
- RAM: 8 GB
- Storage: 20 GB

**Recommended:**
- CPU: 8+ cores
- RAM: 16+ GB
- Storage: 50+ GB
- SSD for better performance

## 🚀 Quick Start

Choose your preferred deployment method:

### Option 1: Docker Compose (Recommended for Development)

```bash
# Clone the repository
git clone https://github.com/YasserZr/Farmer-Equipment-Monitoring-Application.git
cd Farmer-Equipment-Monitoring-Application

# Build and start all services
docker-compose up -d

# Access the application
# Frontend: http://localhost:3000
# API Gateway: http://localhost:8080
# Eureka Dashboard: http://localhost:8761
```

### Option 2: Local Development

```bash
# Start infrastructure services
docker-compose up -d postgres-farmers postgres-equipment postgres-supervision rabbitmq

# Start backend services (in separate terminals)
cd backend/eureka-server && mvn spring-boot:run
cd backend/config-server && mvn spring-boot:run
cd backend/api-gateway && mvn spring-boot:run
cd backend/farmers-service && mvn spring-boot:run
cd backend/equipment-service && mvn spring-boot:run
cd backend/supervision-service && mvn spring-boot:run

# Start frontend
cd frontend && npm install && npm run dev
```

### Option 3: Kubernetes (Production)

```bash
# Deploy to Kubernetes cluster
cd kubernetes
./deploy.sh

# Or use kubectl directly
kubectl apply -k kubernetes/
```

## 📦 Installation

### Local Development Setup

#### 1. Clone the Repository

```bash
git clone https://github.com/YasserZr/Farmer-Equipment-Monitoring-Application.git
cd Farmer-Equipment-Monitoring-Application
```

#### 2. Set Up Backend Services

**Start Infrastructure Services:**

```bash
# Start PostgreSQL databases
docker run -d --name postgres-farmers \
  -e POSTGRES_DB=farmers_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine

docker run -d --name postgres-equipment \
  -e POSTGRES_DB=equipment_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5433:5432 \
  postgres:15-alpine

docker run -d --name postgres-supervision \
  -e POSTGRES_DB=supervision_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5434:5432 \
  postgres:15-alpine

# Start RabbitMQ
docker run -d --name rabbitmq \
  -e RABBITMQ_DEFAULT_USER=guest \
  -e RABBITMQ_DEFAULT_PASS=guest \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3.12-management-alpine
```

**Build and Run Backend Services:**

```bash
# 1. Start Eureka Server (Service Discovery)
cd backend/eureka-server
mvn clean install
mvn spring-boot:run
# Wait for startup (http://localhost:8761)

# 2. Start Config Server (in new terminal)
cd backend/config-server
mvn clean install
mvn spring-boot:run
# Wait for startup (http://localhost:8888)

# 3. Start API Gateway (in new terminal)
cd backend/api-gateway
mvn clean install
mvn spring-boot:run
# Wait for startup (http://localhost:8080)

# 4. Start Business Services (in separate terminals)
cd backend/farmers-service && mvn spring-boot:run
cd backend/equipment-service && mvn spring-boot:run
cd backend/supervision-service && mvn spring-boot:run
```

#### 3. Set Up Frontend

```bash
cd frontend

# Install dependencies
npm install

# Run development server
npm run dev
```

**Access Points:**
- Frontend: http://localhost:3000
- API Gateway: http://localhost:8080
- Eureka Dashboard: http://localhost:8761
- RabbitMQ Management: http://localhost:15672 (guest/guest)

### Docker Compose Setup

Docker Compose provides a complete environment with all services pre-configured.

#### 1. Build All Images

```bash
# Build backend images
cd backend
for service in eureka-server config-server api-gateway farmers-service equipment-service supervision-service; do
  docker build -t farm-monitoring-$service:latest -f $service/Dockerfile $service/
done

# Build frontend image
cd ../frontend
docker build -t farm-monitoring-frontend:latest .
```

Or use the provided script:

```bash
# Linux/macOS
./build-images.sh

# Windows
.\build-images.bat
```

#### 2. Start All Services

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Check service health
docker-compose ps
```

#### 3. Initialize Databases (First Time Only)

The databases are automatically initialized via Flyway migrations when services start.

#### 4. Stop Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (clean state)
docker-compose down -v
```

**Service URLs:**
- Frontend: http://localhost:3000
- API Gateway: http://localhost:8080
- Farmers Service: http://localhost:8081
- Equipment Service: http://localhost:8082
- Supervision Service: http://localhost:8083
- Eureka: http://localhost:8761
- Config Server: http://localhost:8888
- RabbitMQ Management: http://localhost:15672

See [DOCKER_README.md](DOCKER_README.md) for detailed Docker documentation.

### Kubernetes Deployment

Kubernetes deployment provides production-grade orchestration with auto-scaling, self-healing, and zero-downtime updates.

#### Prerequisites

- Kubernetes cluster (Minikube, GKE, EKS, or AKS)
- kubectl configured
- Docker images built and pushed to registry

#### 1. Prepare Docker Images

```bash
# Tag images for your registry
export DOCKER_REGISTRY=your-dockerhub-username

for service in eureka-server config-server api-gateway farmers-service equipment-service supervision-service; do
  docker tag farm-monitoring-$service:latest $DOCKER_REGISTRY/farm-monitoring-$service:latest
  docker push $DOCKER_REGISTRY/farm-monitoring-$service:latest
done

docker tag farm-monitoring-frontend:latest $DOCKER_REGISTRY/farm-monitoring-frontend:latest
docker push $DOCKER_REGISTRY/farm-monitoring-frontend:latest
```

#### 2. Configure Kubernetes Resources

Update image references in `kubernetes/kustomization.yaml`:

```yaml
images:
  - name: farm-monitoring-eureka-server
    newName: your-dockerhub-username/farm-monitoring-eureka-server
    newTag: latest
  # ... update other images
```

Update secrets in `kubernetes/secrets/`:

```bash
# Generate base64 encoded secrets
echo -n 'your-password' | base64
```

#### 3. Deploy to Kubernetes

**Automated Deployment:**

```bash
cd kubernetes

# Linux/macOS
./deploy.sh

# Windows
.\deploy.bat
```

**Manual Deployment:**

```bash
# Create namespace
kubectl apply -f kubernetes/namespace.yaml

# Deploy ConfigMaps and Secrets
kubectl apply -f kubernetes/configmaps/
kubectl apply -f kubernetes/secrets/

# Deploy databases
kubectl apply -f kubernetes/databases/
kubectl wait --for=condition=ready pod -l app=farmers-db -n farm-monitoring --timeout=300s
kubectl wait --for=condition=ready pod -l app=equipment-db -n farm-monitoring --timeout=300s
kubectl wait --for=condition=ready pod -l app=supervision-db -n farm-monitoring --timeout=300s

# Deploy RabbitMQ
kubectl apply -f kubernetes/rabbitmq/
kubectl wait --for=condition=ready pod -l app=rabbitmq -n farm-monitoring --timeout=300s

# Deploy infrastructure services
kubectl apply -f kubernetes/eureka-server/
kubectl apply -f kubernetes/config-server/
kubectl wait --for=condition=ready pod -l app=eureka-server -n farm-monitoring --timeout=300s
kubectl wait --for=condition=ready pod -l app=config-server -n farm-monitoring --timeout=300s

# Deploy business services and API Gateway
kubectl apply -f kubernetes/farmers-service/
kubectl apply -f kubernetes/equipment-service/
kubectl apply -f kubernetes/supervision-service/
kubectl apply -f kubernetes/api-gateway/

# Deploy frontend
kubectl apply -f kubernetes/frontend/

# Deploy Ingress
kubectl apply -f kubernetes/ingress/
```

#### 4. Verify Deployment

```bash
# Check all pods are running
kubectl get pods -n farm-monitoring

# Check services
kubectl get svc -n farm-monitoring

# Check ingress
kubectl get ingress -n farm-monitoring

# Check HPA status
kubectl get hpa -n farm-monitoring
```

#### 5. Access the Application

**For Minikube:**

```bash
# Enable ingress
minikube addons enable ingress

# Get Minikube IP
minikube ip

# Add to /etc/hosts (Linux/macOS) or C:\Windows\System32\drivers\etc\hosts (Windows)
# Replace <MINIKUBE_IP> with actual IP
<MINIKUBE_IP> farm-monitoring.example.com
<MINIKUBE_IP> api.farm-monitoring.example.com
<MINIKUBE_IP> admin.farm-monitoring.example.com

# Access application
# http://farm-monitoring.example.com
```

**For Cloud Providers (GKE/EKS/AKS):**

Configure DNS records to point to the LoadBalancer IP:

```bash
# Get LoadBalancer IP
kubectl get ingress -n farm-monitoring

# Configure DNS A records:
# farm-monitoring.example.com -> <EXTERNAL-IP>
# api.farm-monitoring.example.com -> <EXTERNAL-IP>
```

**Port Forwarding (Development):**

```bash
# Frontend
kubectl port-forward -n farm-monitoring svc/frontend 3000:3000

# API Gateway
kubectl port-forward -n farm-monitoring svc/api-gateway 8080:8080

# Eureka
kubectl port-forward -n farm-monitoring svc/eureka-server 8761:8761

# RabbitMQ Management
kubectl port-forward -n farm-monitoring svc/rabbitmq 15672:15672
```

See [KUBERNETES_README.md](kubernetes/KUBERNETES_README.md) for detailed Kubernetes documentation.

## ⚙️ Configuration

### Backend Configuration

#### Application Properties

Configuration is managed through Spring Cloud Config Server. Each service has a configuration file in `backend/config-server/src/main/resources/config/`.

**Example: farmers-service.yml**

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/farmers_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

#### Environment Variables

Override configuration using environment variables:

```bash
# Database
export SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/farmers_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=your-secure-password

# Eureka
export EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

# RabbitMQ
export SPRING_RABBITMQ_HOST=rabbitmq
export SPRING_RABBITMQ_PORT=5672
export SPRING_RABBITMQ_USERNAME=guest
export SPRING_RABBITMQ_PASSWORD=your-secure-password
```

### Frontend Configuration

#### Environment Variables

Create `.env.local` in the frontend directory:

```bash
# API Configuration
NEXT_PUBLIC_API_URL=http://localhost:8080

# Feature Flags
NEXT_PUBLIC_ENABLE_DEBUG=false

# Analytics (optional)
NEXT_PUBLIC_GA_MEASUREMENT_ID=G-XXXXXXXXXX
```

**Production Environment:**

```bash
NEXT_PUBLIC_API_URL=https://api.farm-monitoring.example.com
NEXT_PUBLIC_ENABLE_DEBUG=false
```

### Docker Configuration

Edit `docker-compose.yml` for Docker deployments:

```yaml
services:
  farmers-service:
    environment:
      - SPRING_DATASOURCE_PASSWORD=secure-password
      - SPRING_RABBITMQ_PASSWORD=secure-password
```

### Kubernetes Configuration

Update ConfigMaps and Secrets:

```bash
# Edit ConfigMap
kubectl edit configmap application-config -n farm-monitoring

# Update Secret
kubectl create secret generic database-secrets \
  --from-literal=POSTGRES_PASSWORD=new-secure-password \
  -n farm-monitoring \
  --dry-run=client -o yaml | kubectl apply -f -
```

## 📚 API Documentation

### OpenAPI (Swagger) Documentation

Each service provides interactive API documentation:

- **Farmers Service**: http://localhost:8081/swagger-ui.html
- **Equipment Service**: http://localhost:8082/swagger-ui.html
- **Supervision Service**: http://localhost:8083/swagger-ui.html
- **API Gateway**: http://localhost:8080/swagger-ui.html

### API Examples

#### Create a Farmer

```bash
curl -X POST http://localhost:8080/api/farmers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+1234567890",
    "address": "123 Farm Road",
    "city": "FarmVille",
    "country": "USA"
  }'
```

#### Get All Farmers

```bash
curl -X GET "http://localhost:8080/api/farmers?page=0&size=10"
```

#### Create Equipment

```bash
curl -X POST http://localhost:8080/api/equipment/pumps \
  -H "Content-Type: application/json" \
  -d '{
    "serialNumber": "PUMP-001",
    "model": "Model-X",
    "manufacturer": "AgriTech",
    "farmerId": 1,
    "latitude": 40.7128,
    "longitude": -74.0060
  }'
```

#### Update Equipment Status

```bash
curl -X PATCH http://localhost:8080/api/equipment/pumps/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "MAINTENANCE",
    "reason": "Scheduled maintenance"
  }'
```

### Authentication

**Note**: Authentication is not yet implemented. Future versions will include:
- JWT-based authentication
- OAuth2/OpenID Connect support
- Role-based access control (RBAC)

## 🧪 Testing

### Backend Tests

#### Run All Tests

```bash
# Navigate to service directory
cd backend/farmers-service

# Run tests
mvn clean test

# Run with coverage
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

#### Run Specific Test Classes

```bash
# Unit tests
mvn test -Dtest=FarmerServiceTest

# Integration tests
mvn test -Dtest=FarmerRepositoryTest

# Controller tests
mvn test -Dtest=FarmerControllerTest
```

#### Run Integration Tests with TestContainers

```bash
mvn verify -Pintegration-tests
```

### Frontend Tests

```bash
cd frontend

# Run all tests
npm test

# Run in watch mode
npm run test:watch

# Run with coverage
npm run test:coverage

# View coverage report
open coverage/index.html
```

### Run CI/CD Pipeline Locally

```bash
# Backend tests (for each service)
for service in eureka-server config-server api-gateway farmers-service equipment-service supervision-service; do
  echo "Testing $service..."
  cd backend/$service
  mvn clean test
  cd ../..
done

# Frontend tests
cd frontend
npm run lint
npm run type-check
npm run test:coverage
```

### Test Coverage

Current test coverage:
- **Backend**: 80%+ line coverage
- **Frontend**: 80%+ line coverage
- **Total Tests**: 56+ tests

See [TESTING.md](TESTING.md) for comprehensive testing documentation.

## 📁 Project Structure

```
Farmer-Equipment-Monitoring-Application/
├── .github/
│   └── workflows/
│       └── ci-cd.yml                    # GitHub Actions CI/CD pipeline
├── backend/
│   ├── eureka-server/                   # Service discovery
│   │   ├── src/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   ├── config-server/                   # Configuration management
│   │   ├── src/
│   │   │   └── main/resources/config/   # Service configurations
│   │   ├── Dockerfile
│   │   └── pom.xml
│   ├── api-gateway/                     # API Gateway
│   │   ├── src/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   ├── farmers-service/                 # Farmer management
│   │   ├── src/
│   │   │   ├── main/java/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── entity/
│   │   │   │   ├── dto/
│   │   │   │   └── mapper/
│   │   │   ├── test/java/              # Unit & integration tests
│   │   │   └── resources/
│   │   │       └── db/migration/        # Flyway migrations
│   │   ├── Dockerfile
│   │   └── pom.xml
│   ├── equipment-service/               # Equipment monitoring
│   │   ├── src/
│   │   │   ├── main/java/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── client/              # Feign clients
│   │   │   │   ├── event/               # RabbitMQ events
│   │   │   │   └── ...
│   │   │   └── test/java/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   └── supervision-service/             # Event processing & alerts
│       ├── src/
│       ├── Dockerfile
│       └── pom.xml
├── frontend/
│   ├── app/                             # Next.js app directory
│   │   ├── farmers/                     # Farmer pages
│   │   ├── equipment/                   # Equipment pages
│   │   ├── dashboard/                   # Dashboard page
│   │   └── layout.tsx
│   ├── components/                      # React components
│   │   ├── ui/                          # shadcn/ui components
│   │   ├── farmers/
│   │   ├── equipment/
│   │   └── dashboard/
│   ├── hooks/                           # Custom React hooks
│   ├── lib/                             # Utilities
│   ├── store/                           # Zustand stores
│   ├── __tests__/                       # Tests
│   │   ├── components/
│   │   ├── pages/
│   │   └── hooks/
│   ├── public/                          # Static assets
│   ├── Dockerfile
│   ├── package.json
│   └── vitest.config.ts
├── kubernetes/
│   ├── namespace.yaml
│   ├── configmaps/
│   ├── secrets/
│   ├── databases/                       # StatefulSets
│   ├── rabbitmq/
│   ├── eureka-server/
│   ├── config-server/
│   ├── api-gateway/
│   ├── farmers-service/
│   ├── equipment-service/
│   ├── supervision-service/
│   ├── frontend/
│   ├── ingress/
│   ├── kustomization.yaml
│   ├── deploy.sh
│   ├── deploy.bat
│   └── KUBERNETES_README.md
├── docker-compose.yml                   # Docker Compose orchestration
├── build-images.sh                      # Docker build script
├── build-images.bat
├── run-services.sh                      # Service startup script
├── run-services.bat
├── DOCKER_README.md                     # Docker documentation
├── TESTING.md                           # Testing documentation
├── TEST_IMPLEMENTATION_SUMMARY.md
├── KUBERNETES_IMPLEMENTATION_SUMMARY.md
└── README.md                            # This file
```

### Key Directories

- **backend/**: Spring Boot microservices
- **frontend/**: Next.js application
- **kubernetes/**: Kubernetes manifests
- **.github/workflows/**: CI/CD pipelines

## 🔄 Communication Patterns

### Synchronous Communication (REST)

**Service-to-Service via Feign Client:**

```java
@FeignClient(name = "farmers-service")
public interface FarmerClient {
    @GetMapping("/api/farmers/{id}")
    FarmerDTO getFarmerById(@PathVariable("id") Long id);
}
```

**Usage in Equipment Service:**

```java
@Service
public class EquipmentService {
    @Autowired
    private FarmerClient farmerClient;
    
    public void assignEquipmentToFarmer(Long equipmentId, Long farmerId) {
        // Validate farmer exists via synchronous call
        FarmerDTO farmer = farmerClient.getFarmerById(farmerId);
        // Assign equipment
    }
}
```

### Asynchronous Communication (RabbitMQ)

**Event Publishing:**

```java
@Service
public class EquipmentEventPublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void publishStatusChanged(Pump pump, String oldStatus) {
        EquipmentStatusEvent event = new EquipmentStatusEvent(
            pump.getId(),
            oldStatus,
            pump.getStatus().name(),
            LocalDateTime.now()
        );
        
        rabbitTemplate.convertAndSend(
            "equipment.exchange",
            "equipment.status.changed",
            event
        );
    }
}
```

**Event Consumption:**

```java
@Component
public class EquipmentEventListener {
    @RabbitListener(queues = "equipment.status.queue")
    public void handleStatusChanged(EquipmentStatusEvent event) {
        // Process status change
        // Generate alerts if needed
        log.info("Equipment {} status changed: {} -> {}",
            event.getEquipmentId(),
            event.getOldStatus(),
            event.getNewStatus());
    }
}
```

### Communication Flow Example

1. **Client** sends HTTP request to **API Gateway**
2. **API Gateway** routes to **Farmers Service** (synchronous)
3. **Farmers Service** creates farmer and returns response
4. **Client** sends request to create equipment
5. **Equipment Service** validates farmer exists via **Feign Client** (synchronous)
6. **Equipment Service** creates equipment
7. **Equipment Service** publishes `EquipmentCreatedEvent` to **RabbitMQ** (asynchronous)
8. **Supervision Service** consumes event and creates monitoring record

## 📊 Monitoring

### Health Checks

All services expose health endpoints:

```bash
# Service health
curl http://localhost:8081/actuator/health

# Detailed health
curl http://localhost:8081/actuator/health/details
```

### Metrics

Prometheus metrics available at:

```bash
# Service metrics
curl http://localhost:8081/actuator/prometheus
```

### Kubernetes Monitoring

```bash
# Pod status
kubectl get pods -n farm-monitoring

# Pod logs
kubectl logs -f <pod-name> -n farm-monitoring

# Resource usage
kubectl top pods -n farm-monitoring
kubectl top nodes

# Events
kubectl get events -n farm-monitoring --sort-by='.lastTimestamp'
```

### Recommended Monitoring Stack

**Prometheus + Grafana:**

```bash
# Install Prometheus Operator
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm install prometheus prometheus-community/kube-prometheus-stack -n monitoring

# Access Grafana
kubectl port-forward -n monitoring svc/prometheus-grafana 3000:80
# Default credentials: admin / prom-operator
```

**Key Metrics to Monitor:**
- CPU and memory usage
- Request rate and latency
- Error rate (4xx, 5xx)
- Database connection pool
- RabbitMQ queue depth
- JVM metrics (heap, GC)

## 🔧 Troubleshooting

### Common Issues

#### 1. Services Not Registering with Eureka

**Symptom:** Services show as DOWN in Eureka dashboard

**Solutions:**
```bash
# Check Eureka is running
curl http://localhost:8761/

# Check service logs
docker-compose logs farmers-service

# Verify Eureka URL in configuration
# Should be: http://eureka-server:8761/eureka/ (Docker)
# Or: http://localhost:8761/eureka/ (Local)
```

#### 2. Database Connection Errors

**Symptom:** `Connection refused` or `Unknown database`

**Solutions:**
```bash
# Check database is running
docker ps | grep postgres

# Test connection
docker exec -it postgres-farmers psql -U postgres -d farmers_db

# Verify credentials and URL
# Format: jdbc:postgresql://host:port/database

# Reset database (WARNING: deletes data)
docker-compose down -v
docker-compose up -d postgres-farmers postgres-equipment postgres-supervision
```

#### 3. RabbitMQ Connection Issues

**Symptom:** `Connection refused` on port 5672

**Solutions:**
```bash
# Check RabbitMQ is running
docker ps | grep rabbitmq

# Check management UI
http://localhost:15672 (guest/guest)

# Restart RabbitMQ
docker-compose restart rabbitmq

# Check logs
docker-compose logs rabbitmq
```

#### 4. Frontend Can't Connect to Backend

**Symptom:** `Network Error` or `CORS` errors

**Solutions:**
```bash
# Verify API Gateway is running
curl http://localhost:8080/actuator/health

# Check NEXT_PUBLIC_API_URL
cat frontend/.env.local

# For Docker, ensure services are on same network
docker network ls
docker network inspect farmer-equipment-monitoring-application_default

# Test API directly
curl http://localhost:8080/api/farmers
```

#### 5. Kubernetes Pods Not Starting

**Symptom:** `ImagePullBackOff`, `CrashLoopBackOff`

**Solutions:**
```bash
# Check pod status
kubectl describe pod <pod-name> -n farm-monitoring

# Check pod logs
kubectl logs <pod-name> -n farm-monitoring

# For ImagePullBackOff (Minikube)
eval $(minikube docker-env)
docker images  # Verify images exist
# Rebuild images if needed

# For CrashLoopBackOff
# Check liveness/readiness probe settings
# Increase initialDelaySeconds if needed

# Restart deployment
kubectl rollout restart deployment <deployment-name> -n farm-monitoring
```

#### 6. Port Already in Use

**Symptom:** `Address already in use` error

**Solutions:**
```bash
# Linux/macOS - Find process using port
lsof -i :8080
kill -9 <PID>

# Windows - Find process using port
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Change port in application.yml
server:
  port: 8090  # Use different port
```

#### 7. Out of Memory Errors

**Symptom:** `OutOfMemoryError: Java heap space`

**Solutions:**
```bash
# Increase Java heap size
export JAVA_OPTS="-Xmx2g -Xms512m"

# Or in Dockerfile
ENV JAVA_OPTS="-Xmx2g -Xms512m"

# For Docker Compose
services:
  farmers-service:
    environment:
      - JAVA_OPTS=-Xmx2g -Xms512m

# For Kubernetes
resources:
  limits:
    memory: "2Gi"
  requests:
    memory: "1Gi"
```

#### 8. Build Failures

**Symptom:** Maven build fails or npm install errors

**Solutions:**
```bash
# Maven - Clear cache and rebuild
mvn clean install -U

# Maven - Skip tests temporarily
mvn clean install -DskipTests

# npm - Clear cache
npm cache clean --force
rm -rf node_modules package-lock.json
npm install

# Check Java version
java -version  # Should be 17+

# Check Node version
node -v  # Should be 18+
```

### Getting Help

- **Issues**: [GitHub Issues](https://github.com/YasserZr/Farmer-Equipment-Monitoring-Application/issues)
- **Discussions**: [GitHub Discussions](https://github.com/YasserZr/Farmer-Equipment-Monitoring-Application/discussions)
- **Documentation**: See additional docs in repository

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

### Getting Started

1. **Fork the repository**
2. **Clone your fork**
   ```bash
   git clone https://github.com/your-username/Farmer-Equipment-Monitoring-Application.git
   cd Farmer-Equipment-Monitoring-Application
   ```

3. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

### Development Guidelines

#### Code Style

**Backend (Java):**
- Follow Java Code Conventions
- Use Spring Boot best practices
- Add JavaDoc for public methods
- Write unit tests for new code

**Frontend (TypeScript):**
- Follow TypeScript/React best practices
- Use ESLint configuration
- Add JSDoc for complex functions
- Write tests for components

#### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add farmer search functionality
fix: resolve database connection timeout
docs: update API documentation
test: add tests for equipment service
chore: update dependencies
```

### Testing Requirements

All contributions must include tests:

```bash
# Backend - ensure tests pass
mvn clean test

# Frontend - ensure tests pass
npm test

# Check coverage
npm run test:coverage
```

### Pull Request Process

1. **Update documentation** if needed
2. **Add tests** for new features
3. **Ensure all tests pass**
4. **Update CHANGELOG.md** (if applicable)
5. **Create Pull Request** with clear description
6. **Address review comments**

### Code Review

- At least one approval required
- All CI checks must pass
- Test coverage must not decrease

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2026 Farm Equipment Monitoring Application

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 🙏 Acknowledgments

- **Spring Boot Team** - Excellent framework and documentation
- **Next.js Team** - Amazing React framework
- **TestContainers** - Simplified integration testing
- **shadcn/ui** - Beautiful UI components
- **Open Source Community** - For all the amazing libraries used in this project

## 📞 Support

For support and questions:

- 📧 Email: support@farm-monitoring.example.com
- 💬 Discord: [Join our community](https://discord.gg/farm-monitoring)
- 📖 Documentation: [https://docs.farm-monitoring.example.com](https://docs.farm-monitoring.example.com)
- 🐛 Bug Reports: [GitHub Issues](https://github.com/YasserZr/Farmer-Equipment-Monitoring-Application/issues)

---

**Built with ❤️ by the Farm Equipment Monitoring Team**

[⬆ Back to top](#farm-equipment-monitoring-application)
