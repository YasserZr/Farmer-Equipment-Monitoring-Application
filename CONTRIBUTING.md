# Contributing to Farmer & Equipment Monitoring Application

Thank you for considering contributing to this project! This document provides guidelines and conventions for contributing.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Git Workflow](#git-workflow)
- [Commit Message Conventions](#commit-message-conventions)
- [Branch Naming Conventions](#branch-naming-conventions)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)

## 🤝 Code of Conduct

- Be respectful and inclusive
- Welcome newcomers and encourage diverse perspectives
- Focus on constructive feedback
- Maintain professional communication

## 🚀 Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/Farmer-Equipment-Monitoring-Application.git`
3. Add upstream remote: `git remote add upstream https://github.com/YasserZr/Farmer-Equipment-Monitoring-Application.git`
4. Create a feature branch from `main`
5. Make your changes
6. Test thoroughly
7. Submit a pull request

## 🔄 Git Workflow

We follow the **Git Feature Branch Workflow**:

1. **Always branch from `main`**
2. **Keep branches focused** - one feature/fix per branch
3. **Sync regularly** with `main` to avoid conflicts
4. **Rebase before merging** to keep history clean

```bash
# Create a new feature branch
git checkout main
git pull upstream main
git checkout -b feature/your-feature-name

# Make changes and commit
git add .
git commit -m "feat(farmers): add farmer profile endpoint"

# Before submitting PR, sync with main
git fetch upstream
git rebase upstream/main

# Push to your fork
git push origin feature/your-feature-name
```

## 📝 Commit Message Conventions

We use **Conventional Commits** specification for clear and semantic commit messages.

### Format

```
<type>(<scope>): <subject>

<body> (optional)

<footer> (optional)
```

### Types

- **feat**: A new feature
- **fix**: A bug fix
- **docs**: Documentation changes only
- **style**: Code style changes (formatting, missing semicolons, etc.)
- **refactor**: Code refactoring without changing functionality
- **perf**: Performance improvements
- **test**: Adding or updating tests
- **build**: Changes to build system or dependencies
- **ci**: Changes to CI/CD configuration
- **chore**: Other changes that don't modify src or test files
- **revert**: Reverts a previous commit

### Scopes

Use the service or component name as scope:

- **farmers**: Farmers microservice
- **equipment**: Equipment microservice
- **supervision**: Supervision microservice
- **gateway**: API Gateway
- **eureka**: Eureka Server
- **config**: Config Server
- **frontend**: Next.js frontend
- **docker**: Docker configuration
- **k8s**: Kubernetes manifests
- **docs**: Documentation

### Examples

```bash
# Feature addition
git commit -m "feat(farmers): add farmer registration endpoint"

# Bug fix
git commit -m "fix(equipment): resolve null pointer in telemetry processor"

# Documentation
git commit -m "docs(readme): update setup instructions for local development"

# Breaking change
git commit -m "feat(gateway)!: change authentication to OAuth2

BREAKING CHANGE: JWT authentication replaced with OAuth2.
Clients must now use OAuth2 client credentials flow."

# Multiple files
git commit -m "refactor(farmers): restructure service layer
- Extract permission logic to separate service
- Add PermissionService interface
- Update unit tests"

# Dependency update
git commit -m "build(equipment): upgrade spring-boot to 3.2.0"

# Infrastructure
git commit -m "ci: add GitHub Actions workflow for Docker builds"

# Frontend
git commit -m "feat(frontend): add equipment dashboard page"

# Kubernetes
git commit -m "chore(k8s): add resource limits to deployment manifests"
```

### Commit Message Rules

1. **Use imperative mood** in subject: "add feature" not "added feature"
2. **Don't capitalize** the first letter of subject
3. **No period** at the end of subject
4. **Limit subject line** to 72 characters
5. **Separate subject from body** with a blank line
6. **Use body to explain** what and why, not how
7. **Reference issues** in footer: `Fixes #123` or `Closes #456`

## 🌿 Branch Naming Conventions

Use descriptive branch names with the following patterns:

```
feature/<short-description>      # New features
fix/<issue-number>-<description> # Bug fixes
hotfix/<critical-issue>          # Critical production fixes
refactor/<component-name>        # Code refactoring
docs/<what-is-documented>        # Documentation updates
test/<what-is-tested>            # Test additions
chore/<task-description>         # Maintenance tasks
```

### Examples

```
feature/farmer-profile-api
feature/equipment-telemetry-ingestion
fix/123-null-pointer-in-gateway
fix/supervision-event-processing
hotfix/security-vulnerability-jwt
refactor/farmers-service-layer
docs/kubernetes-deployment-guide
test/equipment-integration-tests
chore/upgrade-spring-dependencies
```

## 🔍 Pull Request Process

1. **Update documentation** if you change APIs or configuration
2. **Add/update tests** for new features or bug fixes
3. **Ensure all tests pass** locally before submitting
4. **Update the README.md** if needed
5. **Follow the PR template** when creating your pull request
6. **Link related issues** using keywords (Fixes #, Closes #, Resolves #)
7. **Request review** from at least one maintainer
8. **Address review feedback** promptly
9. **Squash commits** if requested before merge

### PR Title Format

Use the same format as commit messages:

```
feat(equipment): add sensor calibration endpoint
fix(gateway): resolve CORS issue for frontend requests
docs(architecture): update microservices communication diagram
```

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix (non-breaking change)
- [ ] New feature (non-breaking change)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update

## Related Issues
Fixes #(issue number)

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] No new warnings generated
- [ ] Tests pass locally
```

## 💻 Coding Standards

### Java / Spring Boot

- Follow **Google Java Style Guide**
- Use **Lombok** to reduce boilerplate
- Apply **SOLID principles**
- Write **meaningful variable and method names**
- Keep methods **small and focused** (< 20 lines preferred)
- Add **JavaDoc** for public APIs
- Use **constructor injection** over field injection
- Handle exceptions properly, don't swallow them

```java
// Good
@Service
@RequiredArgsConstructor
public class FarmerService {
    private final FarmerRepository farmerRepository;
    
    /**
     * Retrieves a farmer by ID.
     * @param id the farmer ID
     * @return Farmer entity
     * @throws FarmerNotFoundException if not found
     */
    public Farmer getFarmerById(Long id) {
        return farmerRepository.findById(id)
            .orElseThrow(() -> new FarmerNotFoundException(id));
    }
}
```

### TypeScript / Next.js

- Use **TypeScript strict mode**
- Follow **Airbnb React Style Guide**
- Use **functional components** with hooks
- Apply **proper typing** for props and state
- Use **descriptive component names**
- Keep components **small and reusable**

```typescript
// Good
interface EquipmentCardProps {
  equipment: Equipment;
  onStatusChange: (id: string, status: EquipmentStatus) => void;
}

export const EquipmentCard: React.FC<EquipmentCardProps> = ({ 
  equipment, 
  onStatusChange 
}) => {
  // component logic
};
```

### General

- **DRY**: Don't Repeat Yourself
- **KISS**: Keep It Simple, Stupid
- **YAGNI**: You Aren't Gonna Need It
- Write **self-documenting code**
- Avoid **magic numbers** - use constants
- Use **meaningful comments** for complex logic only

## 🧪 Testing Guidelines

### Backend (Spring Boot)

- Write **unit tests** for service layer logic
- Write **integration tests** for controllers and repositories
- Use **@SpringBootTest** for integration tests
- Mock external dependencies with **Mockito**
- Aim for **>80% code coverage**

```java
@SpringBootTest
@AutoConfigureMockMvc
class FarmerControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldReturnFarmerById() throws Exception {
        mockMvc.perform(get("/api/farmers/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }
}
```

### Frontend (Next.js)

- Use **Jest** for unit tests
- Use **React Testing Library** for component tests
- Test **user interactions** and **accessibility**
- Mock API calls

```typescript
import { render, screen } from '@testing-library/react';
import { EquipmentCard } from './EquipmentCard';

describe('EquipmentCard', () => {
  it('renders equipment name', () => {
    const equipment = { id: '1', name: 'Pump A', status: 'ACTIVE' };
    render(<EquipmentCard equipment={equipment} onStatusChange={jest.fn()} />);
    expect(screen.getByText('Pump A')).toBeInTheDocument();
  });
});
```

## 📦 Versioning

We use **Semantic Versioning** (SemVer):

- **MAJOR** version for incompatible API changes
- **MINOR** version for backwards-compatible functionality
- **PATCH** version for backwards-compatible bug fixes

## ❓ Questions?

If you have questions or need clarification:
- Open a **GitHub Discussion**
- Ask in the **PR comments**
- Check existing **issues and documentation**

---

**Thank you for contributing! 🎉**
