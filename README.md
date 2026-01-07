# Farmer & Equipment Monitoring Application

A comprehensive microservices-based platform for monitoring and managing agricultural equipment and farm operations.

## 📋 Overview

This application provides real-time monitoring of connected agricultural equipment (pumps and sensors), farm management capabilities, and automated supervision workflows. Built using modern microservices architecture patterns, it enables farmers to efficiently manage their equipment, receive alerts, and make data-driven decisions.

## 🏗️ Architecture

The system is composed of the following microservices:

### Backend Services (Spring Boot 3.x)
- **Farmers Service**: Manages farmer profiles, farm entities, and permission systems
- **Equipment Service**: Handles connected pumps, sensors, and telemetry data
- **Supervision Service**: Processes asynchronous events, triggers alerts and notifications

### Infrastructure Services
- **API Gateway** (Spring Cloud Gateway): Single entry point for all client requests with authentication and routing
- **Eureka Server**: Service discovery and registry
- **Config Server**: Centralized configuration management (Git-backed)

### Frontend
- **Next.js 14+**: Modern React-based web application with SSR support

### Messaging & Data
- **RabbitMQ**: Asynchronous event streaming (Equipment → Supervision)
- **PostgreSQL**: Database per service pattern
- **Optional**: TimescaleDB/InfluxDB for telemetry time-series data

For detailed architecture documentation, see [docs/architecture.md](./docs/architecture.md).

## 🛠️ Technology Stack

| Layer | Technologies |
|-------|-------------|
| Backend Framework | Spring Boot 3.x, Spring Cloud, Spring WebFlux |
| Frontend | Next.js 14+, React, TypeScript |
| Service Discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Configuration | Spring Cloud Config (Git-backed) |
| Messaging | RabbitMQ (AMQP) |
| Databases | PostgreSQL, TimescaleDB (optional) |
| Security | OAuth2, JWT, Spring Security |
| Containerization | Docker, Docker Compose |
| Orchestration | Kubernetes |
| Observability | OpenTelemetry, Prometheus, Grafana, ELK/Loki |
| Build Tools | Maven 3.9+, npm/yarn |

## 📁 Project Structure

```
Farmer-Equipment-Monitoring-Application/
├── backend/
│   ├── farmers-service/           # Farmer & Farm management
│   ├── equipment-service/         # Connected equipment & telemetry
│   └── supervision-service/       # Event processing & alerts
├── infrastructure/
│   ├── api-gateway/               # Spring Cloud Gateway
│   ├── eureka-server/             # Service discovery
│   └── config-server/             # Centralized config
├── frontend/                      # Next.js web application
├── config-repo/                   # Git-backed configuration files
├── docker/                        # Docker Compose and service Dockerfiles
├── kubernetes/                    # K8s manifests (deployments, services, ingress)
│   ├── deployments/
│   ├── services/
│   └── configmaps/
├── scripts/                       # Build and deployment automation scripts
└── docs/                          # Architecture and API documentation
```

## 🚀 Getting Started

### Prerequisites

- **Java 17+** (Eclipse Temurin or OpenJDK recommended)
- **Maven 3.9+** or **Gradle 8+**
- **Node.js 18+** and **npm/yarn**
- **Docker** and **Docker Compose**
- **Kubernetes cluster** (Minikube, Docker Desktop, or cloud provider)
- **Git**

### Local Development Setup

#### 1. Clone the repository

```bash
git clone https://github.com/YasserZr/Farmer-Equipment-Monitoring-Application.git
cd Farmer-Equipment-Monitoring-Application
```

#### 2. Start infrastructure services with Docker Compose

```bash
cd docker
docker-compose up -d postgres rabbitmq
```

#### 3. Start Config Server and Eureka Server

```bash
# Terminal 1: Config Server
cd infrastructure/config-server
mvn spring-boot:run

# Terminal 2: Eureka Server
cd infrastructure/eureka-server
mvn spring-boot:run
```

Wait for Eureka to be accessible at `http://localhost:8761`.

#### 4. Start microservices

```bash
# Terminal 3: Farmers Service
cd backend/farmers-service
mvn spring-boot:run

# Terminal 4: Equipment Service
cd backend/equipment-service
mvn spring-boot:run

# Terminal 5: Supervision Service
cd backend/supervision-service
mvn spring-boot:run

# Terminal 6: API Gateway
cd infrastructure/api-gateway
mvn spring-boot:run
```

#### 5. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

The application will be accessible at:
- **Frontend**: http://localhost:3000
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761

### Running with Docker Compose (Full Stack)

The easiest way to run the entire application stack is using Docker Compose.

**Quick Start:**

**Windows:**
```powershell
.\build.bat  # Build all Docker images
.\run.bat    # Start all services
```

**Linux/macOS:**
```bash
chmod +x build.sh run.sh
./build.sh   # Build all Docker images
./run.sh     # Start all services
```

**Manual Docker Compose:**
```bash
# Build and start all services
docker-compose up --build -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

**Access Points:**
- **Frontend**: http://localhost:3000
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)

For comprehensive Docker documentation, see [DOCKER_README.md](./DOCKER_README.md).

### Deploying to Kubernetes

**Automated Deployment:**

**Windows:**
```powershell
cd kubernetes
.\deploy.bat
```

**Linux/macOS:**
```bash
cd kubernetes
chmod +x deploy.sh
./deploy.sh
```

**Manual Deployment:**
```bash
cd kubernetes

# Deploy all resources
kubectl apply -k .

# Or step by step
kubectl apply -f namespace.yaml
kubectl apply -f configmaps/
kubectl apply -f secrets/
kubectl apply -f databases/
kubectl apply -f rabbitmq/
kubectl apply -f eureka-server/
kubectl apply -f config-server/
kubectl apply -f api-gateway/
kubectl apply -f farmers-service/
kubectl apply -f equipment-service/
kubectl apply -f supervision-service/
kubectl apply -f frontend/
kubectl apply -f ingress/

# Check deployment status
kubectl get pods -n farm-monitoring
kubectl get svc -n farm-monitoring
```

**Access the Application:**
```bash
# Port forwarding for local access
kubectl port-forward -n farm-monitoring svc/frontend 3000:3000
kubectl port-forward -n farm-monitoring svc/api-gateway 8080:8080
```

For comprehensive Kubernetes documentation, see [kubernetes/KUBERNETES_README.md](./kubernetes/KUBERNETES_README.md).

## 🔧 Configuration

Configuration is managed centrally via the Config Server and stored in the `config-repo/` directory.

- **Development**: Config Server reads from local `config-repo/`
- **Production**: Config Server reads from a Git repository (configure in `application.yml`)

### Environment Variables

Each service can be configured using environment variables:

```bash
# Example for Farmers Service
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/farmers_db
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/
```

## 📊 Observability

### Metrics & Monitoring
- **Prometheus**: Scrapes metrics from `/actuator/prometheus` endpoints
- **Grafana**: Dashboards for service health, JVM metrics, business KPIs

### Distributed Tracing
- **OpenTelemetry**: Automatic instrumentation
- **Jaeger/Zipkin**: Trace visualization

### Logging
- Structured JSON logs
- Centralized aggregation with ELK Stack or Grafana Loki

## 🧪 Testing

```bash
# Run unit tests for a service
cd backend/farmers-service
mvn test

# Run integration tests
mvn verify

# Frontend tests
cd frontend
npm test
```

## 🔐 Security

- **Authentication**: OAuth2/OIDC with JWT tokens
- **Authorization**: Role-based access control (RBAC)
- **Service-to-Service**: Client credentials flow or mTLS
- **Secrets Management**: Kubernetes Secrets or HashiCorp Vault
- **Transport Security**: TLS/HTTPS everywhere

## 📝 API Documentation

API documentation is available via Swagger/OpenAPI:
- **API Gateway**: http://localhost:8080/swagger-ui.html
- Each microservice also exposes `/swagger-ui.html` when running standalone

## 🤝 Contributing

Please read [CONTRIBUTING.md](./CONTRIBUTING.md) for details on our code of conduct, commit message conventions, and the process for submitting pull requests.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.

## 👥 Authors

- **YasserZr** - [GitHub Profile](https://github.com/YasserZr)

## 🙏 Acknowledgments

- Spring Boot and Spring Cloud teams
- Next.js and Vercel
- Open source community

## 📞 Support

For questions or issues:
- Open an issue on GitHub
- Review the [architecture documentation](./docs/architecture.md)
- Check the troubleshooting guide in the wiki

---

**Built with ❤️ for modern agriculture**
A microservices-based web application designed to help farmers monitor their farms and connected equipment in real time. The platform provides a centralized and intelligent solution for managing farms, tracking equipment health, and ensuring efficient maintenance operations.
