package org.boava.jpa.temporal.converter;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.boava.jpa.temporal.embeddable.EmbeddableTemporal;

@DisplayName("TemporalAttributeConverter Tests")
class TemporalAttributeConverterTest {

    private InstantTemporalConverter instantConverter;
    private DurationTemporalConverter durationConverter;

    @BeforeEach
    void setUp() {
        instantConverter = new InstantTemporalConverter();
        durationConverter = new DurationTemporalConverter();
    }

    @Nested
    @DisplayName("InstantTemporalConverter Tests")
    class InstantTemporalConverterTests {

        @Test
        @DisplayName("Should convert Instant to EmbeddableTemporal")
        void shouldConvertInstantToEmbeddableTemporal() {
            Instant instant = Instant.ofEpochSecond(123456789, 123456789);
            
            EmbeddableTemporal result = instantConverter.convertToDatabaseColumn(instant);
            
            assertThat(result).isNotNull();
            assertThat(result.getSeconds()).isEqualTo(123456789);
            assertThat(result.getNanos()).isEqualTo(123456789);
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
            EmbeddableTemporal temporal = new EmbeddableTemporal(123456789, 123456789);
            
            Instant result = instantConverter.convertToEntityAttribute(temporal);
            
            assertThat(result).isNotNull();
            assertThat(result.getEpochSecond()).isEqualTo(123456789);
            assertThat(result.getNano()).isEqualTo(123456789);
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
            Instant original = Instant.ofEpochSecond(123456789, 987654321);
            
            EmbeddableTemporal dbData = instantConverter.convertToDatabaseColumn(original);
            Instant roundTrip = instantConverter.convertToEntityAttribute(dbData);
            
            assertThat(roundTrip).isEqualTo(original);
        }
    }

    @Nested
    @DisplayName("DurationTemporalConverter Tests")
    class DurationTemporalConverterTests {

        @Test
        @DisplayName("Should convert Duration to EmbeddableTemporal")
        void shouldConvertDurationToEmbeddableTemporal() {
            Duration duration = Duration.ofSeconds(98765, 987654321);
            
            EmbeddableTemporal result = durationConverter.convertToDatabaseColumn(duration);
            
            assertThat(result).isNotNull();
            assertThat(result.getSeconds()).isEqualTo(98765);
            assertThat(result.getNanos()).isEqualTo(987654321);
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
            EmbeddableTemporal temporal = new EmbeddableTemporal(98765, 987654321);
            
            Duration result = durationConverter.convertToEntityAttribute(temporal);
            
            assertThat(result).isNotNull();
            assertThat(result.getSeconds()).isEqualTo(98765);
            assertThat(result.getNano()).isEqualTo(987654321);
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
            Duration original = Duration.ofSeconds(98765, 123456789);
            
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
            Instant maxPrecision = Instant.ofEpochSecond(Long.MAX_VALUE, 999_999_999);
            
            EmbeddableTemporal dbData = instantConverter.convertToDatabaseColumn(maxPrecision);
            Instant roundTrip = instantConverter.convertToEntityAttribute(dbData);
            
            assertThat(roundTrip).isEqualTo(maxPrecision);
        }

        @Test
        @DisplayName("Should handle negative values")
        void shouldHandleNegativeValues() {
            Instant negative = Instant.ofEpochSecond(-123456789, -123456789);
            Duration negativeDuration = Duration.ofSeconds(-123456789, -123456789);
            
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
