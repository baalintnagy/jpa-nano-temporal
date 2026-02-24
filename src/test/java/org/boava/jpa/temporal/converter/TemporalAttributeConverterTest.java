package org.boava.jpa.temporal.converter;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.boava.jpa.temporal.embeddable.EmbeddableTemporal;
import static org.boava.jpa.temporal.test.TestConstants.*;
import static java.time.Duration.ofSeconds;
import static java.time.Instant.ofEpochSecond;

@DisplayName("TemporalAttributeConverter Tests")
class TemporalAttributeConverterTest {

    private InstantConverter instantConverter;
    private DurationConverter durationConverter;

    @BeforeEach
    void setUp() {
        instantConverter = new InstantConverter();
        durationConverter = new DurationConverter();
    }

    @Nested
    @DisplayName("InstantConverter Tests")
    class InstantConverterTests {

        @Test
        @DisplayName("Should convert Instant to EmbeddableTemporal")
        void shouldConvertInstantToEmbeddableTemporal() {
            Instant instant = STANDARD_INSTANT;
            
            EmbeddableTemporal result = instantConverter.convertToDatabaseColumn(instant);
            
            assertThat(result).isNotNull();
            assertThat(result.getSeconds()).isEqualTo(STANDARD_SECONDS);
            assertThat(result.getNanos()).isEqualTo(STANDARD_NANOS);
        }

        @Test
        @DisplayName("Should convert null Instant to null EmbeddableTemporal")
        void shouldConvertNullInstantToNullEmbeddableTemporal() {
            EmbeddableTemporal result = instantConverter.convertToDatabaseColumn(null);
            
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should convert EmbeddableTemporal to Instant")
        void shouldConvertEmbeddableTemporalToInstant() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(STANDARD_SECONDS, STANDARD_NANOS);
            
            Instant result = instantConverter.convertToEntityAttribute(temporal);
            
            assertThat(result).isNotNull();
            assertThat(result.getEpochSecond()).isEqualTo(STANDARD_SECONDS);
            assertThat(result.getNano()).isEqualTo(STANDARD_NANOS);
        }

        @Test
        @DisplayName("Should convert null EmbeddableTemporal to null Instant")
        void shouldConvertNullEmbeddableTemporalToNullInstant() {
            Instant result = instantConverter.convertToEntityAttribute(null);
            
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should maintain precision through round-trip conversion")
        void shouldMaintainPrecisionThroughRoundTripConversion() {
            Instant original = ofEpochSecond(STANDARD_SECONDS, 987654321);
            
            EmbeddableTemporal dbData = instantConverter.convertToDatabaseColumn(original);
            Instant roundTrip = instantConverter.convertToEntityAttribute(dbData);
            
            assertThat(roundTrip).isEqualTo(original);
        }
    }

    @Nested
    @DisplayName("DurationConverter Tests")
    class DurationConverterTests {

        @Test
        @DisplayName("Should convert Duration to EmbeddableTemporal")
        void shouldConvertDurationToEmbeddableTemporal() {
            Duration duration = ALT_DURATION;
            
            EmbeddableTemporal result = durationConverter.convertToDatabaseColumn(duration);
            
            assertThat(result).isNotNull();
            assertThat(result.getSeconds()).isEqualTo(ALT_SECONDS);
            assertThat(result.getNanos()).isEqualTo(ALT_NANOS);
        }

        @Test
        @DisplayName("Should convert null Duration to null EmbeddableTemporal")
        void shouldConvertNullDurationToNullEmbeddableTemporal() {
            EmbeddableTemporal result = durationConverter.convertToDatabaseColumn(null);
            
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should convert EmbeddableTemporal to Duration")
        void shouldConvertEmbeddableTemporalToDuration() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(ALT_SECONDS, ALT_NANOS);
            
            Duration result = durationConverter.convertToEntityAttribute(temporal);
            
            assertThat(result).isNotNull();
            assertThat(result.getSeconds()).isEqualTo(ALT_SECONDS);
            assertThat(result.getNano()).isEqualTo(ALT_NANOS);
        }

        @Test
        @DisplayName("Should convert null EmbeddableTemporal to null Duration")
        void shouldConvertNullEmbeddableTemporalToNullDuration() {
            Duration result = durationConverter.convertToEntityAttribute(null);
            
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should maintain precision through round-trip conversion")
        void shouldMaintainPrecisionThroughRoundTripConversion() {
            Duration original = ofSeconds(ALT_SECONDS, STANDARD_NANOS);
            
            EmbeddableTemporal dbData = durationConverter.convertToDatabaseColumn(original);
            Duration roundTrip = durationConverter.convertToEntityAttribute(dbData);
            
            assertThat(roundTrip).isEqualTo(original);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle epoch Instant")
        void shouldHandleEpochInstant() {
            Instant epoch = Instant.EPOCH;
            
            EmbeddableTemporal dbData = instantConverter.convertToDatabaseColumn(epoch);
            Instant roundTrip = instantConverter.convertToEntityAttribute(dbData);
            
            assertThat(roundTrip).isEqualTo(epoch);
            assertThat(dbData.isZero()).isTrue();
        }

        @Test
        @DisplayName("Should handle zero Duration")
        void shouldHandleZeroDuration() {
            Duration zero = Duration.ZERO;
            
            EmbeddableTemporal dbData = durationConverter.convertToDatabaseColumn(zero);
            Duration roundTrip = durationConverter.convertToEntityAttribute(dbData);
            
            assertThat(roundTrip).isEqualTo(zero);
            assertThat(dbData.isZero()).isTrue();
        }

        @Test
        @DisplayName("Should handle maximum precision values")
        void shouldHandleMaximumPrecisionValues() {
            Instant maxPrecision = MAX_INSTANT;
            
            EmbeddableTemporal dbData = instantConverter.convertToDatabaseColumn(maxPrecision);
            Instant roundTrip = instantConverter.convertToEntityAttribute(dbData);
            
            assertThat(roundTrip).isEqualTo(maxPrecision);
        }

        @Test
        @DisplayName("Should handle negative values")
        void shouldHandleNegativeValues() {
            Instant negative = NEGATIVE_INSTANT;
            Duration negativeDuration = NEGATIVE_DURATION;
            
            // Test Instant
            EmbeddableTemporal instantDbData = instantConverter.convertToDatabaseColumn(negative);
            Instant instantRoundTrip = instantConverter.convertToEntityAttribute(instantDbData);
            assertThat(instantRoundTrip).isEqualTo(negative);
            
            // Test Duration
            EmbeddableTemporal durationDbData = durationConverter.convertToDatabaseColumn(negativeDuration);
            Duration durationRoundTrip = durationConverter.convertToEntityAttribute(durationDbData);
            assertThat(durationRoundTrip).isEqualTo(negativeDuration);
        }
    }
}
