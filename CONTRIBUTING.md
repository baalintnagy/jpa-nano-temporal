# Contributing to Embeddable Temporal

We welcome contributions to the Embeddable Temporal library! This guide will help you get started.

## 🚀 Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Git
- IDE (IntelliJ IDEA, Eclipse, or VS Code recommended)

### Setup

1. **Fork the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/embeddable-temporal.git
   cd embeddable-temporal
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Run tests**
   ```bash
   mvn test
   ```

## 📝 Development Guidelines

### Code Style

We follow standard Java conventions:
- Use 4-space indentation
- Maximum line length: 120 characters
- Use meaningful variable and method names
- Add Javadoc comments for public APIs

### Testing

- All new features must include unit tests
- Maintain >90% code coverage
- Use descriptive test names
- Follow Arrange-Act-Assert pattern

### Commit Messages

Use conventional commit format:
```
type(scope): description

[optional body]

[optional footer]
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Code style (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Test additions/changes
- `chore`: Build process, dependency updates

Example:
```
feat(converter): add LocalDateTime converter

Add support for converting between LocalDateTime and EmbeddableTemporal
while preserving timezone information.

Closes #123
```

## 🔄 Pull Request Process

1. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes**
   - Write code following our guidelines
   - Add comprehensive tests
   - Update documentation if needed

3. **Run the full test suite**
   ```bash
   mvn clean verify
   ```

4. **Commit your changes**
   ```bash
   git add .
   git commit -m "feat: add your feature"
   ```

5. **Push and create PR**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create a Pull Request**
   - Use descriptive title
   - Fill out the PR template
   - Link any relevant issues

## 🧪 Running Tests

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Coverage Report
```bash
mvn clean verify jacoco:report
```

### Specific Test Class
```bash
mvn test -Dtest=EmbeddableTemporalTest
```

## 🐛 Bug Reports

When reporting bugs, please include:
- Java version
- Database and version
- JPA provider and version
- Minimal reproduction code
- Stack trace (if applicable)
- Expected vs actual behavior

## 💡 Feature Requests

We welcome feature suggestions! Please:
- Check existing issues first
- Describe the use case
- Explain why it's valuable
- Consider API design

## 📚 Documentation

- Update README.md for user-facing changes
- Add Javadoc for public APIs
- Update this CONTRIBUTING.md if process changes

## 🔍 Code Review

All PRs require review. Reviewers check:
- Functionality and correctness
- Test coverage
- Code style and conventions
- Documentation
- Performance implications

## 🏷️ Release Process

Releases are automated via GitHub Actions:
1. Create a release tag: `git tag v1.0.0`
2. Push the tag: `git push origin v1.0.0`
3. GitHub Actions will build and deploy to Maven Central

## 🤝 Community

- Be respectful and inclusive
- Help others in discussions
- Share knowledge and experiences
- Follow our Code of Conduct

## 📞 Getting Help

- Check [documentation](README.md)
- Search [existing issues](https://github.com/temporal-precision/embeddable-temporal/issues)
- Start a [discussion](https://github.com/temporal-precision/embeddable-temporal/discussions)
- Create an issue for bugs or feature requests

Thank you for contributing! 🎉
