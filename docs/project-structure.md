# Farmer Equipment Monitoring Application - Project Structure

This file documents the complete folder structure of the microservices project.

```
Farmer-Equipment-Monitoring-Application/
├── README.md                          # Main project documentation
├── CONTRIBUTING.md                    # Contribution guidelines and commit conventions
├── .gitignore                         # Git ignore patterns
├── LICENSE                            # Project license
│
├── docs/                              # Documentation
│   └── architecture.md                # Microservices architecture document
│
├── backend/                           # Backend microservices
│   ├── farmers-service/               # Farmers & Farms management
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/farm/farmers/
│   │   │   │   │   ├── controller/   # REST controllers
│   │   │   │   │   ├── service/      # Business logic
│   │   │   │   │   ├── repository/   # JPA repositories
│   │   │   │   │   ├── model/        # Domain entities
│   │   │   │   │   ├── dto/          # Data transfer objects
│   │   │   │   │   ├── config/       # Configuration classes
│   │   │   │   │   └── FarmersServiceApplication.java
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml
│   │   │   │       └── db/migration/ # Flyway migrations
│   │   │   └── test/java/com/farm/farmers/
│   │   ├── Dockerfile
│   │   └── pom.xml                    # Maven configuration
│   │
│   ├── equipment-service/             # Equipment & telemetry management
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/farm/equipment/
│   │   │   │   │   ├── controller/
│   │   │   │   │   ├── service/
│   │   │   │   │   ├── repository/
│   │   │   │   │   ├── model/
│   │   │   │   │   ├── dto/
│   │   │   │   │   ├── messaging/    # RabbitMQ publishers
│   │   │   │   │   ├── config/
│   │   │   │   │   └── EquipmentServiceApplication.java
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml
│   │   │   │       └── db/migration/
│   │   │   └── test/java/com/farm/equipment/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   └── supervision-service/           # Event processing & alerts
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/farm/supervision/
│       │   │   │   ├── controller/
│       │   │   │   ├── service/
│       │   │   │   ├── repository/
│       │   │   │   ├── model/
│       │   │   │   ├── dto/
│       │   │   │   ├── messaging/     # RabbitMQ consumers
│       │   │   │   ├── config/
│       │   │   │   └── SupervisionServiceApplication.java
│       │   │   └── resources/
│       │   │       ├── application.yml
│       │   │       └── db/migration/
│       │   └── test/java/com/farm/supervision/
│       ├── Dockerfile
│       └── pom.xml
│
├── infrastructure/                    # Infrastructure services
│   ├── api-gateway/                   # Spring Cloud Gateway
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/farm/gateway/
│   │   │   │   │   ├── config/       # Gateway routes config
│   │   │   │   │   ├── filter/       # Custom filters
│   │   │   │   │   └── ApiGatewayApplication.java
│   │   │   │   └── resources/
│   │   │   │       └── application.yml
│   │   │   └── test/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── eureka-server/                 # Service discovery
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/farm/eureka/
│   │   │   │   │   └── EurekaServerApplication.java
│   │   │   │   └── resources/
│   │   │   │       └── application.yml
│   │   │   └── test/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   └── config-server/                 # Centralized configuration
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/farm/config/
│       │   │   │   └── ConfigServerApplication.java
│       │   │   └── resources/
│       │   │       └── application.yml
│       │   └── test/
│       ├── Dockerfile
│       └── pom.xml
│
├── frontend/                          # Next.js frontend
│   ├── src/
│   │   ├── app/                       # Next.js 14 app directory
│   │   │   ├── layout.tsx
│   │   │   ├── page.tsx
│   │   │   ├── farmers/
│   │   │   ├── equipment/
│   │   │   └── supervision/
│   │   └── components/                # Reusable React components
│   │       ├── Navbar.tsx
│   │       ├── Footer.tsx
│   │       └── ...
│   ├── public/                        # Static assets
│   ├── Dockerfile
│   ├── package.json
│   ├── tsconfig.json
│   └── next.config.js
│
├── config-repo/                       # Git-backed configuration files
│   ├── farmers-service.yml
│   ├── equipment-service.yml
│   ├── supervision-service.yml
│   ├── api-gateway.yml
│   └── application.yml                # Shared config
│
├── docker/                            # Docker configuration
│   ├── docker-compose.yml             # Full stack compose file
│   └── init-db.sql                    # Database initialization
│
├── kubernetes/                        # Kubernetes manifests
│   ├── deployments/
│   │   ├── eureka-server-deployment.yaml
│   │   ├── config-server-deployment.yaml
│   │   ├── api-gateway-deployment.yaml
│   │   ├── farmers-service-deployment.yaml
│   │   ├── equipment-service-deployment.yaml
│   │   └── supervision-service-deployment.yaml
│   ├── services/
│   │   ├── eureka-server-service.yaml
│   │   ├── config-server-service.yaml
│   │   ├── api-gateway-service.yaml
│   │   ├── farmers-service-service.yaml
│   │   ├── equipment-service-service.yaml
│   │   └── supervision-service-service.yaml
│   └── configmaps/
│       ├── config-server-configmap.yaml
│       ├── postgres-secret.yaml.example
│       └── rabbitmq-secret.yaml.example
│
└── scripts/                           # Build and deployment scripts
    ├── build-all.sh                   # Build all Docker images (Bash)
    ├── build-all.ps1                  # Build all Docker images (PowerShell)
    ├── deploy-k8s.sh                  # Deploy to Kubernetes (Bash)
    └── deploy-k8s.ps1                 # Deploy to Kubernetes (PowerShell)
```

## Key Directories

### Backend Services (`backend/`)
Each microservice follows the standard Spring Boot Maven project structure with separate packages for controllers, services, repositories, models, and configuration.

### Infrastructure Services (`infrastructure/`)
Contains the core infrastructure components: API Gateway, Eureka Server, and Config Server.

### Frontend (`frontend/`)
Next.js 14+ application using the new App Router. Components are organized by feature.

### Configuration (`config-repo/`)
Centralized configuration managed by Config Server. In production, this should be a separate Git repository.

### Docker (`docker/`)
Docker Compose orchestration for local development and testing.

### Kubernetes (`kubernetes/`)
Production-ready Kubernetes manifests organized by resource type.

### Scripts (`scripts/`)
Automation scripts for building, testing, and deploying the application.

## Notes

- Each backend service has its own database (database-per-service pattern)
- Services communicate via REST (synchronous) and RabbitMQ (asynchronous)
- All services register with Eureka for service discovery
- Configuration is centralized in Config Server
- API Gateway provides single entry point for external clients
- Frontend communicates only with API Gateway, never directly with services
