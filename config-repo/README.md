# Configuration Repository README

This directory contains centralized configuration files for all microservices.

## Structure

- `application.yml` - Global configuration shared by all services
- `{service-name}.yml` - Service-specific configuration
- `{service-name}-{profile}.yml` - Environment-specific configuration (dev, prod)

## Services

### Farmers Service
- `farmers-service.yml` - Base configuration
- `farmers-service-dev.yml` - Development environment
- `farmers-service-prod.yml` - Production environment

### Equipment Service
- `equipment-service.yml` - Base configuration
- `equipment-service-dev.yml` - Development environment
- `equipment-service-prod.yml` - Production environment

### Supervision Service
- `supervision-service.yml` - Base configuration
- `supervision-service-dev.yml` - Development environment
- `supervision-service-prod.yml` - Production environment

## Encryption

Sensitive properties are encrypted using Spring Cloud Config encryption.

### To encrypt a value:
```bash
curl http://localhost:8888/encrypt -d "mySecretValue"
```

### To decrypt a value:
```bash
curl http://localhost:8888/decrypt -d "{cipher}ENCRYPTED_VALUE"
```

### Using encrypted values in config files:
```yaml
password: '{cipher}ENCRYPTED_VALUE_HERE'
```

## Usage

Services will automatically fetch configuration from Config Server on startup.

### Configuration priority (highest to lowest):
1. `{service-name}-{profile}.yml`
2. `{service-name}.yml`
3. `application-{profile}.yml`
4. `application.yml`

## Development Setup

1. Ensure Config Server is running on port 8888
2. Update `config-server/application.yml` to point to this directory
3. Services will automatically fetch their configuration on startup

## Production Setup

1. Create a Git repository for configurations
2. Update Config Server to use Git URI
3. Add authentication credentials
4. Enable encryption with proper keys
5. Use environment variables for sensitive data
