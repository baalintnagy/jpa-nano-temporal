# JPA Temporal

[![CI](https://github.com/baalintnagy/jpa-nano-temporal/workflows/CI/badge.svg)](https://github.com/baalintnagy/jpa-nano-temporal/actions)
[![codecov](https://codecov.io/gh/baalintnagy/jpa-nano-temporal/branch/main/graph/badge.svg)](https://codecov.io/gh/baalintnagy/jpa-nano-temporal)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.baalintnagy/jpa.nano-temporal.svg)](https://search.maven.org/search?q=g:io.github.baalintnagy%20AND%20a:jpa.nano-temporal)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A JPA embeddable type for storing temporal values with nanosecond precision across different databases.

## 🚀 Why This Library?

JPA/Hibernate has inconsistent handling of temporal types across different database systems:

| Database   | Instant Support | Duration Support | Precision Loss |
|------------|-----------------|------------------|----------------|
| MariaDB    | ❌ Second-only  | ❌ Second-only  | **Yes**        |
| H2         | ❌ Second-only  | ❌ Second-only  | **Yes**        |
| PostgreSQL | ✅ Native       | ✅ INTERVAL     | **No**         |
| MySQL      | ❌ Second-only  | ❌ Second-only  | **Yes**        |

This library solves these problems by:
- ✅ **Consistent behavior** across all supported databases
- ✅ **Full nanosecond precision** preservation
- ✅ **Type safety** with proper conversions
- ✅ **Database-agnostic** storage using primitive types

## 📦 Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.baalintnagy</groupId>
    <artifactId>jpa.nano-temporal</artifactId>
    <version>0.9.2b</version>
</dependency>
```

Or to your `build.gradle`:

```groovy
implementation 'io.github.baalintnagy:jpa.nano-temporal:0.9.2'
```

## Quick Start

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

**Note:** The library mandates storing temporal values as two database columns:
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

### JPQL Query Support

```java
// ✅ All these JPQL queries work perfectly
@Query("SELECT e FROM Event e WHERE e.timestamp.seconds > :seconds")
List<Event> findByTimestampSecondsGreaterThan(@Param("seconds") long seconds);

@Query("SELECT e FROM Event e WHERE e.timestamp.seconds = :seconds AND e.timestamp.nanos > :nanos")
List<Event> findByTimestampExact(@Param("seconds") long seconds, @Param("nanos") int nanos);

@Query("SELECT e FROM Event e ORDER BY e.timestamp.seconds DESC, e.timestamp.nanos DESC")
List<Event> findAllOrderByTimestampDesc();

@Query("SELECT e FROM Event e WHERE e.duration.seconds > 0")
List<Event> findByPositiveDuration();

// ✅ Method query generation also works
List<Event> findByTimestampSeconds(long seconds);
List<Event> findByDurationSecondsGreaterThan(long seconds);
```

**Note**: JPQL queries work with the explicit ("qualified") `seconds` and `nanos` components, not with the whole temporal object. See: any @Embedded @Embeddable

### Working with Temporal Values

```java
EmbeddableTemporal t1 = EmbeddableTemporal.from(Duration.ofSeconds(100, 500_000_000));
EmbeddableTemporal t2 = EmbeddableTemporal.from(Duration.ofSeconds(50, 600_000_000));

// Utility methods
boolean isZero = t1.isZero();

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
| `toLocalTime()` | `LocalTime` | Convert to LocalTime (time of day) |
| `convert(TemporalConverter<T>)` | `T` | Custom conversion |

### Utility Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `isZero()` | `boolean` | Check if value is zero |

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

- **Java**: 17 or higher
- **JPA Provider**: Hibernate 5.4+, EclipseLink 2.7+, or any JPA 2.2+ compatible provider
- **Database**: Any database supporting BIGINT and INTEGER types

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup

```bash
# Clone the repository
git clone https://github.com/baalintnagy/jpa-nano-temporal.git
cd jpa-nano-temporal

# Run tests
mvn clean verify

# Build project
mvn clean install
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Inspired by frustration: JDK supporting nanosecond precision while the RDBMS-s "under" ORM/JPA being "Russian roulette"
- It is a PoC, no harm intended

## 📞 Support

Depends on adoption...

- 📖 [Documentation](https://github.com/baalintnagy/jpa-nano-temporal/wiki)
- 🐛 [Issue Tracker](https://github.com/baalintnagy/jpa-nano-temporal/issues)
- 💬 [Discussions](https://github.com/baalintnagy/jpa-nano-temporal/discussions)

---
