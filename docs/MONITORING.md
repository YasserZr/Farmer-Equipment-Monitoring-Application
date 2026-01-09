# Monitoring and Logging Configuration Guide

## Overview

This document provides comprehensive information about the monitoring, logging, and tracing infrastructure for the Farm Equipment Monitoring Application.

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     Monitoring Stack                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐                  │
│  │ Services │───▶│Prometheus│───▶│ Grafana  │                  │
│  │ /metrics │    │          │    │Dashboards│                  │
│  └──────────┘    └──────────┘    └──────────┘                  │
│                                                                   │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐                  │
│  │ Services │───▶│ Zipkin   │───▶│  Traces  │                  │
│  │  Traces  │    │          │    │   View   │                  │
│  └──────────┘    └──────────┘    └──────────┘                  │
│                                                                   │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐ │
│  │ Services │───▶│ Logstash │───▶│Elastic-  │───▶│  Kibana  │ │
│  │   Logs   │    │          │    │  search  │    │          │ │
│  └──────────┘    └──────────┘    └──────────┘    └──────────┘ │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

## Components

### 1. Spring Boot Actuator

All microservices are configured with Spring Boot Actuator for exposing health, metrics, and management endpoints.

**Exposed Endpoints:**
- `/actuator/health` - Service health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Available metrics
- `/actuator/prometheus` - Prometheus-formatted metrics
- `/actuator/env` - Environment properties
- `/actuator/loggers` - Logger configuration
- `/actuator/threaddump` - Thread dump
- `/actuator/heapdump` - Heap dump

**Configuration in application.yml:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,env,beans,threaddump,heapdump,loggers
      base-path: /actuator
  endpoint:
    health:
      show-details: always
      probes:
        enabled: true
  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true
```

### 2. Prometheus - Metrics Collection

Prometheus scrapes metrics from all microservices at regular intervals.

**Key Metrics Collected:**
- **JVM Metrics:**
  - `jvm_memory_used_bytes` - Heap and non-heap memory usage
  - `jvm_threads_live_threads` - Thread count
  - `jvm_gc_pause_seconds` - Garbage collection time
  - `process_cpu_usage` - CPU utilization

- **HTTP Metrics:**
  - `http_server_requests_seconds_count` - Request count
  - `http_server_requests_seconds_sum` - Total response time
  - Response time percentiles (P50, P95, P99)

- **Database Metrics:**
  - `hikaricp_connections_active` - Active connections
  - `hikaricp_connections_idle` - Idle connections
  - `hikaricp_connections_max` - Maximum pool size

- **RabbitMQ Metrics:**
  - `rabbitmq_queue_messages` - Queue depth
  - `spring_rabbitmq_acknowledged` - Acknowledged messages

- **Custom Business Metrics:**
  - `equipment_status_total{status="FAULTY"}` - Equipment failures
  - `supervision_events_total{severity="CRITICAL"}` - Critical alerts

**Configuration Location:**
- Docker: `monitoring/prometheus/prometheus.yml`
- Kubernetes: `kubernetes/monitoring/prometheus.yaml`

**Alert Rules:**
Located in `monitoring/prometheus/alerts/service-alerts.yml`:
- ServiceDown - Service unavailability
- HighErrorRate - Error rate > 5%
- HighResponseTime - P95 > 1 second
- HighCPUUsage - CPU > 80%
- DatabaseConnectionPoolExhausted - Pool usage > 90%

### 3. Grafana - Visualization

Grafana provides real-time dashboards for monitoring all aspects of the system.

**Pre-configured Dashboards:**

1. **Microservices Overview** (`microservices-overview.json`)
   - Service health status
   - Request rate per service
   - Response time (P95)
   - Error rate percentage
   - JVM heap memory usage
   - CPU usage
   - Thread count
   - Database connection pool status
   - RabbitMQ queue depth
   - Equipment status distribution

2. **JVM Metrics Detailed** (`jvm-metrics.json`)
   - Heap memory (used/committed/max)
   - Non-heap memory
   - GC pause duration
   - GC rate
   - Thread states
   - Class loading statistics
   - Buffer pools

**Access:**
- Docker: http://localhost:3001
- Kubernetes: Service type LoadBalancer
- Default credentials: admin/admin

### 4. Zipkin - Distributed Tracing

Zipkin provides distributed tracing for tracking requests across microservices.

**Features:**
- End-to-end request tracing
- Service dependency visualization
- Latency analysis
- Error tracking

**Configuration in pom.xml:**
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```

**Configuration in application.yml:**
```yaml
management:
  tracing:
    sampling:
      probability: 1.0  # 100% sampling for development
  zipkin:
    tracing:
      endpoint: http://zipkin:9411/api/v2/spans
```

**Trace Context:**
- `traceId` - Unique identifier for the entire request
- `spanId` - Unique identifier for each service call
- Automatically propagated via HTTP headers

**Access:**
- Docker: http://localhost:9411
- Kubernetes: ClusterIP service

### 5. ELK Stack - Centralized Logging

**Elasticsearch** - Log storage and indexing
**Logstash** - Log processing and transformation
**Kibana** - Log visualization and search

**Log Format:**

All services use JSON logging via Logstash encoder:
```json
{
  "timestamp": "2026-01-09T10:30:45.123Z",
  "level": "INFO",
  "service_name": "farmers-service",
  "logger": "com.farm.farmers.controller.FarmerController",
  "thread": "http-nio-8081-exec-5",
  "message": "Fetching farmer with ID: 123",
  "traceId": "abc123def456",
  "spanId": "789xyz"
}
```

**Logback Configuration:**

Profile-based logging:
- `dev/local` - Human-readable console output
- `prod/docker/kubernetes` - JSON format with async appenders

**Log Rotation:**
- Max file size: 100MB
- Retention: 30 days
- Total size cap: 5GB per service

**Logstash Pipeline:**
- Input: TCP port 5000 (JSON lines)
- Filter: Parse JSON, extract trace context, categorize severity
- Output: Elasticsearch with daily indices

**Access:**
- Kibana: http://localhost:5601
- Elasticsearch: http://localhost:9200

**Index Patterns:**
- `farmers-service-*`
- `equipment-service-*`
- `supervision-service-*`

## Deployment

### Docker Compose

**Full Stack:**
```bash
# Start all services including monitoring
docker-compose up -d

# Start monitoring stack separately
docker-compose -f docker/docker-compose.monitoring.yml up -d
```

**Ports:**
- Prometheus: 9090
- Grafana: 3001
- Zipkin: 9411
- Elasticsearch: 9200
- Logstash: 5000, 9600
- Kibana: 5601
- Node Exporter: 9100
- cAdvisor: 8081
- Postgres Exporter: 9187

### Kubernetes

**Deploy Monitoring Namespace:**
```bash
kubectl apply -f kubernetes/monitoring/prometheus.yaml
kubectl apply -f kubernetes/monitoring/grafana.yaml
kubectl apply -f kubernetes/monitoring/zipkin.yaml
kubectl apply -f kubernetes/monitoring/elk-stack.yaml
```

**Service Annotations:**

Add to microservice deployments for Prometheus scraping:
```yaml
metadata:
  annotations:
    prometheus.io/scrape: "true"
    prometheus.io/port: "8080"
    prometheus.io/path: "/actuator/prometheus"
```

**Access Services:**
```bash
# Port forward Prometheus
kubectl port-forward -n monitoring svc/prometheus 9090:9090

# Port forward Grafana
kubectl port-forward -n monitoring svc/grafana 3000:3000

# Port forward Zipkin
kubectl port-forward -n monitoring svc/zipkin 9411:9411

# Port forward Kibana
kubectl port-forward -n monitoring svc/kibana 5601:5601
```

## Monitoring Queries

### Prometheus Queries

**Service Availability:**
```promql
up{job=~".*-service"}
```

**Request Rate:**
```promql
sum(rate(http_server_requests_seconds_count[5m])) by (service)
```

**P95 Response Time:**
```promql
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[5m])) by (service, le))
```

**Error Rate:**
```promql
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) by (service) 
/ sum(rate(http_server_requests_seconds_count[5m])) by (service) * 100
```

**JVM Heap Usage:**
```promql
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100
```

**Database Connection Pool:**
```promql
hikaricp_connections_active / hikaricp_connections_max * 100
```

### Kibana Queries

**Error Logs:**
```
level:error OR level:fatal
```

**Service-Specific Logs:**
```
service_name:"farmers-service" AND level:error
```

**Trace-Based Search:**
```
traceId:"abc123def456"
```

**Time Range Queries:**
```
@timestamp:[now-1h TO now] AND level:(error OR warn)
```

## Best Practices

### 1. Metric Naming

Follow Prometheus conventions:
- Use lowercase with underscores
- Include unit suffix (`_seconds`, `_bytes`, `_total`)
- Consistent label names across services

### 2. Logging

- Use structured logging (JSON format)
- Include correlation IDs (traceId/spanId)
- Log at appropriate levels:
  - DEBUG: Detailed diagnostic information
  - INFO: General informational messages
  - WARN: Warning messages for potential issues
  - ERROR: Error events that might still allow the application to continue

### 3. Alerting

- Define clear thresholds based on SLOs
- Implement alert escalation
- Include runbook links in alert annotations
- Avoid alert fatigue with proper grouping

### 4. Dashboard Design

- One dashboard per service or concern
- Use consistent color schemes
- Include both overview and detailed views
- Add annotations for deployments

### 5. Tracing Sampling

Adjust sampling rate based on environment:
- Development: 100% (`probability: 1.0`)
- Production: 10-20% (`probability: 0.1`)
- High traffic: 1-5% (`probability: 0.01`)

## Troubleshooting

### Services Not Appearing in Prometheus

1. Check service health: `curl http://service:port/actuator/health`
2. Verify metrics endpoint: `curl http://service:port/actuator/prometheus`
3. Check Prometheus targets: http://localhost:9090/targets
4. Verify network connectivity between Prometheus and services

### No Traces in Zipkin

1. Check Zipkin connectivity: `curl http://zipkin:9411/health`
2. Verify ZIPKIN_URL environment variable
3. Check trace sampling probability (must be > 0)
4. Review service logs for tracing errors

### Logs Not Appearing in Kibana

1. Check Logstash connectivity: `curl http://logstash:9600`
2. Verify Elasticsearch health: `curl http://elasticsearch:9200/_cluster/health`
3. Check log format (must be JSON)
4. Create index patterns in Kibana
5. Verify Logback configuration

### High Memory Usage

1. Check heap size configuration
2. Review GC metrics in Grafana
3. Generate heap dump: `curl http://service:port/actuator/heapdump`
4. Analyze with tools like Eclipse MAT

## Performance Tuning

### Prometheus

```yaml
# Increase retention
--storage.tsdb.retention.time=90d

# Adjust scrape interval
scrape_interval: 30s  # Less frequent scraping

# Enable remote write for long-term storage
remote_write:
  - url: http://remote-storage:9009/api/v1/push
```

### Elasticsearch

```yaml
# Increase heap size
ES_JAVA_OPTS: "-Xms2g -Xmx2g"

# Configure index lifecycle management
PUT _ilm/policy/logs_policy
{
  "policy": {
    "phases": {
      "hot": { "actions": { "rollover": { "max_size": "50GB", "max_age": "1d" }}},
      "delete": { "min_age": "30d", "actions": { "delete": {}}}
    }
  }
}
```

### Grafana

- Enable caching for dashboards
- Limit time range in queries
- Use query variables for filtering
- Implement dashboard folders and permissions

## Security Considerations

### Production Checklist

- [ ] Enable authentication for all monitoring tools
- [ ] Use HTTPS for external access
- [ ] Implement RBAC for Kubernetes monitoring services
- [ ] Secure Elasticsearch with X-Pack security
- [ ] Rotate credentials regularly
- [ ] Limit metric exposure via network policies
- [ ] Enable audit logging
- [ ] Implement rate limiting for monitoring APIs

### Recommended Settings

**Grafana:**
```yaml
GF_SECURITY_ADMIN_PASSWORD: ${GRAFANA_PASSWORD}
GF_AUTH_ANONYMOUS_ENABLED: false
GF_SECURITY_ALLOW_EMBEDDING: false
```

**Elasticsearch:**
```yaml
xpack.security.enabled: true
xpack.security.transport.ssl.enabled: true
```

**Prometheus:**
```yaml
--web.enable-admin-api=false
--web.enable-lifecycle=false  # In production
```

## Maintenance

### Regular Tasks

**Daily:**
- Review critical alerts
- Check service health dashboards
- Verify log ingestion rates

**Weekly:**
- Review disk usage for time-series data
- Analyze slow queries and optimize
- Update alert thresholds based on trends

**Monthly:**
- Clean up old indices in Elasticsearch
- Review and update dashboards
- Conduct capacity planning

### Backup Strategy

**Prometheus:**
```bash
# Snapshot prometheus data
docker exec prometheus tar czf /prometheus-backup.tar.gz /prometheus
```

**Elasticsearch:**
```bash
# Create snapshot repository
PUT _snapshot/backup_repo
{
  "type": "fs",
  "settings": {
    "location": "/backup"
  }
}

# Create snapshot
PUT _snapshot/backup_repo/snapshot_1
```

**Grafana:**
- Export dashboards as JSON
- Backup Grafana database (SQLite/PostgreSQL)
- Version control dashboard JSON files

## Resources

- [Spring Boot Actuator Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Prometheus Best Practices](https://prometheus.io/docs/practices/)
- [Grafana Dashboard Best Practices](https://grafana.com/docs/grafana/latest/best-practices/)
- [Zipkin Documentation](https://zipkin.io/pages/instrumenting.html)
- [ELK Stack Guide](https://www.elastic.co/guide/index.html)
- [Micrometer Documentation](https://micrometer.io/docs)

## Support

For issues or questions regarding monitoring and logging:
1. Check this documentation
2. Review service logs in Kibana
3. Check Prometheus alerts
4. Review Grafana dashboards for anomalies
5. Contact DevOps team
