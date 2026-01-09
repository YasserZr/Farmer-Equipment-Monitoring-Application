# Testing Documentation

## Overview

This document provides comprehensive information about the test suites implemented for the Farm Equipment Monitoring Application, covering both backend microservices and frontend components.

## Backend Testing (Spring Boot)

### Test Structure

Each microservice follows a three-layered testing approach:

1. **Unit Tests** - Service layer with Mockito
2. **Integration Tests** - Repository layer with TestContainers
3. **REST API Tests** - Controllers with MockMvc

### Test Configuration

#### Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- TestContainers -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>testcontainers</artifactId>
        <version>1.19.3</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <version>1.19.3</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>1.19.3</version>
        <scope>test</scope>
    </dependency>
    
    <!-- Spring Cloud Contract (for Feign testing) -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-contract-stub-runner</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### Test Configuration (application-test.yml)

Located in `src/test/resources/application-test.yml`:

```yaml
spring:
  datasource:
    url: jdbc:tc:postgresql:15-alpine:///testdb
    driver-class-name: org.testcontainers.jdbc.ContainerDatabaseDriver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  cloud:
    config:
      enabled: false
  rabbitmq:
    host: localhost
    port: 5672

eureka:
  client:
    enabled: false
```

### Unit Tests (Mockito)

**Location**: `src/test/java/com/farmmonitoring/{service}/service/`

**Example**: FarmerServiceTest.java

**Coverage**:
- CRUD operations
- Business logic validation
- Exception handling
- Permission verification

**Key Patterns**:
```java
@ExtendWith(MockitoExtension.class)
class FarmerServiceTest {
    @Mock
    private FarmerRepository repository;
    
    @InjectMocks
    private FarmerService service;
    
    @Test
    void testCreateFarmer_Success() {
        // Given
        when(repository.save(any())).thenReturn(farmer);
        
        // When
        FarmerDTO result = service.createFarmer(farmerDTO);
        
        // Then
        assertThat(result).isNotNull();
        verify(repository, times(1)).save(any());
    }
}
```

### Integration Tests (TestContainers)

**Location**: `src/test/java/com/farmmonitoring/{service}/repository/`

**Example**: FarmerRepositoryTest.java

**Coverage**:
- Database operations
- Query methods
- Transaction handling
- Data persistence

**Key Patterns**:
```java
@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
class FarmerRepositoryTest {
    @Container
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:15-alpine");
    
    @Autowired
    private FarmerRepository repository;
    
    @Test
    void testFindByEmail() {
        // Test implementation
    }
}
```

### REST API Tests (MockMvc)

**Location**: `src/test/java/com/farmmonitoring/{service}/controller/`

**Example**: FarmerControllerTest.java

**Coverage**:
- HTTP endpoints
- Request/response validation
- Status codes
- Error handling

**Key Patterns**:
```java
@WebMvcTest(FarmerController.class)
class FarmerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private FarmerService service;
    
    @Test
    void testCreateFarmer_Success() throws Exception {
        mockMvc.perform(post("/api/farmers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }
}
```

### Feign Client Tests

**Location**: `src/test/java/com/farmmonitoring/{service}/client/`

**Coverage**:
- Feign client communication
- Contract validation
- Error handling

**Key Patterns**:
```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class FarmerClientTest {
    @Autowired
    private FarmerClient client;
    
    @Test
    void testGetFarmerById() {
        stubFor(get(urlEqualTo("/api/farmers/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(json)));
        
        FarmerDTO result = client.getFarmerById(1L);
        assertThat(result).isNotNull();
    }
}
```

## Frontend Testing (Next.js + React)

### Test Structure

1. **Component Tests** - React Testing Library
2. **Hook Tests** - React Hooks Testing Library
3. **Page Integration Tests** - Full page rendering
4. **API Mock Tests** - Mocked fetch calls

### Test Configuration

#### Dependencies (package.json)

```json
{
  "devDependencies": {
    "@testing-library/react": "^14.0.0",
    "@testing-library/jest-dom": "^6.1.5",
    "@testing-library/user-event": "^14.5.1",
    "@vitejs/plugin-react": "^4.2.1",
    "vitest": "^1.0.4",
    "jsdom": "^23.0.1"
  },
  "scripts": {
    "test": "vitest",
    "test:unit": "vitest run --coverage",
    "test:integration": "vitest run --config vitest.integration.config.ts",
    "test:coverage": "vitest run --coverage"
  }
}
```

#### Vitest Configuration (vitest.config.ts)

```typescript
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./__tests__/setup.ts'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html', 'lcov'],
    },
  },
});
```

### Component Tests

**Location**: `__tests__/components/`

**Coverage**:
- Component rendering
- User interactions
- Form validation
- Error states

**Example**:
```typescript
describe('FarmerForm', () => {
  it('displays validation errors', async () => {
    render(<FarmerForm />);
    
    fireEvent.click(screen.getByRole('button', { name: /submit/i }));
    
    await waitFor(() => {
      expect(screen.getByText(/first name is required/i))
        .toBeInTheDocument();
    });
  });
});
```

### Hook Tests

**Location**: `__tests__/hooks/`

**Coverage**:
- Custom hooks logic
- Data fetching
- State management
- Error handling

**Example**:
```typescript
describe('useFarmers', () => {
  it('fetches farmers successfully', async () => {
    const { result } = renderHook(() => useFarmers({ page: 0 }), {
      wrapper: createWrapper(),
    });
    
    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });
  });
});
```

### Page Integration Tests

**Location**: `__tests__/pages/`

**Coverage**:
- Full page rendering
- Data loading states
- Navigation
- User workflows

## Running Tests

### Backend Tests

```bash
# Run all tests for a service
cd backend/farmers-service
mvn clean test

# Run specific test class
mvn test -Dtest=FarmerServiceTest

# Run with coverage
mvn clean test jacoco:report

# Run integration tests only
mvn verify -Pintegration-tests
```

### Frontend Tests

```bash
cd frontend

# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Run with coverage
npm run test:coverage

# Run specific test file
npm test farmer-form.test.tsx
```

## CI/CD Integration

### GitHub Actions Workflow

The CI/CD pipeline (`.github/workflows/ci-cd.yml`) includes:

1. **Backend Tests**: Runs all tests for each microservice in parallel
2. **Frontend Tests**: Runs lint, type-check, unit, and integration tests
3. **Integration Tests**: Runs with PostgreSQL and RabbitMQ services
4. **Coverage Reports**: Uploads to Codecov
5. **Docker Build**: Builds images on main branch push
6. **Security Scan**: Trivy vulnerability scanning
7. **Deployment**: Deploys to Kubernetes on successful tests

### Running Locally

```bash
# Simulate CI pipeline locally
# Backend
for service in eureka-server config-server api-gateway farmers-service equipment-service supervision-service; do
  echo "Testing $service..."
  cd backend/$service
  mvn clean test
  cd ../..
done

# Frontend
cd frontend
npm run lint
npm run type-check
npm run test:coverage
```

## Test Coverage Goals

### Backend
- **Unit Tests**: > 80% line coverage
- **Integration Tests**: All repository methods
- **Controller Tests**: All endpoints

### Frontend
- **Component Tests**: > 80% coverage
- **Hook Tests**: All custom hooks
- **Integration Tests**: Critical user flows

## Best Practices

1. **Arrange-Act-Assert (AAA)**: Structure all tests with clear Given-When-Then sections
2. **Test Isolation**: Each test should be independent
3. **Meaningful Names**: Use descriptive test method names
4. **Mock External Dependencies**: Use mocks for external services
5. **Test Edge Cases**: Include error scenarios and boundary conditions
6. **Fast Tests**: Keep unit tests fast, use TestContainers sparingly
7. **Readable Assertions**: Use AssertJ/Testing Library matchers

## Troubleshooting

### Common Issues

**TestContainers fails to start**:
```bash
# Ensure Docker is running
docker ps

# Check TestContainers logs
docker logs <container-id>
```

**Frontend tests timeout**:
```typescript
// Increase timeout in vitest.config.ts
test: {
  testTimeout: 10000,
}
```

**Mock not working**:
```java
// Reset mocks between tests
@BeforeEach
void setUp() {
    Mockito.reset(repository);
}
```

## Additional Resources

- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/)
- [TestContainers Documentation](https://www.testcontainers.org/)
- [React Testing Library](https://testing-library.com/react)
- [Vitest Documentation](https://vitest.dev/)
