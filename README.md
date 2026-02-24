# JPA Temporal

[![CI](https://github.com/boava/jpa-temporal/workflows/CI/badge.svg)](https://github.com/boava/jpa-temporal/actions)
[![codecov](https://codecov.io/gh/boava/jpa-temporal/branch/main/graph/badge.svg)](https://codecov.io/gh/boava/jpa-temporal)
[![Maven Central](https://img.shields.io/maven-central/v/org.boava/jpa.temporal.svg)](https://search.maven.org/search?q=g:org.boava%20AND%20a:jpa.temporal)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A JPA embeddable type for storing temporal values with nanosecond precision across different databases.

## 🚀 Why This Library?

JPA/Hibernate has inconsistent handling of temporal types across different database systems:

| Database | Instant Support | Duration Support | Precision Loss |
|----------|-----------------|------------------|----------------|
| MariaDB  | ❌ Second-only  | ❌ Second-only   | ✅ Yes |
| H2       | ❌ Second-only  | ❌ Second-only   | ✅ Yes |
| PostgreSQL | ✅ Native      | ✅ INTERVAL      | ❌ No |
| MySQL    | ❌ Second-only  | ❌ Second-only   | ✅ Yes |

This library solves these problems by:
- ✅ **Consistent behavior** across all supported databases
- ✅ **Full nanosecond precision** preservation
- ✅ **Type safety** with proper conversions
- ✅ **Database-agnostic** storage using primitive types

## 📦 Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.boava</groupId>
    <artifactId>jpa.temporal</artifactId>
    <version>1.0.0</version>
</dependency>
```

Or to your `build.gradle`:

```groovy
implementation 'org.boava:jpa.temporal:1.0.0'
```

## 🔧 Quick Start

### Basic Usage in JPA Entities

```java
import org.boava.jpa.temporal.embeddable.EmbeddableTemporal;
import jakarta.persistence.*;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @Embedded
    @AttributeOverride(name = "seconds", column = @Column(name = "event_timestamp_seconds"))
    @AttributeOverride(name = "nanos", column = @Column(name = "event_timestamp_nanos"))
    private EmbeddableTemporal timestamp;
    
    @Embedded
    @AttributeOverride(name = "seconds", column = @Column(name = "duration_seconds"))
    @AttributeOverride(name = "nanos", column = @Column(name = "duration_nanos"))
    private EmbeddableTemporal duration;
    
    // Constructors, getters, setters...
}
```

### Creating and Converting Values

```java
import java.time.Instant;
import java.time.Duration;

// From Instant
Instant now = Instant.now();
EmbeddableTemporal timestamp = EmbeddableTemporal.from(now);

// From Duration  
Duration processingTime = Duration.ofMinutes(5);
EmbeddableTemporal duration = EmbeddableTemporal.from(processingTime);

// Convert back to temporal types
Instant roundTripInstant = timestamp.toInstant();
Duration roundTripDuration = duration.toDuration();

// Custom conversions
String formatted = timestamp.convert((seconds, nanos) -> 
    String.format("%d.%09d seconds", seconds, nanos));
```

### Working with Temporal Values

```java
EmbeddableTemporal t1 = EmbeddableTemporal.from(Duration.ofSeconds(100, 500_000_000));
EmbeddableTemporal t2 = EmbeddableTemporal.from(Duration.ofSeconds(50, 600_000_000));

// Arithmetic operations
EmbeddableTemporal sum = t1.add(t2);        // 151s, 100M ns
EmbeddableTemporal diff = t1.subtract(t2);  // 49s, 900M ns

// Utility methods
boolean isPositive = t1.isPositive();
EmbeddableTemporal abs = t1.abs();

// Comparisons
int comparison = t1.compareTo(t2);
boolean isEqual = t1.equals(t2);
```

## 🏗️ Architecture

The library stores temporal values as two primitive components:
- **`seconds`** (long): The whole seconds component
- **`nanos`** (int): The nanosecond component (0..999,999,999)

This approach ensures:
- **Database compatibility**: Works with any database that supports numeric types
- **Precision preservation**: No loss of nanosecond precision
- **Performance**: Efficient storage and retrieval
- **Portability**: Consistent behavior across all JPA providers

## 📚 API Reference

### Factory Methods

| Method | Description | Example |
|--------|-------------|---------|
| `from(Instant)` | Create from Instant | `EmbeddableTemporal.from(Instant.now())` |
| `from(Duration)` | Create from Duration | `EmbeddableTemporal.from(Duration.ofMinutes(5))` |

### Conversion Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `toInstant()` | `Instant` | Convert to Instant |
| `toDuration()` | `Duration` | Convert to Duration |
| `toLocalDateTime()` | `LocalDateTime` | Convert to LocalDateTime (UTC) |
| `toZonedDateTime()` | `ZonedDateTime` | Convert to ZonedDateTime (UTC) |
| `toOffsetDateTime()` | `OffsetDateTime` | Convert to OffsetDateTime (UTC) |
| `convert(TemporalConverter<T>)` | `T` | Custom conversion |

### Utility Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `isZero()` | `boolean` | Check if value is zero |
| `isPositive()` | `boolean` | Check if value is positive |
| `isNegative()` | `boolean` | Check if value is negative |
| `add(EmbeddableTemporal)` | `EmbeddableTemporal` | Add two temporal values |
| `subtract(EmbeddableTemporal)` | `EmbeddableTemporal` | Subtract two temporal values |
| `abs()` | `EmbeddableTemporal` | Absolute value |

## 🗄️ Database Schema

When used in JPA entities, the library creates two columns per temporal field:

```sql
CREATE TABLE event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    event_timestamp_seconds BIGINT NOT NULL,
    event_timestamp_nanos INT NOT NULL,
    duration_seconds BIGINT NOT NULL,
    duration_nanos INT NOT NULL
);
```

## 🧪 Testing

The library includes comprehensive unit tests with >90% code coverage:

```bash
# Run all tests
mvn test

# Run with coverage
mvn clean verify

# Run specific test class
mvn test -Dtest=EmbeddableTemporalTest
```

## 🔒 Security

This library has no external dependencies beyond the Jakarta Persistence API and includes:

- **OWASP Dependency Check**: Automated vulnerability scanning
- **Code Coverage**: 90%+ test coverage requirement
- **Static Analysis**: Comprehensive code quality checks

## 📋 Requirements

- **Java**: 11 or higher
- **JPA Provider**: Hibernate 5.4+, EclipseLink 2.7+, or any JPA 2.2+ compatible provider
- **Database**: Any database supporting BIGINT and INTEGER types

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup

```bash
# Clone the repository
git clone https://github.com/boava/jpa-temporal.git
cd jpa-temporal

# Run tests
mvn clean verify

# Build project
mvn clean install
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Inspired by real-world JPA temporal handling challenges
- Built with modern Java practices and comprehensive testing
- Community-driven development and feedback

## 📞 Support

- 📖 [Documentation](https://github.com/boava/jpa-temporal/wiki)
- 🐛 [Issue Tracker](https://github.com/boava/jpa-temporal/issues)
- 💬 [Discussions](https://github.com/boava/jpa-temporal/discussions)

---

**Made with ❤️ by the Boava Team**
