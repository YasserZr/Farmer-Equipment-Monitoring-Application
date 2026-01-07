@echo off
REM Windows batch script for Kubernetes deployment

echo =============================================
echo Deploying Farm Monitoring to Kubernetes
echo =============================================

REM Check if kubectl is installed
kubectl version --client >nul 2>&1
if errorlevel 1 (
    echo Error: kubectl is not installed
    exit /b 1
)

REM Check if cluster is accessible
kubectl cluster-info >nul 2>&1
if errorlevel 1 (
    echo Error: Unable to connect to Kubernetes cluster
    exit /b 1
)

REM Parse command line arguments
set ACTION=apply
set DRY_RUN=false

:parse_args
if "%1"=="--delete" (
    set ACTION=delete
    shift
    goto parse_args
)
if "%1"=="--dry-run" (
    set DRY_RUN=true
    shift
    goto parse_args
)

if "%ACTION%"=="apply" goto deploy
if "%ACTION%"=="delete" goto delete_all

:deploy
echo.
echo Step 1: Creating namespace...
kubectl apply -f namespace.yaml

echo.
echo Step 2: Creating ConfigMaps and Secrets...
kubectl apply -f configmaps/application-config.yaml
kubectl apply -f secrets/database-secrets.yaml
kubectl apply -f secrets/rabbitmq-secrets.yaml

echo.
echo Step 3: Deploying Databases...
kubectl apply -f databases/farmers-db-statefulset.yaml
kubectl apply -f databases/equipment-db-statefulset.yaml
kubectl apply -f databases/supervision-db-statefulset.yaml

echo.
echo Waiting for databases to be ready...
kubectl wait --for=condition=ready pod -l app=farmers-db -n farm-monitoring --timeout=300s
kubectl wait --for=condition=ready pod -l app=equipment-db -n farm-monitoring --timeout=300s
kubectl wait --for=condition=ready pod -l app=supervision-db -n farm-monitoring --timeout=300s

echo.
echo Step 4: Deploying RabbitMQ...
kubectl apply -f rabbitmq/rabbitmq-deployment.yaml

echo.
echo Waiting for RabbitMQ to be ready...
kubectl wait --for=condition=ready pod -l app=rabbitmq -n farm-monitoring --timeout=300s

echo.
echo Step 5: Deploying Infrastructure Services...
kubectl apply -f eureka-server/deployment.yaml

echo.
echo Waiting for Eureka Server to be ready...
kubectl wait --for=condition=ready pod -l app=eureka-server -n farm-monitoring --timeout=300s

kubectl apply -f config-server/deployment.yaml

echo.
echo Waiting for Config Server to be ready...
kubectl wait --for=condition=ready pod -l app=config-server -n farm-monitoring --timeout=300s

echo.
echo Step 6: Deploying Business Services...
kubectl apply -f farmers-service/deployment.yaml
kubectl apply -f equipment-service/deployment.yaml
kubectl apply -f supervision-service/deployment.yaml

echo.
echo Step 7: Deploying API Gateway...
kubectl apply -f api-gateway/deployment.yaml

echo.
echo Step 8: Deploying Frontend...
kubectl apply -f frontend/deployment.yaml

echo.
echo Step 9: Configuring Ingress...
kubectl apply -f ingress/ingress.yaml

echo.
echo =============================================
echo Deployment Complete!
echo =============================================

echo.
echo Checking deployment status...
kubectl get pods -n farm-monitoring

echo.
echo Service URLs:
echo To access the application, configure DNS or use port-forward:
echo Frontend:       kubectl port-forward -n farm-monitoring svc/frontend 3000:3000
echo API Gateway:    kubectl port-forward -n farm-monitoring svc/api-gateway 8080:8080
echo Eureka Server:  kubectl port-forward -n farm-monitoring svc/eureka-server 8761:8761

echo.
echo Useful Commands:
echo View logs:      kubectl logs -f ^<pod-name^> -n farm-monitoring
echo Scale service:  kubectl scale deployment ^<name^> --replicas=3 -n farm-monitoring
echo View services:  kubectl get svc -n farm-monitoring
echo View ingress:   kubectl get ingress -n farm-monitoring

goto end

:delete_all
echo.
echo Deleting all resources...
kubectl delete namespace farm-monitoring
echo All resources deleted

:end
echo.
echo Done!
pause
