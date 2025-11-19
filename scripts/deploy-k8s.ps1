# Deploy all Kubernetes resources (PowerShell)

Write-Host "Deploying Farmer Equipment Monitoring Application to Kubernetes..." -ForegroundColor Green

# Create namespace
Write-Host "Creating namespace..." -ForegroundColor Cyan
kubectl create namespace farm-monitoring --dry-run=client -o yaml | kubectl apply -f -

# Apply ConfigMaps
Write-Host "Applying ConfigMaps..." -ForegroundColor Cyan
kubectl apply -f kubernetes/configmaps/ -n farm-monitoring

# Apply Secrets (create from examples or use existing)
Write-Host "Note: Create secrets manually using kubectl create secret" -ForegroundColor Yellow
Write-Host "  kubectl create secret generic postgres-secret --from-literal=username=postgres --from-literal=password=postgres -n farm-monitoring"
Write-Host "  kubectl create secret generic rabbitmq-secret --from-literal=username=guest --from-literal=password=guest -n farm-monitoring"

# Apply Deployments
Write-Host "Applying Deployments..." -ForegroundColor Cyan
kubectl apply -f kubernetes/deployments/ -n farm-monitoring

# Apply Services
Write-Host "Applying Services..." -ForegroundColor Cyan
kubectl apply -f kubernetes/services/ -n farm-monitoring

Write-Host "`nDeployment complete!" -ForegroundColor Green
Write-Host "`nCheck status:" -ForegroundColor Yellow
Write-Host "  kubectl get pods -n farm-monitoring"
Write-Host "  kubectl get services -n farm-monitoring"
