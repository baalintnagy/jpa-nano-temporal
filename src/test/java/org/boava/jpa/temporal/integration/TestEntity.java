package org.boava.jpa.temporal.integration;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Duration;
import java.time.Instant;

import org.boava.jpa.temporal.embeddable.EmbeddableTemporal;

/**
 * Test entity for integration testing of EmbeddableTemporal.
 * This entity uses EmbeddableTemporal directly for full JPQL support.
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

    // Using EmbeddableTemporal directly for full JPQL support
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "seconds", column = @Column(name = "timestamp_seconds")),
        @AttributeOverride(name = "nanos", column = @Column(name = "timestamp_nanos"))
    })
    private EmbeddableTemporal timestamp;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "seconds", column = @Column(name = "duration_seconds")),
        @AttributeOverride(name = "nanos", column = @Column(name = "duration_nanos"))
    })
    private EmbeddableTemporal duration;

    // Default constructor required by JPA
    public TestEntity() {
    }

    public TestEntity(String name, String description, Instant timestamp, Duration duration) {
        this.name = name;
        this.description = description;
        this.timestamp = timestamp != null ? EmbeddableTemporal.from(timestamp) : null;
        this.duration = duration != null ? EmbeddableTemporal.from(duration) : null;
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

    public EmbeddableTemporal getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(EmbeddableTemporal timestamp) {
        this.timestamp = timestamp;
    }

    public EmbeddableTemporal getDuration() {
        return duration;
    }

    public void setDuration(EmbeddableTemporal duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "TestEntity{id=%s, name='%s', description='%s', timestamp=%s, duration=%s}"
            .formatted(id, name, description, timestamp, duration);
    }
}
