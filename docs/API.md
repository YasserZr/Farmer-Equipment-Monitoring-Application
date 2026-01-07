# Farm Equipment Monitoring API Documentation

## Table of Contents

- [Overview](#overview)
- [Authentication](#authentication)
- [Base URLs](#base-urls)
- [Common Response Codes](#common-response-codes)
- [Error Handling](#error-handling)
- [Pagination](#pagination)
- [Rate Limiting](#rate-limiting)
- [Farmers Service API](#farmers-service-api)
  - [Farmer Endpoints](#farmer-endpoints)
  - [Farm Endpoints](#farm-endpoints)
- [Equipment Service API](#equipment-service-api)
  - [Pump Endpoints](#pump-endpoints)
  - [Sensor Endpoints](#sensor-endpoints)
- [Supervision Service API](#supervision-service-api)
  - [Event Endpoints](#event-endpoints)
  - [Statistics Endpoints](#statistics-endpoints)
- [Sample Workflows](#sample-workflows)
- [OpenAPI Specifications](#openapi-specifications)
- [Postman Collection](#postman-collection)

---

## Overview

The Farm Equipment Monitoring Application provides a comprehensive REST API for managing farms, farmers, equipment, and monitoring events. The API is built on a microservices architecture with the following services:

- **Farmers Service** (Port 8081): Farmer and farm management
- **Equipment Service** (Port 8082): Connected equipment (pumps, sensors) management
- **Supervision Service** (Port 8083): Equipment event monitoring and statistics
- **API Gateway** (Port 8080): Unified entry point for all services

All services support JSON request/response format and use standard HTTP methods (GET, POST, PUT, DELETE).

---

## Authentication

All API endpoints require JWT Bearer token authentication. Tokens must be included in the `Authorization` header:

```http
Authorization: Bearer <your-jwt-token>
```

### Obtaining a Token

Authentication tokens are typically obtained from an authentication service (not covered in this documentation). Once obtained, the token should be included in all subsequent API requests.

### Token Expiration

JWT tokens expire after a configured period (typically 24 hours). When a token expires, you'll receive a `401 Unauthorized` response and must obtain a new token.

---

## Base URLs

### Development Environment

- **API Gateway**: `http://localhost:8080`
- **Farmers Service**: `http://localhost:8081` (direct access)
- **Equipment Service**: `http://localhost:8082` (direct access)
- **Supervision Service**: `http://localhost:8083` (direct access)

### Production Environment

- **API Gateway**: `https://api.farmmonitoring.com`

**Recommendation**: Always use the API Gateway endpoint for production deployments to leverage routing, load balancing, and centralized authentication.

---

## Common Response Codes

| Status Code | Description |
|-------------|-------------|
| **200 OK** | Request succeeded |
| **201 Created** | Resource created successfully |
| **204 No Content** | Request succeeded with no response body (typically for DELETE) |
| **400 Bad Request** | Invalid request data or validation failed |
| **401 Unauthorized** | Missing or invalid authentication token |
| **403 Forbidden** | Authenticated but lacking required permissions |
| **404 Not Found** | Requested resource does not exist |
| **409 Conflict** | Resource already exists (e.g., duplicate email) |
| **500 Internal Server Error** | Unexpected server error |

---

## Error Handling

All error responses follow a consistent format:

```json
{
  "timestamp": "2024-01-22T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field 'email' - must be a valid email address",
  "path": "/api/farmers"
}
```

### Error Response Fields

- **timestamp**: ISO 8601 timestamp when the error occurred
- **status**: HTTP status code
- **error**: Human-readable error type
- **message**: Detailed error message
- **path**: API endpoint where the error occurred

---

## Pagination

List endpoints support pagination using query parameters:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | integer | 0 | Page number (0-indexed) |
| `size` | integer | 20 | Items per page (max 100) |
| `sort` | string | varies | Sort field and direction (e.g., `name,asc`) |

### Paginated Response Format

```json
{
  "content": [
    { /* item 1 */ },
    { /* item 2 */ }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 42,
  "totalPages": 3,
  "last": false,
  "first": true,
  "numberOfElements": 20,
  "size": 20,
  "number": 0
}
```

---

## Rate Limiting

API requests are subject to rate limiting to ensure service stability:

- **Standard Rate**: 100 requests per minute per IP address
- **Burst Rate**: 200 requests per minute (temporary)

When rate limit is exceeded, you'll receive a `429 Too Many Requests` response with a `Retry-After` header indicating when to retry.

---

## Farmers Service API

Base path: `/api/farmers` and `/api/farms`

### Farmer Endpoints

#### Create Farmer

Create a new farmer in the system.

**Endpoint**: `POST /api/farmers`

**Request Body**:
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0123",
  "role": "OWNER",
  "region": "North Region"
}
```

**Request Fields**:
- `name` (required): Farmer's full name (2-100 characters)
- `email` (required): Valid email address (must be unique)
- `phone` (optional): Phone number in E.164 format
- `role` (required): One of: `OWNER`, `MANAGER`, `WORKER`, `GUEST`
- `region` (optional): Geographic region (max 100 characters)

**Response** (201 Created):
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0123",
  "role": "OWNER",
  "region": "North Region",
  "registrationDate": "2024-01-15T10:30:00Z"
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/farmers \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "+1-555-0123",
    "role": "OWNER",
    "region": "North Region"
  }'
```

**Possible Errors**:
- `400 Bad Request`: Invalid email format or validation failed
- `401 Unauthorized`: Missing or invalid JWT token
- `409 Conflict`: Email already exists

---

#### Get All Farmers

Retrieve all farmers with pagination.

**Endpoint**: `GET /api/farmers`

**Query Parameters**:
- `page` (optional): Page number (default: 0)
- `size` (optional): Items per page (default: 20)
- `sort` (optional): Sort field (default: name)

**Response** (200 OK):
```json
{
  "content": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "John Doe",
      "email": "john.doe@example.com",
      "phone": "+1-555-0123",
      "role": "OWNER",
      "region": "North Region",
      "registrationDate": "2024-01-15T10:30:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 42,
  "totalPages": 3
}
```

**cURL Example**:
```bash
curl -X GET "http://localhost:8080/api/farmers?page=0&size=20&sort=name" \
  -H "Authorization: Bearer <token>"
```

---

#### Get Farmer by ID

Retrieve a specific farmer by their unique identifier.

**Endpoint**: `GET /api/farmers/{id}`

**Path Parameters**:
- `id` (required): Farmer UUID

**Response** (200 OK):
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0123",
  "role": "OWNER",
  "region": "North Region",
  "registrationDate": "2024-01-15T10:30:00Z"
}
```

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/farmers/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>"
```

**Possible Errors**:
- `404 Not Found`: Farmer with specified ID does not exist

---

#### Get Farmer by Email

Retrieve a farmer by their email address.

**Endpoint**: `GET /api/farmers/email/{email}`

**Path Parameters**:
- `email` (required): Farmer's email address

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/farmers/email/john.doe@example.com \
  -H "Authorization: Bearer <token>"
```

---

#### Search Farmers

Search farmers by name (case-insensitive).

**Endpoint**: `GET /api/farmers/search`

**Query Parameters**:
- `name` (required): Search term for farmer name
- `page`, `size` (optional): Pagination parameters

**cURL Example**:
```bash
curl -X GET "http://localhost:8080/api/farmers/search?name=John" \
  -H "Authorization: Bearer <token>"
```

---

#### Get Farmers by Role

Retrieve all farmers with a specific role.

**Endpoint**: `GET /api/farmers/role/{role}`

**Path Parameters**:
- `role` (required): One of `OWNER`, `MANAGER`, `WORKER`, `GUEST`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/farmers/role/OWNER \
  -H "Authorization: Bearer <token>"
```

---

#### Get Admin Farmers

Retrieve all farmers with admin privileges (OWNER or MANAGER).

**Endpoint**: `GET /api/farmers/admins`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/farmers/admins \
  -H "Authorization: Bearer <token>"
```

---

#### Get Recent Farmers

Retrieve farmers registered within the specified number of days.

**Endpoint**: `GET /api/farmers/recent`

**Query Parameters**:
- `days` (optional): Number of days to look back (default: 30)

**cURL Example**:
```bash
curl -X GET "http://localhost:8080/api/farmers/recent?days=30" \
  -H "Authorization: Bearer <token>"
```

---

#### Update Farmer

Update farmer information.

**Endpoint**: `PUT /api/farmers/{id}`

**Path Parameters**:
- `id` (required): Farmer UUID

**Request Body**:
```json
{
  "name": "John Doe Updated",
  "email": "john.doe@example.com",
  "phone": "+1-555-9999",
  "role": "MANAGER",
  "region": "East Region"
}
```

**Response** (200 OK): Updated farmer object

**cURL Example**:
```bash
curl -X PUT http://localhost:8080/api/farmers/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe Updated",
    "email": "john.doe@example.com",
    "phone": "+1-555-9999",
    "role": "MANAGER",
    "region": "East Region"
  }'
```

**Possible Errors**:
- `404 Not Found`: Farmer does not exist
- `409 Conflict`: Email already exists for another farmer

---

#### Delete Farmer

Delete a farmer from the system.

**Endpoint**: `DELETE /api/farmers/{id}`

**Path Parameters**:
- `id` (required): Farmer UUID

**Response** (204 No Content)

**cURL Example**:
```bash
curl -X DELETE http://localhost:8080/api/farmers/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>"
```

---

#### Check Farmer Exists

Check if a farmer with the given ID exists.

**Endpoint**: `GET /api/farmers/{id}/exists`

**Response** (200 OK):
```json
true
```

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/farmers/123e4567-e89b-12d3-a456-426614174000/exists \
  -H "Authorization: Bearer <token>"
```

---

#### Check Email Exists

Check if a farmer with the given email exists.

**Endpoint**: `GET /api/farmers/email/{email}/exists`

**Response** (200 OK):
```json
false
```

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/farmers/email/john.doe@example.com/exists \
  -H "Authorization: Bearer <token>"
```

---

### Farm Endpoints

#### Create Farm

Create a new farm for a farmer.

**Endpoint**: `POST /api/farms`

**Request Body**:
```json
{
  "name": "Green Valley Farm",
  "location": "North Region, Sector 5",
  "area": 150.5,
  "farmerId": "123e4567-e89b-12d3-a456-426614174000"
}
```

**Request Fields**:
- `name` (required): Farm name (2-100 characters)
- `location` (required): Physical location (5-255 characters)
- `area` (required): Farm area in hectares (minimum 0.01)
- `farmerId` (required): UUID of the owning farmer

**Response** (201 Created):
```json
{
  "id": "223e4567-e89b-12d3-a456-426614174000",
  "name": "Green Valley Farm",
  "location": "North Region, Sector 5",
  "area": 150.5,
  "farmerId": "123e4567-e89b-12d3-a456-426614174000",
  "farmerName": "John Doe",
  "createdAt": "2024-01-20T09:00:00Z",
  "updatedAt": "2024-01-20T09:00:00Z"
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/farms \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Green Valley Farm",
    "location": "North Region, Sector 5",
    "area": 150.5,
    "farmerId": "123e4567-e89b-12d3-a456-426614174000"
  }'
```

**Possible Errors**:
- `404 Not Found`: Farmer with specified ID does not exist
- `409 Conflict`: Farm name already exists for this farmer

---

#### Get All Farms

Retrieve all farms with pagination.

**Endpoint**: `GET /api/farms`

**Query Parameters**:
- `page`, `size`, `sort` (optional): Pagination parameters

**cURL Example**:
```bash
curl -X GET "http://localhost:8080/api/farms?page=0&size=20" \
  -H "Authorization: Bearer <token>"
```

---

#### Get Farm by ID

Retrieve a specific farm by its unique identifier.

**Endpoint**: `GET /api/farms/{id}`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/farms/223e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>"
```

---

#### Get Farms by Farmer

Retrieve all farms belonging to a specific farmer.

**Endpoint**: `GET /api/farms/farmer/{farmerId}`

**Path Parameters**:
- `farmerId` (required): Farmer UUID

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/farms/farmer/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>"
```

---

#### Search Farms

Search farms by name or location (case-insensitive).

**Endpoint**: `GET /api/farms/search`

**Query Parameters**:
- `searchTerm` (required): Search term

**cURL Example**:
```bash
curl -X GET "http://localhost:8080/api/farms/search?searchTerm=Valley" \
  -H "Authorization: Bearer <token>"
```

---

#### Get Farms by Area Range

Retrieve farms within a specified area range (in hectares).

**Endpoint**: `GET /api/farms/area-range`

**Query Parameters**:
- `minArea` (required): Minimum area
- `maxArea` (required): Maximum area

**cURL Example**:
```bash
curl -X GET "http://localhost:8080/api/farms/area-range?minArea=50&maxArea=200" \
  -H "Authorization: Bearer <token>"
```

---

#### Get Large Farms

Retrieve all large farms (area >= 100 hectares).

**Endpoint**: `GET /api/farms/large`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/farms/large \
  -H "Authorization: Bearer <token>"
```

---

#### Update Farm

Update farm information.

**Endpoint**: `PUT /api/farms/{id}`

**Request Body**:
```json
{
  "name": "Green Valley Farm Updated",
  "location": "North Region, Sector 5A",
  "area": 175.0
}
```

**cURL Example**:
```bash
curl -X PUT http://localhost:8080/api/farms/223e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Green Valley Farm Updated",
    "location": "North Region, Sector 5A",
    "area": 175.0
  }'
```

---

#### Delete Farm

Delete a farm from the system.

**Endpoint**: `DELETE /api/farms/{id}`

**Response** (204 No Content)

**cURL Example**:
```bash
curl -X DELETE http://localhost:8080/api/farms/223e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>"
```

---

## Equipment Service API

Base path: `/api/pumps` and `/api/sensors`

**Important**: Most Equipment Service endpoints require the `X-Farmer-Id` header for authorization.

### Pump Endpoints

#### Create Pump

Create a new connected pump for a farm.

**Endpoint**: `POST /api/pumps`

**Headers**:
- `Authorization: Bearer <token>` (required)
- `X-Farmer-Id: <farmer-uuid>` (required)

**Request Body**:
```json
{
  "farmId": "223e4567-e89b-12d3-a456-426614174000",
  "name": "Main Irrigation Pump",
  "model": "AquaPump Pro 3000",
  "serialNumber": "AP3000-2024-001",
  "installationDate": "2024-01-20",
  "flowRateMax": 150.5
}
```

**Request Fields**:
- `farmId` (required): UUID of the farm
- `name` (required): Pump name (2-100 characters)
- `model` (required): Pump model (2-100 characters)
- `serialNumber` (required): Serial number (5-50 characters)
- `installationDate` (required): Installation date (YYYY-MM-DD)
- `flowRateMax` (required): Maximum flow rate in liters/minute (minimum 0.1)

**Response** (201 Created):
```json
{
  "id": "323e4567-e89b-12d3-a456-426614174000",
  "farmId": "223e4567-e89b-12d3-a456-426614174000",
  "name": "Main Irrigation Pump",
  "model": "AquaPump Pro 3000",
  "serialNumber": "AP3000-2024-001",
  "status": "IDLE",
  "installationDate": "2024-01-20T00:00:00Z",
  "lastMaintenanceDate": "2024-01-20T00:00:00Z",
  "nextMaintenanceDate": "2024-07-20T00:00:00Z",
  "flowRateMax": 150.5,
  "currentFlowRate": 0.0,
  "operatingHours": 0,
  "createdAt": "2024-01-20T10:00:00Z",
  "updatedAt": "2024-01-20T10:00:00Z"
}
```

**Status Values**: `OPERATIONAL`, `IDLE`, `MAINTENANCE`, `FAULTY`, `OFFLINE`

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/pumps \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000" \
  -H "Content-Type: application/json" \
  -d '{
    "farmId": "223e4567-e89b-12d3-a456-426614174000",
    "name": "Main Irrigation Pump",
    "model": "AquaPump Pro 3000",
    "serialNumber": "AP3000-2024-001",
    "installationDate": "2024-01-20",
    "flowRateMax": 150.5
  }'
```

**Possible Errors**:
- `403 Forbidden`: Farmer does not have permission to access this farm

---

#### Get Pump by ID

Retrieve a specific pump by its ID.

**Endpoint**: `GET /api/pumps/{id}`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/pumps/323e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>"
```

---

#### Get Pumps by Farm

Retrieve all pumps for a specific farm.

**Endpoint**: `GET /api/pumps/farm/{farmId}`

**Headers**:
- `X-Farmer-Id: <farmer-uuid>` (required)

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/pumps/farm/223e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"
```

---

#### Get Pumps by Status

Retrieve pumps filtered by equipment status.

**Endpoint**: `GET /api/pumps/farm/{farmId}/status/{status}`

**Path Parameters**:
- `farmId` (required): Farm UUID
- `status` (required): One of `OPERATIONAL`, `IDLE`, `MAINTENANCE`, `FAULTY`, `OFFLINE`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/pumps/farm/223e4567-e89b-12d3-a456-426614174000/status/OPERATIONAL \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"
```

---

#### Get Operational Pumps

Retrieve all operational pumps for a farm.

**Endpoint**: `GET /api/pumps/farm/{farmId}/operational`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/pumps/farm/223e4567-e89b-12d3-a456-426614174000/operational \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"
```

---

#### Get Pumps with Overdue Maintenance

Retrieve pumps that have overdue maintenance.

**Endpoint**: `GET /api/pumps/farm/{farmId}/maintenance-overdue`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/pumps/farm/223e4567-e89b-12d3-a456-426614174000/maintenance-overdue \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"
```

---

#### Update Pump

Update an existing pump.

**Endpoint**: `PUT /api/pumps/{id}`

**Headers**:
- `X-Farmer-Id: <farmer-uuid>` (required)

**Request Body**:
```json
{
  "name": "Main Irrigation Pump - Updated",
  "status": "OPERATIONAL",
  "currentFlowRate": 120.5
}
```

**cURL Example**:
```bash
curl -X PUT http://localhost:8080/api/pumps/323e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Main Irrigation Pump - Updated",
    "status": "OPERATIONAL",
    "currentFlowRate": 120.5
  }'
```

---

#### Schedule Maintenance

Schedule maintenance for a pump.

**Endpoint**: `POST /api/pumps/{id}/maintenance/schedule`

**Headers**:
- `X-Farmer-Id: <farmer-uuid>` (required)

**Request Body**:
```json
{
  "scheduledDate": "2024-07-20T09:00:00Z",
  "description": "Regular 6-month maintenance check",
  "estimatedDuration": 4
}
```

**Request Fields**:
- `scheduledDate` (required): ISO 8601 date-time
- `description` (required): Maintenance description (5-500 characters)
- `estimatedDuration` (optional): Estimated duration in hours (minimum 1)

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/pumps/323e4567-e89b-12d3-a456-426614174000/maintenance/schedule \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000" \
  -H "Content-Type: application/json" \
  -d '{
    "scheduledDate": "2024-07-20T09:00:00Z",
    "description": "Regular 6-month maintenance check",
    "estimatedDuration": 4
  }'
```

---

#### Complete Maintenance

Mark maintenance as completed for a pump.

**Endpoint**: `POST /api/pumps/{id}/maintenance/complete`

**Headers**:
- `X-Farmer-Id: <farmer-uuid>` (required)

**Query Parameters**:
- `notes` (optional): Maintenance completion notes

**cURL Example**:
```bash
curl -X POST "http://localhost:8080/api/pumps/323e4567-e89b-12d3-a456-426614174000/maintenance/complete?notes=Maintenance%20completed%20successfully" \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"
```

---

#### Delete Pump

Delete a pump.

**Endpoint**: `DELETE /api/pumps/{id}`

**Headers**:
- `X-Farmer-Id: <farmer-uuid>` (required)

**Response** (204 No Content)

**cURL Example**:
```bash
curl -X DELETE http://localhost:8080/api/pumps/323e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"
```

---

### Sensor Endpoints

#### Create Sensor

Create a new connected sensor for a farm.

**Endpoint**: `POST /api/sensors`

**Headers**:
- `Authorization: Bearer <token>` (required)
- `X-Farmer-Id: <farmer-uuid>` (required)

**Request Body**:
```json
{
  "farmId": "223e4567-e89b-12d3-a456-426614174000",
  "name": "Soil Moisture Sensor Zone A",
  "type": "SOIL_MOISTURE",
  "manufacturer": "SensorTech Inc.",
  "model": "SMT-500",
  "serialNumber": "SMT500-2024-042",
  "installationDate": "2024-01-22"
}
```

**Request Fields**:
- `farmId` (required): UUID of the farm
- `name` (required): Sensor name (2-100 characters)
- `type` (required): One of `SOIL_MOISTURE`, `TEMPERATURE`, `HUMIDITY`, `PH_LEVEL`, `LIGHT`, `WATER_LEVEL`
- `manufacturer` (required): Manufacturer name (2-100 characters)
- `model` (required): Sensor model (2-100 characters)
- `serialNumber` (required): Serial number (5-50 characters)
- `installationDate` (required): Installation date (YYYY-MM-DD)

**Response** (201 Created):
```json
{
  "id": "423e4567-e89b-12d3-a456-426614174000",
  "farmId": "223e4567-e89b-12d3-a456-426614174000",
  "name": "Soil Moisture Sensor Zone A",
  "type": "SOIL_MOISTURE",
  "manufacturer": "SensorTech Inc.",
  "model": "SMT-500",
  "serialNumber": "SMT500-2024-042",
  "status": "ACTIVE",
  "installationDate": "2024-01-22T00:00:00Z",
  "lastReadingDate": null,
  "batteryLevel": 100,
  "currentValue": null,
  "unit": "%",
  "createdAt": "2024-01-22T12:00:00Z",
  "updatedAt": "2024-01-22T12:00:00Z"
}
```

**Status Values**: `ACTIVE`, `INACTIVE`, `MAINTENANCE`, `FAULTY`

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/sensors \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000" \
  -H "Content-Type: application/json" \
  -d '{
    "farmId": "223e4567-e89b-12d3-a456-426614174000",
    "name": "Soil Moisture Sensor Zone A",
    "type": "SOIL_MOISTURE",
    "manufacturer": "SensorTech Inc.",
    "model": "SMT-500",
    "serialNumber": "SMT500-2024-042",
    "installationDate": "2024-01-22"
  }'
```

---

#### Get Sensor by ID

Retrieve a specific sensor by its ID.

**Endpoint**: `GET /api/sensors/{id}`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/sensors/423e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>"
```

---

#### Get Sensors by Farm

Retrieve all sensors for a specific farm.

**Endpoint**: `GET /api/sensors/farm/{farmId}`

**Headers**:
- `X-Farmer-Id: <farmer-uuid>` (required)

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/sensors/farm/223e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"
```

---

#### Get Sensors by Type

Retrieve sensors filtered by type.

**Endpoint**: `GET /api/sensors/farm/{farmId}/type/{type}`

**Path Parameters**:
- `farmId` (required): Farm UUID
- `type` (required): Sensor type

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/sensors/farm/223e4567-e89b-12d3-a456-426614174000/type/SOIL_MOISTURE \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"
```

---

#### Get Active Sensors

Retrieve all active sensors for a farm.

**Endpoint**: `GET /api/sensors/farm/{farmId}/active`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/sensors/farm/223e4567-e89b-12d3-a456-426614174000/active \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"
```

---

#### Get Sensors with Low Battery

Retrieve sensors with battery level below 20%.

**Endpoint**: `GET /api/sensors/farm/{farmId}/low-battery`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/sensors/farm/223e4567-e89b-12d3-a456-426614174000/low-battery \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"
```

---

#### Get Offline Sensors

Retrieve sensors that haven't communicated in the last 30 minutes.

**Endpoint**: `GET /api/sensors/farm/{farmId}/offline`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/sensors/farm/223e4567-e89b-12d3-a456-426614174000/offline \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"
```

---

#### Update Sensor

Update an existing sensor.

**Endpoint**: `PUT /api/sensors/{id}`

**Headers**:
- `X-Farmer-Id: <farmer-uuid>` (required)

**Request Body**:
```json
{
  "name": "Soil Moisture Sensor Zone A - Updated",
  "status": "ACTIVE",
  "batteryLevel": 85,
  "currentValue": 65.3
}
```

**cURL Example**:
```bash
curl -X PUT http://localhost:8080/api/sensors/423e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Soil Moisture Sensor Zone A - Updated",
    "status": "ACTIVE",
    "batteryLevel": 85,
    "currentValue": 65.3
  }'
```

---

#### Delete Sensor

Delete a sensor.

**Endpoint**: `DELETE /api/sensors/{id}`

**Headers**:
- `X-Farmer-Id: <farmer-uuid>` (required)

**Response** (204 No Content)

**cURL Example**:
```bash
curl -X DELETE http://localhost:8080/api/sensors/423e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"
```

---

## Supervision Service API

Base path: `/api/events` and `/api/statistics`

### Event Endpoints

#### Get All Events

Retrieve all equipment events with optional filtering and pagination.

**Endpoint**: `GET /api/events`

**Query Parameters**:
- `farmId` (optional): Filter by farm UUID
- `equipmentId` (optional): Filter by equipment UUID
- `eventType` (optional): Filter by event type
- `severity` (optional): Filter by severity level
- `startDate` (optional): Filter events from this date (ISO 8601)
- `endDate` (optional): Filter events until this date (ISO 8601)
- `acknowledged` (optional): Filter by acknowledgment status (true/false)
- `page`, `size` (optional): Pagination parameters

**Event Types**: `STATUS_CHANGE`, `ALERT`, `MAINTENANCE_DUE`, `TELEMETRY_ANOMALY`, `OFFLINE`, `ERROR`

**Severity Levels**: `INFO`, `WARNING`, `CRITICAL`, `EMERGENCY`

**Response** (200 OK):
```json
{
  "content": [
    {
      "id": "523e4567-e89b-12d3-a456-426614174000",
      "farmId": "223e4567-e89b-12d3-a456-426614174000",
      "equipmentId": "323e4567-e89b-12d3-a456-426614174000",
      "equipmentName": "Main Irrigation Pump",
      "eventType": "STATUS_CHANGE",
      "severity": "INFO",
      "message": "Pump status changed from IDLE to OPERATIONAL",
      "metadata": {
        "previousStatus": "IDLE",
        "newStatus": "OPERATIONAL",
        "flowRate": 120.5
      },
      "timestamp": "2024-01-22T10:15:30Z",
      "acknowledged": false,
      "acknowledgedBy": null,
      "acknowledgedAt": null,
      "createdAt": "2024-01-22T10:15:30Z"
    }
  ],
  "totalElements": 156,
  "totalPages": 8
}
```

**cURL Example - All Events**:
```bash
curl -X GET "http://localhost:8080/api/events?page=0&size=20" \
  -H "Authorization: Bearer <token>"
```

**cURL Example - Filtered Events**:
```bash
curl -X GET "http://localhost:8080/api/events?farmId=223e4567-e89b-12d3-a456-426614174000&severity=CRITICAL&acknowledged=false" \
  -H "Authorization: Bearer <token>"
```

---

#### Get Event by ID

Retrieve a specific event by its unique identifier.

**Endpoint**: `GET /api/events/{id}`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/events/523e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>"
```

---

#### Get Unacknowledged Critical Events

Retrieve all unacknowledged critical events that require immediate attention.

**Endpoint**: `GET /api/events/unacknowledged`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/events/unacknowledged \
  -H "Authorization: Bearer <token>"
```

---

#### Get Events by Farm

Retrieve all events for a specific farm.

**Endpoint**: `GET /api/events/farm/{farmId}`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/events/farm/223e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>"
```

---

#### Get Events by Equipment

Retrieve all events for a specific piece of equipment.

**Endpoint**: `GET /api/events/equipment/{equipmentId}`

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/events/equipment/323e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>"
```

---

#### Acknowledge Event

Mark an event as acknowledged by a user.

**Endpoint**: `POST /api/events/{id}/acknowledge`

**Request Body**:
```json
{
  "acknowledgedBy": "john.doe@example.com",
  "notes": "Investigated the issue - pump was manually shut off for field inspection"
}
```

**Request Fields**:
- `acknowledgedBy` (required): Email of the user acknowledging the event
- `notes` (optional): Notes about the acknowledgment (max 1000 characters)

**Response** (200 OK):
```json
{
  "id": "523e4567-e89b-12d3-a456-426614174000",
  "farmId": "223e4567-e89b-12d3-a456-426614174000",
  "equipmentId": "323e4567-e89b-12d3-a456-426614174000",
  "equipmentName": "Main Irrigation Pump",
  "eventType": "ALERT",
  "severity": "CRITICAL",
  "message": "Pump offline - no communication for 15 minutes",
  "timestamp": "2024-01-22T15:30:00Z",
  "acknowledged": true,
  "acknowledgedBy": "john.doe@example.com",
  "acknowledgedAt": "2024-01-22T15:45:00Z",
  "notes": "Investigated the issue - pump was manually shut off for field inspection"
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/events/523e4567-e89b-12d3-a456-426614174000/acknowledge \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "acknowledgedBy": "john.doe@example.com",
    "notes": "Investigated the issue - pump was manually shut off for field inspection"
  }'
```

---

### Statistics Endpoints

#### Get Dashboard Statistics

Retrieve comprehensive dashboard statistics including event counts, recent events, and trends.

**Endpoint**: `GET /api/statistics/dashboard`

**Response** (200 OK):
```json
{
  "totalEvents": 1542,
  "totalCriticalEvents": 23,
  "unacknowledgedCriticalEvents": 5,
  "eventsBySeverity": {
    "INFO": 1245,
    "WARNING": 274,
    "CRITICAL": 21,
    "EMERGENCY": 2
  },
  "eventsByType": {
    "STATUS_CHANGE": 856,
    "ALERT": 312,
    "MAINTENANCE_DUE": 145,
    "TELEMETRY_ANOMALY": 189,
    "OFFLINE": 28,
    "ERROR": 12
  },
  "recentCriticalEvents": [
    {
      "id": "523e4567-e89b-12d3-a456-426614174002",
      "equipmentName": "Main Irrigation Pump",
      "eventType": "ALERT",
      "severity": "CRITICAL",
      "message": "Pump offline - no communication for 15 minutes",
      "timestamp": "2024-01-22T15:30:00Z",
      "acknowledged": false
    }
  ],
  "equipmentHealthMetrics": {
    "totalEquipment": 45,
    "operational": 38,
    "faulty": 4,
    "offline": 3
  },
  "eventTrends": {
    "last24Hours": 87,
    "last7Days": 456,
    "last30Days": 1542
  },
  "averageResponseTime": 14.5,
  "generatedAt": "2024-01-22T17:00:00Z"
}
```

**Statistics Fields**:
- `totalEvents`: Total number of events in the system
- `totalCriticalEvents`: Count of critical and emergency events
- `unacknowledgedCriticalEvents`: Count of unacknowledged critical events
- `eventsBySeverity`: Event count grouped by severity level
- `eventsByType`: Event count grouped by event type
- `recentCriticalEvents`: List of last 10 critical events
- `equipmentHealthMetrics`: Overall equipment health statistics
- `eventTrends`: Event counts for different time periods
- `averageResponseTime`: Average time to acknowledge critical events (minutes)
- `generatedAt`: ISO 8601 timestamp when statistics were generated

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/statistics/dashboard \
  -H "Authorization: Bearer <token>"
```

---

## Sample Workflows

### Workflow 1: Complete Farm Setup

Create a farmer, farm, and equipment in sequence:

```bash
# 1. Create Farmer
curl -X POST http://localhost:8080/api/farmers \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "+1-555-0123",
    "role": "OWNER",
    "region": "North Region"
  }'
# Response includes farmer ID

# 2. Create Farm
curl -X POST http://localhost:8080/api/farms \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Green Valley Farm",
    "location": "North Region, Sector 5",
    "area": 150.5,
    "farmerId": "123e4567-e89b-12d3-a456-426614174000"
  }'
# Response includes farm ID

# 3. Create Pump
curl -X POST http://localhost:8080/api/pumps \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000" \
  -H "Content-Type: application/json" \
  -d '{
    "farmId": "223e4567-e89b-12d3-a456-426614174000",
    "name": "Main Irrigation Pump",
    "model": "AquaPump Pro 3000",
    "serialNumber": "AP3000-2024-001",
    "installationDate": "2024-01-20",
    "flowRateMax": 150.5
  }'

# 4. Create Sensor
curl -X POST http://localhost:8080/api/sensors \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000" \
  -H "Content-Type: application/json" \
  -d '{
    "farmId": "223e4567-e89b-12d3-a456-426614174000",
    "name": "Soil Moisture Sensor Zone A",
    "type": "SOIL_MOISTURE",
    "manufacturer": "SensorTech Inc.",
    "model": "SMT-500",
    "serialNumber": "SMT500-2024-042",
    "installationDate": "2024-01-22"
  }'
```

---

### Workflow 2: Monitor Critical Events

```bash
# 1. Get Unacknowledged Critical Events
curl -X GET http://localhost:8080/api/events/unacknowledged \
  -H "Authorization: Bearer <token>"

# 2. Get Details of Specific Event
curl -X GET http://localhost:8080/api/events/523e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>"

# 3. Acknowledge the Event
curl -X POST http://localhost:8080/api/events/523e4567-e89b-12d3-a456-426614174000/acknowledge \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "acknowledgedBy": "john.doe@example.com",
    "notes": "Issue resolved - pump restarted"
  }'
```

---

### Workflow 3: Maintenance Scheduling

```bash
# 1. Get Pumps with Overdue Maintenance
curl -X GET http://localhost:8080/api/pumps/farm/223e4567-e89b-12d3-a456-426614174000/maintenance-overdue \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"

# 2. Schedule Maintenance for Pump
curl -X POST http://localhost:8080/api/pumps/323e4567-e89b-12d3-a456-426614174000/maintenance/schedule \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000" \
  -H "Content-Type: application/json" \
  -d '{
    "scheduledDate": "2024-07-20T09:00:00Z",
    "description": "Regular 6-month maintenance check",
    "estimatedDuration": 4
  }'

# 3. Complete Maintenance
curl -X POST "http://localhost:8080/api/pumps/323e4567-e89b-12d3-a456-426614174000/maintenance/complete?notes=Maintenance%20completed%20successfully" \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"
```

---

### Workflow 4: Dashboard Monitoring

```bash
# 1. Get Dashboard Statistics
curl -X GET http://localhost:8080/api/statistics/dashboard \
  -H "Authorization: Bearer <token>"

# 2. Get All Events for a Specific Farm
curl -X GET http://localhost:8080/api/events/farm/223e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <token>"

# 3. Get All Operational Pumps
curl -X GET http://localhost:8080/api/pumps/farm/223e4567-e89b-12d3-a456-426614174000/operational \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"

# 4. Get Sensors with Low Battery
curl -X GET http://localhost:8080/api/sensors/farm/223e4567-e89b-12d3-a456-426614174000/low-battery \
  -H "Authorization: Bearer <token>" \
  -H "X-Farmer-Id: 123e4567-e89b-12d3-a456-426614174000"
```

---

## OpenAPI Specifications

Detailed OpenAPI 3.0 specifications are available for each service:

- **Farmers Service**: `docs/api/farmers-service-api.yaml`
- **Equipment Service**: `docs/api/equipment-service-api.yaml`
- **Supervision Service**: `docs/api/supervision-service-api.yaml`

These YAML files can be imported into tools like:
- **Swagger UI**: Available at service endpoints (e.g., http://localhost:8081/swagger-ui.html)
- **Postman**: Import OpenAPI spec directly
- **API Client Generators**: Use with OpenAPI Generator for automatic client generation

---

## Postman Collection

A complete Postman collection is available at `docs/api/postman-collection.json` containing:

- Pre-configured requests for all endpoints
- Collection variables for easy customization
- Sample request bodies
- Organized folder structure by service

### Importing into Postman

1. Open Postman
2. Click **Import**
3. Select `docs/api/postman-collection.json`
4. Configure collection variables:
   - `base_url`: Your API Gateway URL
   - `jwt_token`: Your JWT authentication token
   - Resource IDs (farmer_id, farm_id, etc.)

---

## Support and Resources

### Swagger UI Access

Each service provides interactive API documentation via Swagger UI:

- **Farmers Service**: http://localhost:8081/swagger-ui.html
- **Equipment Service**: http://localhost:8082/swagger-ui.html
- **Supervision Service**: http://localhost:8083/swagger-ui.html

### Additional Documentation

- **Main README**: Project overview and setup instructions
- **Testing Guide**: `TESTING.md` - Comprehensive testing documentation
- **Kubernetes Deployment**: `KUBERNETES_README.md` - K8s deployment guide
- **Docker Guide**: `DOCKER_README.md` - Docker containerization details

### Contact

For API support or questions:
- **Email**: support@farmmonitoring.com
- **GitHub Issues**: [Project Issue Tracker]

---

**Last Updated**: January 2025  
**API Version**: 1.0.0  
**Documentation Version**: 1.0.0
