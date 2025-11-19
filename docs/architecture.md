# Farmer & Equipment Monitoring — Microservices Architecture

## Overview

This document describes a microservices architecture for the Farmer and Equipment Monitoring Application. It covers high-level architecture, service responsibilities, synchronous and asynchronous communication flows, technology choices for each component, database strategy (one DB per service), API Gateway routing, containerization, Kubernetes deployment patterns, and non-functional requirements.

Target platforms and frameworks:
- Backend: Spring Boot 3.x microservices
- Frontend: Next.js 14+
- Async messaging: RabbitMQ
- Service discovery: Eureka
- Centralized configuration: Spring Cloud Config Server (backed by Git)
- Containerization: Docker
- Orchestration: Kubernetes

---

## 1. High-level architecture diagram (text description)

Imagine the following layers from left to right:

- Clients: Web (Next.js) and optionally Mobile. These connect to the API Gateway (HTTPS).
- API Gateway (Spring Cloud Gateway) — single external entrypoint, routing, authentication, request rate-limiting, circuit breakers.
- Service Mesh / Discovery layer: Eureka registry used by services to locate each other (internal service-to-service calls use discovery).
- Microservices:
  - Farmers Microservice: manages Farmer and Farm entities, their permissions and profiles. Owns its database.
  - Equipment Microservice: manages ConnectedPump and ConnectedSensor devices, their lifecycle, and telemetry ingestion. Owns its database.
  - Supervision Microservice: receives asynchronous status events (alarms, telemetry anomalies) and runs workflows/notifications. Owns its database.
  - Config Server: Spring Cloud Config, backs service configuration from a Git repo.
  - (Optional) Auth Service / Identity Provider: issues OAuth2/JWT tokens (e.g., Keycloak or AWS Cognito).
- Messaging: RabbitMQ cluster — handles asynchronous events from Equipment to Supervision.
- Datastores: each microservice has its own database (recommended: PostgreSQL for relational needs; time-series store for high-frequency telemetry optional).
- Observability: centralized logging (ELK/EFK), distributed tracing (OpenTelemetry / Zipkin), metrics (Prometheus + Grafana).

Flows on the diagram:
- Synchronous: Equipment -> Farmers (permission check) — via HTTP REST using service discovery and mutual TLS or OAuth2. The Equipment service performs a synchronous request to the Farmers service to verify permissions before executing critical operations.
- Asynchronous: Equipment -> RabbitMQ -> Supervision — telemetry/status events published to RabbitMQ by Equipment; Supervision consumes asynchronously and triggers processing/alerts.

---

## 2. Communication flow between services

2.1. External request (frontend to API Gateway)
- Client sends request to API Gateway (HTTPS + JWT).
- Gateway validates JWT (or delegates to Auth) and forwards request to the target service (farmers, equipment, supervision).

2.2. Equipment → Farmers (Synchronous permission verification)
- Use case: Equipment (device or Equipment microservice) attempts an operation that requires farmer permission (e.g., start pump).
- Steps:
  1. Equipment service constructs a permission check request: `GET /api/farmers/{farmerId}/permissions?resource={pumpId}&action=start`.
  2. Equipment service discovers Farmers service via Eureka; use a load-balanced WebClient (Spring WebFlux WebClient or RestTemplate with Spring Cloud LoadBalancer) and includes its service-to-service client credentials (client credentials flow) or mTLS token.
  3. Farmers service verifies the permission from its DB and responds with 200/403 and an optional JSON body containing policy metadata.
  4. Equipment service proceeds or aborts based on the response. On 5xx from Farmers, Equipment retries with an exponential backoff (limited attempts) and fails safe (deny) if verification cannot be completed within timeout.

Design notes:
- Keep the permission check lightweight — cache permission predicates locally (short TTL) in Equipment service when safe, but always re-check for sensitive operations.
- Use timeouts (e.g., 1s-2s) and circuit breakers on the client side (Resilience4j / Spring Cloud Circuit Breaker) to prevent cascading failures.

2.3. Equipment → Supervision (Asynchronous status events via RabbitMQ)
- Use case: Device status updates, alarms, telemetry anomalies are published by Equipment microservice as events.
- Steps:
  1. Equipment publishes message to RabbitMQ exchange `equipment.status` with routing keys like `pump.<pumpId>.status` or `sensor.<sensorId>.telemetry`.
  2. Supervision declares queues bound to relevant routing keys (e.g., `supervision.alarms`, `supervision.telemetry`).
  3. Supervision consumes messages, acknowledges on success, or rejects/requeues on transient failures.
  4. Supervision runs rules, stores events in its DB, triggers notifications (email/SMS/webhooks), and can emit further events (e.g., `supervision.alert.created`) for other consumers.

Reliability measures:
- Use persistent messages and durable queues.
- Apply publisher confirms and consumer acknowledgements.
- Add dead-letter queues (DLQ) for poison messages.

2.4. Cross-cutting: discovery and config
- Services register with Eureka at startup.
- Services fetch configuration from Spring Cloud Config Server on startup; Config Server reads a Git repo holding YAML/Properties per service.

---

## 3. Technology stack for each component

- Frontend
  - Framework: Next.js 14+
  - Rendering: Server-side rendering for dashboards, client-side for device control UI
  - Auth: OAuth2/OIDC via Auth provider (Keycloak / Auth0) with JWT

- API Gateway
  - Spring Cloud Gateway (Spring Boot 3.x)
  - Filters: authentication (JWT validation), rate limiting, request rate/size limits, path rewriting, CORS
  - Integration: OpenTelemetry instrumentation

- Farmers Microservice
  - Framework: Spring Boot 3.x with Spring Web (or WebFlux if reactive), Spring Data JPA
  - DB: PostgreSQL (primary), Liquibase/Flyway for schema migrations
  - Security: Resource server (JWT validation) + OAuth2 client for service-to-service
  - Discovery: Eureka client
  - Config: Spring Cloud Config client

- Equipment Microservice
  - Framework: Spring Boot 3.x, Spring WebFlux (recommended for concurrency) or Spring Web
  - DB: PostgreSQL (metadata), timeseries DB optional (InfluxDB or TimescaleDB) for high-frequency telemetry
  - Messaging: Spring AMQP or RabbitMQ Java client for publishing events
  - Security: OAuth2 client / service credentials + mTLS optional
  - Discovery: Eureka client
  - Config: Spring Cloud Config client

- Supervision Microservice
  - Framework: Spring Boot 3.x, Spring AMQP consumers
  - DB: PostgreSQL for events and workflow state; optionally Elasticsearch for event search
  - Messaging: RabbitMQ consumer, DLQs
  - Discovery: Eureka client
  - Config: Spring Cloud Config

- Config Server
  - Spring Cloud Config Server (backed by Git repository)
  - Store: Git (centralized configs), can integrate with Vault for secrets

- Service Discovery
  - Netflix Eureka server (or Spring Cloud Discovery server)

- Messaging
  - RabbitMQ cluster (production deployment with persistence, HA, mirroring or quorum queues)

- Observability
  - Tracing: OpenTelemetry -> Jaeger/Zipkin
  - Metrics: Micrometer -> Prometheus
  - Logging: Structured logs -> Logstash / Fluentd -> Elasticsearch / Grafana Loki

- Containerization & Orchestration
  - Docker (multi-stage builds)
  - Kubernetes (Deployments, Services, StatefulSets for DBs where needed)
  - Ingress controller (Traefik / NGINX)

---

## 4. Database strategy (one DB per service)

Principles:
- Each microservice owns its data and schema. No other service directly reads/writes another service's DB.
- Use asynchronous events to share state where eventual consistency is acceptable.
- Choose DB per service based on access patterns (relational vs time-series).

Recommended mapping:
- Farmers service: PostgreSQL
  - Entities: Farmer, Farm, Permissions, Roles
  - Use: transactional data (ACID), joins, constraints
  - Migrations: Flyway/Liquibase

- Equipment service: PostgreSQL + optional TimeSeries DB (TimescaleDB / InfluxDB)
  - Entities: ConnectedPump, ConnectedSensor, DeviceMetadata
  - Telemetry: high-frequency time-series stored in TimescaleDB/InfluxDB or forwarded to dedicated pipeline (Kafka -> TS DB)

- Supervision service: PostgreSQL (events/warnings) + Elasticsearch for searching historical events

- Config Server: Git-backed config; secrets in Vault (or Kubernetes Secrets in cluster)

Backup and operational considerations:
- Backup each DB independently (logical and physical backups). Schedule nightly backups and periodic full snapshots.
- Use operator-managed databases in Kubernetes (e.g., Crunchy Postgres Operator, Zalando Postgres Operator) or managed cloud DBs.
- Monitor connections, slow queries, and retention for telemetry data (rolling downsample strategy).

Data exchange patterns:
- For read scenarios where other services need farmer info, expose explicit API endpoints in Farmers service (no direct DB access).
- Consider CQRS / read-model projection using events for heavy read traffic (e.g., replicate parts of farmer profile into Equipment read-model via events).

---

## 5. API Gateway routing strategy

Goals:
- Centralized authentication and request filtering
- Path-based routing to service clusters
- Provide API surface for clients; internal services use service discovery

Example routing table (Spring Cloud Gateway routes):

- Path `/api/farmers/**` -> `lb://FARMERS-SERVICE`
- Path `/api/equipment/**` -> `lb://EQUIPMENT-SERVICE`
- Path `/api/supervision/**` -> `lb://SUPERVISION-SERVICE`
- Path `/api/config/**` -> `lb://CONFIG-SERVER` (read-only for clients if needed)
- Path `/api/auth/**` -> `lb://AUTH-SERVICE`

Routing rules and filters:
- Authentication: a pre-filter validates JWT tokens and extracts roles/claims. For certificate-based mTLS, Gateway terminates TLS and validates client certs.
- Authorization: Gateway can handle coarse-grained RBAC (route-level) while fine-grained checks remain in services.
- Rate-limiting: per-client and per-route rate limits to protect backend.
- Circuit breaker: for critical downstream calls, apply a circuit-breaker filter.
- Global exception handling and consistent error payloads.

Internal calls:
- Inter-service calls should bypass the external API Gateway when possible (use internal network and Eureka). If the Gateway is used for internal calls, ensure proper mTLS and routing rules to avoid loops.

---

## 6. Non-functional requirements implementation

6.1. Scalability
- Stateless microservices: design services to be stateless (store state in DBs and caches) so they can scale horizontally via Kubernetes Deployments.
- Autoscaling: configure Kubernetes Horizontal Pod Autoscaler (HPA) based on CPU, memory, or custom metrics (Prometheus adapter) for services receiving variable load (Gateway, Equipment API).
- Partitioning: shard telemetry data by device/time to manage volume; offload analytics workloads to separate cluster or data warehouse.
- Caching: apply distributed cache for read-heavy endpoints (Redis or Hazelcast) with appropriate TTLs and cache invalidation triggered by events.

6.2. Fault tolerance & resilience
- Circuit breakers: Resilience4j integrated into service clients.
- Bulkheads: separate thread pools or limited concurrency per downstream remote call.
- Retries: exponential backoff with jitter for transient errors.
- Timeouts: set conservative timeouts for all external calls (e.g., 1s-3s depending on operation).
- Graceful degradation: when Farmers service unavailable, Equipment can use cached policies or present degraded UI with clear messaging.
- Messaging durability: RabbitMQ with persistent messages, durable queues, and quorum queues for availability.

6.3. Centralized configuration
- Use Spring Cloud Config Server (Git-backed) to store environment-specific configuration. For secrets, integrate HashiCorp Vault or Kubernetes Secrets and avoid storing secrets in Git.
- Enable refresh scope for beans that should respond to config changes and use `/actuator/refresh` or Spring Cloud Bus (with RabbitMQ) to notify services of config changes.

6.4. Security
- Auth: OAuth2 / OIDC for human clients and client-credentials for service-to-service.
- Transport: TLS everywhere (ingress, internal traffic, RabbitMQ TLS). Consider mTLS for sensitive inter-service calls.
- Secrets: store in Vault or K8s Secrets with restricted RBAC. Do not store secrets in Git.
- Auditing: store audit trails for critical operations (e.g., pump on/off) in the Farmers or Supervision DB and optionally append-only logs.

6.5. Observability
- Metrics: Micrometer -> Prometheus. Create dashboards in Grafana for KPIs (requests/sec, error rate, consumer lag, DB latency).
- Tracing: OpenTelemetry instrumentation propagated across services (trace ids in messages). Collector -> Jaeger.
- Logging: structured logs (JSON) centralized to ELK/EFK or Grafana Loki. Correlate logs and traces using trace id.

6.6. Operational concerns
- Health checks: each service exposes `/actuator/health` (readiness & liveness) and Kubernetes uses these for rolling restarts.
- Readiness: only register service with Eureka when ready (after DB migration done).
- Migrations: automate DB migrations via Flyway during CI/CD or as init containers with locks.

---

## 7. Docker & Kubernetes guidance (practical patterns)

7.1. Docker
- Use multi-stage builds and minimal runtime images (Eclipse Temurin / OpenJDK slim). Example Dockerfile pattern for Spring Boot:

```dockerfile
# build stage
FROM maven:3.9.0-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

# runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

7.2. Kubernetes
- Deploy each microservice as a Deployment with a Service (ClusterIP). Use HPA for autoscaling.
- Use StatefulSet for stateful services when required (e.g., rabbitmq with PersistentVolumes, or DB if operator not used).
- Use a managed DB service or a Postgres operator rather than running vanilla Postgres in pods where possible.
- Example Deployment concerns:
  - Liveness/Readiness probes hitting `/actuator/health/liveness` and `/actuator/health/readiness`.
  - Resource requests/limits defined.
  - Sidecars: Prometheus metrics exporter and/or OpenTelemetry collector as DaemonSet or sidecar.

7.3. RabbitMQ on Kubernetes
- Run a RabbitMQ cluster (StatefulSet) or use managed RabbitMQ. Use quorum queues for safety.
- Expose durable queues for Supervision and publisher confirms for Equipment.

7.4. Config & secrets
- Mount application config via environment variables supplied from Config Server or via Kubernetes Secrets for pod-level secrets.
- Use Vault with Kubernetes auth to deliver secrets at runtime.

7.5. CI/CD
- Build images in CI (GitHub Actions / GitLab CI / Jenkins), tag images with commit SHA, push to registry.
- Deploy via GitOps (Argo CD) or pipeline (kubectl apply / helm upgrade).

---

## 8. Example integration & developer notes

- Local development:
  - Run Config Server locally and point services to local git-backed config.
  - Use Docker Compose for a local dev stack (Eureka, Config Server, RabbitMQ, Postgres, farmers, equipment, supervision).
- Production:
  - Use Kubernetes with managed Postgres and RabbitMQ if available.
  - Use an external identity provider (Keycloak, Auth0) for centralized auth.

Smart defaults for implementation:
- Use reactive WebClient in Equipment for concurrency and backpressure when contacting Farmers.
- Keep RPC permission checks small and use caching with short TTL.
- Emit structured events from Equipment including versioning in event schema to support evolution.

---

## 9. Appendix: sample RabbitMQ topology

- Exchanges:
  - `equipment.status` (topic)
  - `supervision.alerts` (topic)

- Queues:
  - `supervision.telemetry` -> binds `equipment.status` with `*.telemetry`routing key
  - `supervision.alarms` -> binds `equipment.status` with `*.alarm` routing key
  - `supervision.dlq` -> dead-letter queue

---

## 10. Next steps (implementation roadmap)

- Create Git repo for `config` and add service configuration YAML for each environment.
- Scaffold Spring Boot projects for Farmers, Equipment, Supervision, Gateway, Config Server, and Eureka server.
- Create Dockerfiles and CI pipeline to build and push images.
- Create Kubernetes manifests or Helm charts and test deployment to a dev cluster.

---

For questions or to continue, I can:
- scaffold initial Spring Boot project templates,
- create a `docker-compose.yml` for local development,
- or generate example Kubernetes manifests and Helm charts.

---

Document created by GitHub Copilot.
