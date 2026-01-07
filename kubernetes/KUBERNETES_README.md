# Kubernetes Deployment Guide

This document provides comprehensive instructions for deploying the Farmer Equipment Monitoring Application to Kubernetes.

## Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Architecture](#architecture)
- [Quick Start](#quick-start)
- [Deployment Resources](#deployment-resources)
- [Configuration](#configuration)
- [Scaling](#scaling)
- [Monitoring](#monitoring)
- [Troubleshooting](#troubleshooting)
- [Production Considerations](#production-considerations)

## Overview

The application is deployed as a multi-tier microservices architecture on Kubernetes with:
- **3 StatefulSets** for PostgreSQL databases
- **7 Deployments** for backend services and frontend
- **1 Deployment** for RabbitMQ
- **HorizontalPodAutoscalers** for automatic scaling
- **Ingress** for external access
- **ConfigMaps** and **Secrets** for configuration management

## Prerequisites

### Required Tools

- **Kubernetes Cluster**: v1.25+ (Minikube, Kind, GKE, EKS, AKS, etc.)
- **kubectl**: v1.25+
- **Docker**: For building images
- **Helm** (optional): For managing releases

### Minimum Cluster Requirements

- **Nodes**: 3+ worker nodes
- **CPU**: 8+ cores total
- **Memory**: 16GB+ RAM total
- **Storage**: 50GB+ persistent storage

### Optional Components

- **NGINX Ingress Controller**: For external access
- **Cert-Manager**: For TLS certificate management
- **Metrics Server**: For HPA functionality
- **Prometheus/Grafana**: For monitoring

## Architecture

### Kubernetes Resources

```
farm-monitoring namespace
├── StatefulSets (3)
│   ├── farmers-db (1 replica)
│   ├── equipment-db (1 replica)
│   └── supervision-db (1 replica)
├── Deployments (8)
│   ├── eureka-server (2 replicas)
│   ├── config-server (2 replicas)
│   ├── api-gateway (3 replicas, HPA)
│   ├── farmers-service (2 replicas, HPA)
│   ├── equipment-service (2 replicas, HPA)
│   ├── supervision-service (2 replicas, HPA)
│   ├── frontend (3 replicas, HPA)
│   └── rabbitmq (1 replica)
├── Services (12)
│   ├── ClusterIP services for internal communication
│   └── Headless services for StatefulSets
├── ConfigMaps (1)
│   └── application-config
├── Secrets (2)
│   ├── database-secrets
│   └── rabbitmq-secrets
├── PersistentVolumeClaims (4)
│   ├── farmers-db-storage
│   ├── equipment-db-storage
│   ├── supervision-db-storage
│   └── rabbitmq-pvc
├── HorizontalPodAutoscalers (4)
│   ├── api-gateway-hpa (3-10 replicas)
│   ├── farmers-service-hpa (2-8 replicas)
│   ├── equipment-service-hpa (2-8 replicas)
│   ├── supervision-service-hpa (2-8 replicas)
│   └── frontend-hpa (3-10 replicas)
└── Ingress (2)
    ├── farm-monitoring-ingress (public)
    └── farm-monitoring-admin-ingress (admin)
```

### Resource Allocation

| Service | Replicas | CPU Request | CPU Limit | Memory Request | Memory Limit |
|---------|----------|-------------|-----------|----------------|--------------|
| Eureka Server | 2 | 500m | 1000m | 512Mi | 1Gi |
| Config Server | 2 | 500m | 1000m | 512Mi | 1Gi |
| API Gateway | 3-10 (HPA) | 500m | 1000m | 512Mi | 1Gi |
| Farmers Service | 2-8 (HPA) | 500m | 1000m | 512Mi | 1Gi |
| Equipment Service | 2-8 (HPA) | 500m | 1000m | 512Mi | 1Gi |
| Supervision Service | 2-8 (HPA) | 500m | 1000m | 512Mi | 1Gi |
| Frontend | 3-10 (HPA) | 250m | 500m | 256Mi | 512Mi |
| RabbitMQ | 1 | 250m | 500m | 256Mi | 512Mi |
| PostgreSQL (x3) | 1 each | 250m | 500m | 256Mi | 512Mi |

**Total Resources:**
- **CPU**: ~8-12 cores (with headroom for HPA)
- **Memory**: ~10-16GB
- **Storage**: ~35GB persistent volumes

## Quick Start

### 1. Install Prerequisites

**Install kubectl:**
```bash
# Linux
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl
sudo mv kubectl /usr/local/bin/

# macOS
brew install kubectl

# Windows
choco install kubernetes-cli
```

**Install Minikube (for local testing):**
```bash
# Linux
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube

# macOS
brew install minikube

# Windows
choco install minikube
```

### 2. Start Kubernetes Cluster

**For local development (Minikube):**
```bash
# Start cluster with sufficient resources
minikube start --cpus=4 --memory=8192 --disk-size=50g

# Enable Ingress
minikube addons enable ingress

# Enable Metrics Server for HPA
minikube addons enable metrics-server
```

**For cloud providers:**
```bash
# Google Kubernetes Engine (GKE)
gcloud container clusters create farm-monitoring \
  --num-nodes=3 \
  --machine-type=e2-standard-2 \
  --zone=us-central1-a

# Amazon Elastic Kubernetes Service (EKS)
eksctl create cluster --name farm-monitoring \
  --region us-west-2 \
  --nodegroup-name standard-workers \
  --node-type t3.medium \
  --nodes 3

# Azure Kubernetes Service (AKS)
az aks create --resource-group farm-monitoring-rg \
  --name farm-monitoring \
  --node-count 3 \
  --node-vm-size Standard_D2s_v3
```

### 3. Build and Push Docker Images

**Build images:**
```bash
# Navigate to project root
cd Farmer-Equipment-Monitoring-Application

# Build all images
./build.sh
```

**Push to registry (for cloud deployment):**
```bash
# Tag images for your registry
docker tag farm-monitoring/eureka-server:latest gcr.io/your-project/eureka-server:latest
docker tag farm-monitoring/config-server:latest gcr.io/your-project/config-server:latest
docker tag farm-monitoring/api-gateway:latest gcr.io/your-project/api-gateway:latest
docker tag farm-monitoring/farmers-service:latest gcr.io/your-project/farmers-service:latest
docker tag farm-monitoring/equipment-service:latest gcr.io/your-project/equipment-service:latest
docker tag farm-monitoring/supervision-service:latest gcr.io/your-project/supervision-service:latest
docker tag farm-monitoring/frontend:latest gcr.io/your-project/frontend:latest

# Push to registry
docker push gcr.io/your-project/eureka-server:latest
docker push gcr.io/your-project/config-server:latest
docker push gcr.io/your-project/api-gateway:latest
docker push gcr.io/your-project/farmers-service:latest
docker push gcr.io/your-project/equipment-service:latest
docker push gcr.io/your-project/supervision-service:latest
docker push gcr.io/your-project/frontend:latest
```

**For Minikube (load images directly):**
```bash
minikube image load farm-monitoring/eureka-server:latest
minikube image load farm-monitoring/config-server:latest
minikube image load farm-monitoring/api-gateway:latest
minikube image load farm-monitoring/farmers-service:latest
minikube image load farm-monitoring/equipment-service:latest
minikube image load farm-monitoring/supervision-service:latest
minikube image load farm-monitoring/frontend:latest
```

### 4. Deploy to Kubernetes

**Automated deployment (recommended):**

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

**Manual deployment:**
```bash
cd kubernetes

# Apply all manifests in order
kubectl apply -f namespace.yaml
kubectl apply -f configmaps/
kubectl apply -f secrets/
kubectl apply -f databases/
kubectl apply -f rabbitmq/
kubectl apply -f eureka-server/
kubectl apply -f config-server/
kubectl apply -f farmers-service/
kubectl apply -f equipment-service/
kubectl apply -f supervision-service/
kubectl apply -f api-gateway/
kubectl apply -f frontend/
kubectl apply -f ingress/
```

**Using Kustomize:**
```bash
cd kubernetes
kubectl apply -k .
```

### 5. Verify Deployment

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

### 6. Access the Application

**For Minikube:**
```bash
# Get Minikube IP
minikube ip

# Add to /etc/hosts
echo "$(minikube ip) farm-monitoring.example.com api.farm-monitoring.example.com" | sudo tee -a /etc/hosts

# Or use port-forward
kubectl port-forward -n farm-monitoring svc/frontend 3000:3000
kubectl port-forward -n farm-monitoring svc/api-gateway 8080:8080
```

**Access URLs:**
- **Frontend**: http://localhost:3000
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761 (via port-forward)

## Deployment Resources

### Namespace

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: farm-monitoring
  labels:
    name: farm-monitoring
    environment: production
```

### ConfigMap

All non-sensitive configuration is stored in `application-config` ConfigMap:

- Service URLs (Eureka, Config Server, API Gateway)
- Database connection details
- RabbitMQ configuration
- Spring profiles

### Secrets

Sensitive data is stored in Kubernetes Secrets:

**database-secrets:**
- PostgreSQL credentials
- Database connection strings

**rabbitmq-secrets:**
- RabbitMQ admin credentials

**Update secrets for production:**
```bash
# Edit secrets
kubectl edit secret database-secrets -n farm-monitoring
kubectl edit secret rabbitmq-secrets -n farm-monitoring

# Or replace with base64 encoded values
echo -n "your-secure-password" | base64
# Update the YAML file and reapply
```

### StatefulSets

PostgreSQL databases use StatefulSets for:
- Stable network identities
- Persistent storage
- Ordered deployment and scaling

**Features:**
- Headless service for stable DNS
- PersistentVolumeClaim templates
- Liveness and readiness probes

### Deployments

All microservices use Deployments with:
- Multiple replicas for high availability
- Rolling update strategy
- Resource limits and requests
- Health probes
- Environment variable injection

### Services

**ClusterIP Services:**
- Internal communication between pods
- Load balancing across replicas
- DNS-based service discovery

**Headless Services:**
- Direct pod-to-pod communication
- Used for StatefulSets

### HorizontalPodAutoscalers

Automatic scaling based on:
- CPU utilization (70% threshold)
- Memory utilization (80% threshold)

**Scaling behavior:**
- Scale up: Fast (100% per 30s, max 2 pods per 30s)
- Scale down: Gradual (50% per 60s, 5-minute stabilization)

### Ingress

**Public Ingress:**
- Frontend: `farm-monitoring.example.com`
- API Gateway: `api.farm-monitoring.example.com`
- TLS/SSL enabled
- Automatic HTTPS redirect

**Admin Ingress:**
- Eureka Dashboard: `admin.farm-monitoring.example.com/eureka`
- RabbitMQ Management: `admin.farm-monitoring.example.com/rabbitmq`
- Basic authentication required

## Configuration

### Update ConfigMap

```bash
# Edit ConfigMap
kubectl edit configmap application-config -n farm-monitoring

# Or update the YAML file and reapply
kubectl apply -f configmaps/application-config.yaml
```

**Important:** Pods need to be restarted to pick up ConfigMap changes:
```bash
kubectl rollout restart deployment farmers-service -n farm-monitoring
```

### Update Secrets

```bash
# Create new password
NEW_PASSWORD=$(openssl rand -base64 32)

# Update secret
kubectl patch secret database-secrets -n farm-monitoring \
  -p "{\"data\":{\"POSTGRES_PASSWORD\":\"$(echo -n $NEW_PASSWORD | base64)\"}}"

# Restart pods
kubectl rollout restart statefulset farmers-db -n farm-monitoring
```

### Environment-Specific Configuration

Create overlays for different environments:

```bash
kubernetes/
├── base/
│   └── (current manifests)
└── overlays/
    ├── dev/
    │   └── kustomization.yaml
    ├── staging/
    │   └── kustomization.yaml
    └── production/
        └── kustomization.yaml
```

## Scaling

### Manual Scaling

```bash
# Scale deployment
kubectl scale deployment farmers-service --replicas=5 -n farm-monitoring

# Scale StatefulSet
kubectl scale statefulset farmers-db --replicas=3 -n farm-monitoring
```

### HorizontalPodAutoscaler

HPA automatically scales based on metrics:

```bash
# Check HPA status
kubectl get hpa -n farm-monitoring

# Describe HPA
kubectl describe hpa api-gateway-hpa -n farm-monitoring

# Update HPA
kubectl edit hpa api-gateway-hpa -n farm-monitoring
```

### Resource Limits

Adjust resource limits based on load:

```yaml
resources:
  requests:
    memory: "1Gi"
    cpu: "1000m"
  limits:
    memory: "2Gi"
    cpu: "2000m"
```

## Monitoring

### Pod Status

```bash
# Watch pods
kubectl get pods -n farm-monitoring -w

# Describe pod
kubectl describe pod <pod-name> -n farm-monitoring

# View logs
kubectl logs -f <pod-name> -n farm-monitoring

# View logs for all replicas
kubectl logs -f -l app=farmers-service -n farm-monitoring
```

### Resource Usage

```bash
# Top pods by CPU/Memory
kubectl top pods -n farm-monitoring

# Top nodes
kubectl top nodes
```

### Events

```bash
# View events
kubectl get events -n farm-monitoring --sort-by='.lastTimestamp'
```

### Metrics Server

```bash
# Install Metrics Server (if not installed)
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
```

### Prometheus and Grafana

Install monitoring stack:

```bash
# Add Prometheus Helm repo
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

# Install Prometheus and Grafana
helm install monitoring prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --create-namespace

# Access Grafana
kubectl port-forward -n monitoring svc/monitoring-grafana 3001:80
```

## Troubleshooting

### Pod Not Starting

```bash
# Check pod status
kubectl get pod <pod-name> -n farm-monitoring

# Describe pod for events
kubectl describe pod <pod-name> -n farm-monitoring

# Check logs
kubectl logs <pod-name> -n farm-monitoring
kubectl logs <pod-name> -n farm-monitoring --previous
```

### ImagePullBackOff

```bash
# Check image name and tag
kubectl describe pod <pod-name> -n farm-monitoring | grep Image

# For Minikube, ensure images are loaded
minikube image ls | grep farm-monitoring

# Check ImagePullPolicy
kubectl get deployment <deployment-name> -n farm-monitoring -o yaml | grep imagePullPolicy
```

### CrashLoopBackOff

```bash
# Check logs
kubectl logs <pod-name> -n farm-monitoring

# Check liveness/readiness probes
kubectl describe pod <pod-name> -n farm-monitoring | grep -A 10 Liveness
kubectl describe pod <pod-name> -n farm-monitoring | grep -A 10 Readiness

# Increase initial delay
kubectl edit deployment <deployment-name> -n farm-monitoring
# Update initialDelaySeconds
```

### Database Connection Issues

```bash
# Check database pod
kubectl get pod -l app=farmers-db -n farm-monitoring

# Test database connection
kubectl exec -it <farmers-service-pod> -n farm-monitoring -- \
  sh -c 'apt-get update && apt-get install -y postgresql-client && \
  psql -h farmers-db -U postgres -d farmers_db'

# Check service DNS
kubectl exec -it <farmers-service-pod> -n farm-monitoring -- nslookup farmers-db
```

### Service Not Accessible

```bash
# Check service
kubectl get svc -n farm-monitoring

# Check endpoints
kubectl get endpoints -n farm-monitoring

# Test service internally
kubectl run curl --image=curlimages/curl -it --rm -n farm-monitoring -- \
  curl http://farmers-service:8081/actuator/health
```

### Ingress Not Working

```bash
# Check Ingress Controller
kubectl get pods -n ingress-nginx

# Check Ingress
kubectl get ingress -n farm-monitoring
kubectl describe ingress farm-monitoring-ingress -n farm-monitoring

# Check Ingress Controller logs
kubectl logs -n ingress-nginx <ingress-controller-pod>
```

### HPA Not Scaling

```bash
# Check Metrics Server
kubectl get deployment metrics-server -n kube-system

# Check HPA
kubectl get hpa -n farm-monitoring
kubectl describe hpa <hpa-name> -n farm-monitoring

# Manually trigger scaling by generating load
kubectl run -it --rm load-generator --image=busybox /bin/sh
# wget -q -O- http://api-gateway:8080/farmers
```

## Production Considerations

### High Availability

**Database Replication:**
```yaml
# Use StatefulSet with replicas > 1
spec:
  replicas: 3  # Master + 2 replicas
```

**Multi-Zone Deployment:**
```yaml
affinity:
  podAntiAffinity:
    preferredDuringSchedulingIgnoredDuringExecution:
    - weight: 100
      podAffinityTerm:
        labelSelector:
          matchExpressions:
          - key: app
            operator: In
            values:
            - farmers-service
        topologyKey: topology.kubernetes.io/zone
```

### Security

**Network Policies:**
```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-from-ingress
  namespace: farm-monitoring
spec:
  podSelector:
    matchLabels:
      app: api-gateway
  policyTypes:
  - Ingress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          name: ingress-nginx
```

**Pod Security Standards:**
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: farm-monitoring
  labels:
    pod-security.kubernetes.io/enforce: restricted
```

**RBAC:**
```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: pod-reader
  namespace: farm-monitoring
rules:
- apiGroups: [""]
  resources: ["pods"]
  verbs: ["get", "list"]
```

### Backup Strategy

**Database Backups:**
```bash
# Create CronJob for backups
kubectl apply -f - <<EOF
apiVersion: batch/v1
kind: CronJob
metadata:
  name: farmers-db-backup
  namespace: farm-monitoring
spec:
  schedule: "0 2 * * *"  # Daily at 2 AM
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: backup
            image: postgres:15-alpine
            command:
            - sh
            - -c
            - pg_dump -h farmers-db -U postgres farmers_db > /backup/farmers_db_\$(date +%Y%m%d).sql
            env:
            - name: PGPASSWORD
              valueFrom:
                secretKeyRef:
                  name: database-secrets
                  key: POSTGRES_PASSWORD
            volumeMounts:
            - name: backup
              mountPath: /backup
          restartPolicy: OnFailure
          volumes:
          - name: backup
            persistentVolumeClaim:
              claimName: backup-pvc
EOF
```

### Disaster Recovery

**Backup PersistentVolumes:**
```bash
# Use VolumeSnapshot (CSI driver required)
kubectl apply -f - <<EOF
apiVersion: snapshot.storage.k8s.io/v1
kind: VolumeSnapshot
metadata:
  name: farmers-db-snapshot
  namespace: farm-monitoring
spec:
  volumeSnapshotClassName: csi-snapshot-class
  source:
    persistentVolumeClaimName: postgres-storage-farmers-db-0
EOF
```

### Cost Optimization

**Resource Right-Sizing:**
- Monitor actual resource usage
- Adjust requests and limits
- Use Vertical Pod Autoscaler

**Spot Instances:**
```yaml
# Node affinity for spot instances
affinity:
  nodeAffinity:
    preferredDuringSchedulingIgnoredDuringExecution:
    - weight: 100
      preference:
        matchExpressions:
        - key: cloud.google.com/gke-preemptible
          operator: Exists
```

### Observability

**Distributed Tracing:**
- Integrate OpenTelemetry
- Use Jaeger or Zipkin

**Logging:**
```bash
# EFK Stack (Elasticsearch, Fluentd, Kibana)
helm install efk elastic/eck-stack --namespace logging --create-namespace

# Loki Stack
helm install loki grafana/loki-stack --namespace logging --create-namespace
```

## Additional Resources

- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [kubectl Cheat Sheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)
- [Kustomize Documentation](https://kustomize.io/)
- [Helm Documentation](https://helm.sh/docs/)
- [Spring Boot on Kubernetes](https://spring.io/guides/gs/spring-boot-kubernetes/)

## Support

For issues or questions:
- GitHub Issues: [Create an issue](https://github.com/your-repo/issues)
- Documentation: [Main README](../README.md)
- Docker Guide: [DOCKER_README.md](../DOCKER_README.md)
