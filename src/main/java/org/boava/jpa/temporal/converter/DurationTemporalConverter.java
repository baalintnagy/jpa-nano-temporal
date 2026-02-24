package org.boava.jpa.temporal.converter;

import java.time.Duration;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.boava.jpa.temporal.embeddable.EmbeddableTemporal;

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
 * @author Boava Team
 * @since 1.0.0
 */
@Converter(autoApply = false)
public class DurationTemporalConverter implements AttributeConverter<Duration, EmbeddableTemporal> {

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
