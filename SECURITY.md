# Security Policy

## Supported Versions

| Version | Supported          |
|---------|--------------------|
| 1.x.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability, please report it privately before disclosing it publicly.

### How to Report

**Email**: security@boava.org

Please include:
- Type of vulnerability
- Affected versions
- Steps to reproduce
- Potential impact
- Any suggested fixes (optional)

### Response Time

We will respond within 48 hours and provide:
- Initial assessment
- Estimated timeline for fix
- Communication plan for disclosure

### Disclosure Process

1. **Private Report**: Vulnerability reported privately
2. **Assessment**: Team evaluates and validates the vulnerability
3. **Fix Development**: Patch is developed and tested
4. **Coordinated Disclosure**: Security advisory is published
5. **Public Disclosure**: Details are shared after patch is available

## Security Best Practices

### For Users

- Keep dependencies updated
- Review security advisories
- Follow secure coding practices
- Monitor for vulnerability announcements

### For Developers

- Input validation and sanitization
- Secure dependency management
- Regular security reviews
- Principle of least privilege

## Security Features

This library includes:

- **No external runtime dependencies** (besides Jakarta Persistence API)
- **OWASP Dependency Check** integration
- **Comprehensive test coverage** (>90%)
- **Static analysis** in CI/CD pipeline
- **Signed releases** with GPG

## Known Limitations

- Database security depends on underlying database configuration
- JPA provider security is the responsibility of the application
- Network security for database connections is application-managed

## Security Updates

Security updates are handled through:
- Patch releases for critical vulnerabilities
- Security advisories on GitHub
- Maven Central notifications
- Community announcements

## Acknowledgments

We thank security researchers who help us maintain secure software. All responsible disclosures are acknowledged in our security advisories.

For questions about this security policy, email security@boava.org.
