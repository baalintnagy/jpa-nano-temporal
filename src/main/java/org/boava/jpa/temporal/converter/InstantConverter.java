package org.boava.jpa.temporal.converter;

import java.time.Instant;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.boava.jpa.temporal.embeddable.EmbeddableTemporal;

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
 *     @Convert(converter = InstantConverter.class)
 *     @Column(name = "event_timestamp_seconds")
 *     private Instant timestamp;
 * }
 * }</pre>
 * <p>
 * <strong>Note:</strong> This converter requires two database columns to be defined:
 * one for seconds and one for nanoseconds. Use {@code @AttributeOverride} on the
 * {@link EmbeddableTemporal} class for more control over column naming.
 * 
 * @author Boava Team
 * @since 1.0.0
 */
@Converter(autoApply = false)
public class InstantConverter implements AttributeConverter<Instant, EmbeddableTemporal> {

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
