package io.temporal.precision.converter;

import java.time.Instant;
import java.time.Duration;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import io.temporal.precision.embeddable.EmbeddableTemporal;

/**
 * JPA AttributeConverter for converting between {@link Instant} and {@link EmbeddableTemporal}.
 * <p>
 * This converter allows you to use {@link Instant} directly in your entity classes while
 * still benefiting from the nanosecond precision and database compatibility of {@link EmbeddableTemporal}.
 * <p>
 * Example usage:
 * <pre>{@code
 * @Entity
 * public class Event {
 *     @Id
 *     private Long id;
 *     
 *     @Convert(converter = InstantTemporalConverter.class)
 *     @Column(name = "event_timestamp_seconds")
 *     private Instant timestamp;
 * }
 * }</pre>
 * <p>
 * <strong>Note:</strong> This converter requires two database columns to be defined:
 * one for seconds and one for nanoseconds. Use {@code @AttributeOverride} on the
 * {@link EmbeddableTemporal} class for more control over column naming.
 * 
 * @author Temporal Precision Team
 * @since 1.0.0
 */
@Converter(autoApply = false)
public class InstantTemporalConverter implements AttributeConverter<Instant, EmbeddableTemporal> {

    @Override
    public EmbeddableTemporal convertToDatabaseColumn(Instant attribute) {
        if (attribute == null) {
            return null;
        }
        return EmbeddableTemporal.from(attribute);
    }

    @Override
    public Instant convertToEntityAttribute(EmbeddableTemporal dbData) {
        if (dbData == null) {
            return null;
        }
        return dbData.toInstant();
    }
}

/**
 * JPA AttributeConverter for converting between {@link Duration} and {@link EmbeddableTemporal}.
 * <p>
 * This converter allows you to use {@link Duration} directly in your entity classes while
 * still benefiting from the nanosecond precision and database compatibility of {@link EmbeddableTemporal}.
 * <p>
 * Example usage:
 * <pre>{@code
 * @Entity
 * public class Process {
 *     @Id
 *     private Long id;
 *     
 *     @Convert(converter = DurationTemporalConverter.class)
 *     @Column(name = "processing_duration_seconds")
 *     private Duration duration;
 * }
 * }</pre>
 * 
 * @author Temporal Precision Team
 * @since 1.0.0
 */
@Converter(autoApply = false)
class DurationTemporalConverter implements AttributeConverter<Duration, EmbeddableTemporal> {

    @Override
    public EmbeddableTemporal convertToDatabaseColumn(Duration attribute) {
        if (attribute == null) {
            return null;
        }
        return EmbeddableTemporal.from(attribute);
    }

    @Override
    public Duration convertToEntityAttribute(EmbeddableTemporal dbData) {
        if (dbData == null) {
            return null;
        }
        return dbData.toDuration();
    }
}
