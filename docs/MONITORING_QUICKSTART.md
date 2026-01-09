# Monitoring Quick Start Guide

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose installed
- At least 8GB RAM available
- Ports available: 3001, 5601, 9090, 9411

### Start Full Stack with Monitoring

```bash
# Clone the repository
git clone <repository-url>
cd Farmer-Equipment-Monitoring-Application

# Start all services (application + monitoring)
docker-compose up -d
docker-compose -f docker/docker-compose.monitoring.yml up -d

# Wait for services to be ready (~2-3 minutes)
docker-compose ps

# Check service health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

## 📊 Access Monitoring Tools

| Tool | URL | Credentials | Purpose |
|------|-----|-------------|---------|
| **Grafana** | http://localhost:3001 | admin/admin | Dashboards and visualization |
| **Prometheus** | http://localhost:9090 | N/A | Metrics and alerts |
| **Zipkin** | http://localhost:9411 | N/A | Distributed tracing |
| **Kibana** | http://localhost:5601 | N/A | Log analysis |
| **Elasticsearch** | http://localhost:9200 | N/A | Log storage |

## 🎯 First Steps

### 1. View Service Dashboards in Grafana

1. Open http://localhost:3001
2. Login with `admin/admin`
3. Navigate to **Dashboards** → **Farm Equipment Monitoring**
4. Open **Microservices Overview** dashboard
5. You should see:
   - All services up and healthy (green)
   - Request rates
   - Response times
   - Memory usage

### 2. Check Prometheus Targets

1. Open http://localhost:9090
2. Navigate to **Status** → **Targets**
3. Verify all services are UP:
   - farmers-service (8081)
   - equipment-service (8082)
   - supervision-service (8083)
   - api-gateway (8080)

### 3. View Distributed Traces in Zipkin

1. Open http://localhost:9411
2. Click **Run Query** to see recent traces
3. Click on any trace to see:
   - Service call sequence
   - Duration of each service
   - Any errors

### 4. Search Logs in Kibana

1. Open http://localhost:5601
2. Go to **Management** → **Stack Management** → **Index Patterns**
3. Create index patterns:
   - `farmers-service-*`
   - `equipment-service-*`
   - `supervision-service-*`
4. Navigate to **Analytics** → **Discover**
5. Search for logs:
   ```
   level:error
   service_name:"farmers-service"
   ```

## 🔍 Common Use Cases

### Monitor Application Health

**Grafana Dashboard:**
- Navigate to **Microservices Overview**
- Check "Service Health Status" panel
- All values should be 1 (up)

**Prometheus Query:**
```promql
up{job=~".*-service"}
```

### Investigate Slow Requests

**Grafana:**
- Check "Response Time (P95)" panel
- Look for spikes or high values (>1s)

**Zipkin:**
- Filter traces with long duration
- Examine slow service calls

### Find Application Errors

**Kibana:**
```
level:error OR level:fatal
```

**Prometheus Alert:**
- Check **Alerts** page for firing alerts
- Look for "HighErrorRate" alert

### Track Equipment Failures

**Prometheus Query:**
```promql
equipment_failures_total
```

**Grafana:**
- View "Equipment Status Distribution" panel
- Monitor "FAULTY" status

### Monitor Database Performance

**Grafana:**
- Check "Database Connection Pool" panel
- Watch for pool exhaustion

**Prometheus Query:**
```promql
hikaricp_connections_active / hikaricp_connections_max * 100
```

## 🛠️ Troubleshooting

### Monitoring Services Not Starting

```bash
# Check Docker logs
docker-compose -f docker/docker-compose.monitoring.yml logs -f

# Check disk space
docker system df

# Restart monitoring stack
docker-compose -f docker/docker-compose.monitoring.yml down
docker-compose -f docker/docker-compose.monitoring.yml up -d
```

### Services Not Appearing in Prometheus

```bash
# Test metrics endpoint
curl http://localhost:8081/actuator/prometheus

# Check Prometheus config
docker exec prometheus cat /etc/prometheus/prometheus.yml

# Check Prometheus logs
docker logs prometheus
```

### No Logs in Kibana

```bash
# Check Logstash
docker logs logstash

# Check Elasticsearch
curl http://localhost:9200/_cat/health

# Verify log format
docker logs farmers-service | head -5

# Test Logstash input
echo '{"message":"test","service_name":"test"}' | nc localhost 5000
```

### Zipkin Not Showing Traces

```bash
# Check Zipkin health
curl http://localhost:9411/health

# Verify ZIPKIN_URL in service
docker exec farmers-service env | grep ZIPKIN

# Check sampling probability in application.yml
# Should be > 0 (e.g., 1.0 for 100%)
```

## 📈 Kubernetes Deployment

```bash
# Create monitoring namespace
kubectl create namespace monitoring

# Deploy monitoring stack
kubectl apply -f kubernetes/monitoring/prometheus.yaml
kubectl apply -f kubernetes/monitoring/grafana.yaml
kubectl apply -f kubernetes/monitoring/zipkin.yaml
kubectl apply -f kubernetes/monitoring/elk-stack.yaml

# Wait for pods to be ready
kubectl get pods -n monitoring -w

# Access services via port-forward
kubectl port-forward -n monitoring svc/grafana 3000:3000
kubectl port-forward -n monitoring svc/prometheus 9090:9090
kubectl port-forward -n monitoring svc/zipkin 9411:9411
kubectl port-forward -n monitoring svc/kibana 5601:5601
```

## 🎓 Learning Resources

### Grafana Dashboards
- Pre-built dashboards in `monitoring/grafana/dashboards/`
- **Microservices Overview**: High-level service metrics
- **JVM Metrics**: Detailed JVM performance

### Prometheus Queries
- Example queries in `docs/MONITORING.md`
- Query builder available at http://localhost:9090/graph

### Alert Rules
- Defined in `monitoring/prometheus/alerts/service-alerts.yml`
- View firing alerts at http://localhost:9090/alerts

### Log Patterns
- Structured JSON logging
- Trace correlation with traceId/spanId
- Profile-based configuration (dev vs prod)

## 📝 Next Steps

1. **Customize Dashboards**
   - Import additional Grafana dashboards
   - Create custom panels for business metrics
   - Set up dashboard variables

2. **Configure Alerts**
   - Adjust alert thresholds in `service-alerts.yml`
   - Set up Alertmanager for notifications
   - Create alert routing rules

3. **Optimize Performance**
   - Adjust scrape intervals
   - Configure log retention
   - Tune resource limits

4. **Enable Security**
   - Set strong passwords
   - Enable HTTPS
   - Configure authentication

5. **Production Readiness**
   - Review `docs/MONITORING.md`
   - Implement backup strategy
   - Set up monitoring for monitoring

## 🆘 Support

- Full documentation: `docs/MONITORING.md`
- Check service logs: `docker-compose logs <service-name>`
- Health endpoints: `http://localhost:<port>/actuator/health`
- Metrics endpoints: `http://localhost:<port>/actuator/prometheus`

## 📋 Checklist

After setup, verify:
- [ ] All services are UP in Prometheus targets
- [ ] Grafana dashboards display data
- [ ] Traces appear in Zipkin
- [ ] Logs are indexed in Elasticsearch
- [ ] Index patterns created in Kibana
- [ ] No alerts firing in Prometheus
- [ ] All Docker containers running
- [ ] Disk space sufficient (>10GB free)

## 🔗 Quick Links

- Application: http://localhost:3000
- API Gateway: http://localhost:8080
- Farmers Service: http://localhost:8081/swagger-ui.html
- Equipment Service: http://localhost:8082/swagger-ui.html
- Supervision Service: http://localhost:8083/swagger-ui.html
- RabbitMQ Management: http://localhost:15672
- Eureka Dashboard: http://localhost:8761

---

**Note**: Default credentials should be changed in production environments.
