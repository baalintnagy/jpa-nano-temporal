package org.boava.jpa.temporal.integration;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Duration;
import java.time.Instant;

import org.boava.jpa.temporal.converter.DurationTemporalConverter;
import org.boava.jpa.temporal.converter.InstantTemporalConverter;

/**
 * Test entity for integration testing of EmbeddableTemporal and converters.
 * This entity uses converters for Instant and Duration fields.
 */
@Entity
@Table(name = "test_entities")
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    // Using converter for Instant - this will map to two columns: seconds and nanos
    @Convert(converter = InstantTemporalConverter.class)
    @AttributeOverrides({
        @AttributeOverride(name = "seconds", column = @Column(name = "timestamp_seconds")),
        @AttributeOverride(name = "nanos", column = @Column(name = "timestamp_nanos"))
    })
    private Instant timestamp;

    // Using converter for Duration - this will map to two columns: seconds and nanos
    @Convert(converter = DurationTemporalConverter.class)
    @AttributeOverrides({
        @AttributeOverride(name = "seconds", column = @Column(name = "duration_seconds")),
        @AttributeOverride(name = "nanos", column = @Column(name = "duration_nanos"))
    })
    private Duration duration;

    // Default constructor required by JPA
    public TestEntity() {
    }

    public TestEntity(String name, String description, Instant timestamp, Duration duration) {
        this.name = name;
        this.description = description;
        this.timestamp = timestamp;
        this.duration = duration;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestEntity testEntity = (TestEntity) o;

        if (id != null ? !id.equals(testEntity.id) : testEntity.id != null) return false;
        if (name != null ? !name.equals(testEntity.name) : testEntity.name != null) return false;
        if (timestamp != null ? !timestamp.equals(testEntity.timestamp) : testEntity.timestamp != null) return false;
        if (duration != null ? !duration.equals(testEntity.duration) : testEntity.duration != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", timestamp=" + timestamp +
                ", duration=" + duration +
                '}';
    }
}
