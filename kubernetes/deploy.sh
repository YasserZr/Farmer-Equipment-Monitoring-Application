#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${GREEN}=============================================${NC}"
echo -e "${GREEN}Deploying Farm Monitoring to Kubernetes${NC}"
echo -e "${GREEN}=============================================${NC}"

# Check if kubectl is installed
if ! command -v kubectl &> /dev/null; then
    echo -e "${RED}Error: kubectl is not installed${NC}"
    exit 1
fi

# Check if cluster is accessible
if ! kubectl cluster-info &> /dev/null; then
    echo -e "${RED}Error: Unable to connect to Kubernetes cluster${NC}"
    exit 1
fi

# Parse command line arguments
ACTION="apply"
DRY_RUN=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --delete)
            ACTION="delete"
            shift
            ;;
        --dry-run)
            DRY_RUN=true
            shift
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            echo "Usage: ./deploy.sh [--delete] [--dry-run]"
            exit 1
            ;;
    esac
done

# Function to apply or delete resources
deploy_resources() {
    local resource=$1
    local name=$2
    
    echo -e "\n${YELLOW}${ACTION^}ing $name...${NC}"
    
    if [ "$DRY_RUN" = true ]; then
        kubectl $ACTION -f $resource --dry-run=client
    else
        kubectl $ACTION -f $resource
    fi
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ $name ${ACTION}ed successfully${NC}"
    else
        echo -e "${RED}✗ Failed to $ACTION $name${NC}"
        return 1
    fi
}

if [ "$ACTION" = "apply" ]; then
    echo -e "\n${BLUE}Step 1: Creating namespace...${NC}"
    deploy_resources "namespace.yaml" "Namespace"
    
    echo -e "\n${BLUE}Step 2: Creating ConfigMaps and Secrets...${NC}"
    deploy_resources "configmaps/application-config.yaml" "Application ConfigMap"
    deploy_resources "secrets/database-secrets.yaml" "Database Secrets"
    deploy_resources "secrets/rabbitmq-secrets.yaml" "RabbitMQ Secrets"
    
    echo -e "\n${BLUE}Step 3: Deploying Databases...${NC}"
    deploy_resources "databases/farmers-db-statefulset.yaml" "Farmers Database"
    deploy_resources "databases/equipment-db-statefulset.yaml" "Equipment Database"
    deploy_resources "databases/supervision-db-statefulset.yaml" "Supervision Database"
    
    echo -e "\n${YELLOW}Waiting for databases to be ready...${NC}"
    if [ "$DRY_RUN" = false ]; then
        kubectl wait --for=condition=ready pod -l app=farmers-db -n farm-monitoring --timeout=300s
        kubectl wait --for=condition=ready pod -l app=equipment-db -n farm-monitoring --timeout=300s
        kubectl wait --for=condition=ready pod -l app=supervision-db -n farm-monitoring --timeout=300s
    fi
    
    echo -e "\n${BLUE}Step 4: Deploying RabbitMQ...${NC}"
    deploy_resources "rabbitmq/rabbitmq-deployment.yaml" "RabbitMQ"
    
    echo -e "\n${YELLOW}Waiting for RabbitMQ to be ready...${NC}"
    if [ "$DRY_RUN" = false ]; then
        kubectl wait --for=condition=ready pod -l app=rabbitmq -n farm-monitoring --timeout=300s
    fi
    
    echo -e "\n${BLUE}Step 5: Deploying Infrastructure Services...${NC}"
    deploy_resources "eureka-server/deployment.yaml" "Eureka Server"
    
    echo -e "\n${YELLOW}Waiting for Eureka Server to be ready...${NC}"
    if [ "$DRY_RUN" = false ]; then
        kubectl wait --for=condition=ready pod -l app=eureka-server -n farm-monitoring --timeout=300s
    fi
    
    deploy_resources "config-server/deployment.yaml" "Config Server"
    
    echo -e "\n${YELLOW}Waiting for Config Server to be ready...${NC}"
    if [ "$DRY_RUN" = false ]; then
        kubectl wait --for=condition=ready pod -l app=config-server -n farm-monitoring --timeout=300s
    fi
    
    echo -e "\n${BLUE}Step 6: Deploying Business Services...${NC}"
    deploy_resources "farmers-service/deployment.yaml" "Farmers Service"
    deploy_resources "equipment-service/deployment.yaml" "Equipment Service"
    deploy_resources "supervision-service/deployment.yaml" "Supervision Service"
    
    echo -e "\n${BLUE}Step 7: Deploying API Gateway...${NC}"
    deploy_resources "api-gateway/deployment.yaml" "API Gateway"
    
    echo -e "\n${BLUE}Step 8: Deploying Frontend...${NC}"
    deploy_resources "frontend/deployment.yaml" "Frontend"
    
    echo -e "\n${BLUE}Step 9: Configuring Ingress...${NC}"
    deploy_resources "ingress/ingress.yaml" "Ingress"
    
    echo -e "\n${GREEN}=============================================${NC}"
    echo -e "${GREEN}Deployment Complete!${NC}"
    echo -e "${GREEN}=============================================${NC}"
    
    if [ "$DRY_RUN" = false ]; then
        echo -e "\n${BLUE}Checking deployment status...${NC}"
        kubectl get pods -n farm-monitoring
        
        echo -e "\n${BLUE}Service URLs:${NC}"
        echo -e "${YELLOW}To access the application, configure DNS or use port-forward:${NC}"
        echo -e "Frontend:       ${GREEN}kubectl port-forward -n farm-monitoring svc/frontend 3000:3000${NC}"
        echo -e "API Gateway:    ${GREEN}kubectl port-forward -n farm-monitoring svc/api-gateway 8080:8080${NC}"
        echo -e "Eureka Server:  ${GREEN}kubectl port-forward -n farm-monitoring svc/eureka-server 8761:8761${NC}"
        
        echo -e "\n${BLUE}Useful Commands:${NC}"
        echo -e "${YELLOW}View logs:${NC}      kubectl logs -f <pod-name> -n farm-monitoring"
        echo -e "${YELLOW}Scale service:${NC}  kubectl scale deployment <name> --replicas=3 -n farm-monitoring"
        echo -e "${YELLOW}View services:${NC}  kubectl get svc -n farm-monitoring"
        echo -e "${YELLOW}View ingress:${NC}   kubectl get ingress -n farm-monitoring"
    fi
    
else
    echo -e "\n${RED}Deleting all resources...${NC}"
    
    if [ "$DRY_RUN" = false ]; then
        kubectl delete namespace farm-monitoring
        echo -e "${GREEN}All resources deleted${NC}"
    else
        echo -e "${YELLOW}Dry run: would delete namespace farm-monitoring${NC}"
    fi
fi

echo -e "\n${GREEN}Done!${NC}"
