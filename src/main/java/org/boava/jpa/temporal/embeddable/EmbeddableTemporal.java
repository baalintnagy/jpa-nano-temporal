package org.boava.jpa.temporal.embeddable;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * A JPA embeddable type for storing temporal values with nanosecond precision across different databases.
 * <p>
 * This class addresses the inconsistent handling of {@link Instant} and {@link Duration} types
 * across different database systems:
 * <ul>
 *   <li>MariaDB/H2: Store only second precision, losing nanoseconds</li>
 *   <li>PostgreSQL: Uses INTERVAL type with different semantics</li>
 *   <li>General: Portability issues and precision loss</li>
 * </ul>
 * <p>
 * By storing temporal values as separate seconds and nanoseconds components, this class ensures:
 * <ul>
 *   <li>Consistent behavior across all supported databases</li>
 *   <li>Full nanosecond precision preservation</li>
 *   <li>Type safety with proper conversions</li>
 *   <li>Database-agnostic storage</li>
 * </ul>
 * <p>
 * Example usage in an entity:
 * 
 * @author baalintnagy
 * @since 0.9.0
 */
@Embeddable
public class EmbeddableTemporal implements Comparable<EmbeddableTemporal>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Number of nanoseconds in one second.
     */
    public static final int NANOS_PER_SECOND = 1_000_000_000;

    /**
     * Maximum nanoseconds value (one less than a full second).
     */
    public static final int MAX_NANOS = NANOS_PER_SECOND - 1;

    /**
     * Number of seconds in one day.
     */
    public static final int SECONDS_PER_DAY = 86_400;

    private long seconds;
    private int nanos;

    /**
     * Default constructor required by JPA.
     */
    public EmbeddableTemporal() {
    }

    /**
     * Constructs an EmbeddableTemporal with the specified seconds and nanoseconds.
     * The values will be normalized to ensure nanos is in the range 0..999,999,999.
     * 
     * @param seconds the seconds component
     * @param nanos the nanoseconds component
     */
    public EmbeddableTemporal(long seconds, int nanos) {
        this.seconds = seconds;
        this.nanos = nanos;
        normalize();
    }

    /**
     * Gets the seconds component of this temporal value.
     * 
     * @return the seconds component
     */
    @Column(name = "seconds", nullable = false)
    public long getSeconds() {
        return seconds;
    }

    /**
     * Sets the seconds component of this temporal value.
     * The internal representation will be normalized after setting.
     * 
     * @param seconds the seconds component to set
     */
    public void setSeconds(long seconds) {
        this.seconds = seconds;
        normalize();
    }

    /**
     * Gets the nanoseconds component of this temporal value.
     * 
     * @return the nanoseconds component (always 0..999,999,999)
     */
    @Column(name = "nanos", nullable = false)
    public int getNanos() {
        return nanos;
    }

    /**
     * Sets the nanoseconds component of this temporal value.
     * The internal representation will be normalized after setting.
     * 
     * @param nanos the nanoseconds component to set
     */
    public void setNanos(int nanos) {
        this.nanos = nanos;
        normalize();
    }

    /**
     * Functional interface for converting seconds and nanoseconds to a target temporal type.
     * 
     * @param <T> the target temporal type
     */
    @FunctionalInterface
    public interface TemporalConverter<T> {
        /**
         * Converts seconds and nanoseconds to the target temporal type.
         * 
         * @param seconds the seconds component
         * @param nanos the nanoseconds component
         * @return the converted temporal value
         */
        T convert(long seconds, int nanos);
    }

    /**
     * Converts this temporal value to any temporal type using the provided converter.
     * 
     * @param <T> the target temporal type
     * @param converter the converter function
     * @return the converted temporal value
     * @throws NullPointerException if converter is null
     */
    public <T> T convert(TemporalConverter<T> converter) {
        Objects.requireNonNull(converter, "Converter cannot be null");
        return converter.convert(seconds, nanos);
    }

    /**
     * Normalizes the internal representation to ensure that nanos is always in
     * the range 0..999,999,999 and seconds is adjusted accordingly.
     */
    protected void normalize() {
        if (nanos >= 0 && nanos < NANOS_PER_SECOND) {
            return;
        }
        
        if (nanos >= NANOS_PER_SECOND) {
            // Positive overflow: add extra seconds
            long carry = nanos / NANOS_PER_SECOND;
            this.seconds += carry;
            this.nanos = nanos % NANOS_PER_SECOND;
        } else {
            // Negative nanoseconds: borrow from seconds
            // Handle negative nanos properly
            long absNanos = -(long) nanos; // Convert to positive for calculation
            long carry = (absNanos + MAX_NANOS) / NANOS_PER_SECOND; // ceiling division
            this.seconds -= carry;
            this.nanos += carry * NANOS_PER_SECOND;
        }
    }

    @Override
    public int compareTo(EmbeddableTemporal other) {
        int secondsComparison = Long.compare(this.seconds, other.seconds);
        return (secondsComparison != 0) ? secondsComparison : Integer.compare(this.nanos, other.nanos);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EmbeddableTemporal that = (EmbeddableTemporal) obj;
        return seconds == that.seconds && nanos == that.nanos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(seconds, nanos);
    }

    @Override
    public String toString() {
        return "EmbeddableTemporal{seconds=%d, nanos=%d}".formatted(seconds, nanos);
    }

    // Factory methods for common temporal types

    /**
     * Creates an EmbeddableTemporal from an {@link Instant}.
     * 
     * @param instant the instant to convert
     * @return the EmbeddableTemporal representation
     * @throws NullPointerException if instant is null
     */
    public static EmbeddableTemporal from(Instant instant) {
        Objects.requireNonNull(instant, "Instant cannot be null");
        return new EmbeddableTemporal(instant.getEpochSecond(), instant.getNano());
    }

    /**
     * Creates an EmbeddableTemporal from a {@link Duration}.
     * 
     * @param duration the duration to convert
     * @return the EmbeddableTemporal representation
     * @throws NullPointerException if duration is null
     */
    public static EmbeddableTemporal from(Duration duration) {
        Objects.requireNonNull(duration, "Duration cannot be null");
        return new EmbeddableTemporal(duration.getSeconds(), duration.getNano());
    }

    // Conversion methods to common temporal types

    /**
     * Converts this EmbeddableTemporal to an {@link Instant}.
     * 
     * @return the Instant representation
     */
    public Instant toInstant() {
        return Instant.ofEpochSecond(seconds, nanos);
    }

    /**
     * Converts this EmbeddableTemporal to a {@link Duration}.
     * 
     * @return the Duration representation
     */
    public Duration toDuration() {
        return Duration.ofSeconds(seconds, nanos);
    }

    /**
     * Converts this EmbeddableTemporal to a {@link LocalDateTime} using the system default timezone.
     * 
     * @return the LocalDateTime representation
     */
    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.ofEpochSecond(seconds, nanos, java.time.ZoneOffset.UTC);
    }

    /**
     * Converts this EmbeddableTemporal to a {@link ZonedDateTime} using UTC timezone.
     * 
     * @return the ZonedDateTime representation
     */
    public ZonedDateTime toZonedDateTime() {
        return ZonedDateTime.ofInstant(toInstant(), java.time.ZoneOffset.UTC);
    }

    /**
     * Converts this EmbeddableTemporal to an {@link OffsetDateTime} using UTC offset.
     * 
     * @return the OffsetDateTime representation
     */
    public OffsetDateTime toOffsetDateTime() {
        return OffsetDateTime.ofInstant(toInstant(), java.time.ZoneOffset.UTC);
    }

    /**
     * Converts this EmbeddableTemporal to a {@link LocalTime} using the total temporal value.
     * 
     * @return the LocalTime representation
     */
    public LocalTime toLocalTime() {
        long secondsInDay = ((seconds % SECONDS_PER_DAY) + SECONDS_PER_DAY) % SECONDS_PER_DAY; // normalize to 0-86,399
        long totalNanos = secondsInDay * NANOS_PER_SECOND + nanos;
        return LocalTime.ofNanoOfDay(totalNanos);
    }

    // Utility methods

    /**
     * Returns true if this temporal value represents zero (both seconds and nanos are zero).
     * 
     * @return true if this temporal value is zero
     */
    public boolean isZero() {
        return seconds == 0L && nanos == 0;
    }

}
