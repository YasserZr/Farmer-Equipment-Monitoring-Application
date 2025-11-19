#!/bin/bash

# Deploy all Kubernetes resources

set -e

echo "Deploying Farmer Equipment Monitoring Application to Kubernetes..."

# Create namespace
echo "Creating namespace..."
kubectl create namespace farm-monitoring --dry-run=client -o yaml | kubectl apply -f -

# Apply ConfigMaps
echo "Applying ConfigMaps..."
kubectl apply -f kubernetes/configmaps/ -n farm-monitoring

# Apply Secrets (create from examples or use existing)
echo "Note: Create secrets manually using kubectl create secret"
echo "  kubectl create secret generic postgres-secret --from-literal=username=postgres --from-literal=password=postgres -n farm-monitoring"
echo "  kubectl create secret generic rabbitmq-secret --from-literal=username=guest --from-literal=password=guest -n farm-monitoring"

# Apply Deployments
echo "Applying Deployments..."
kubectl apply -f kubernetes/deployments/ -n farm-monitoring

# Apply Services
echo "Applying Services..."
kubectl apply -f kubernetes/services/ -n farm-monitoring

echo ""
echo "Deployment complete!"
echo ""
echo "Check status:"
echo "  kubectl get pods -n farm-monitoring"
echo "  kubectl get services -n farm-monitoring"
