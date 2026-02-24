package org.boava.jpa.temporal.embeddable;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.boava.jpa.temporal.test.TestConstants.*;
import static java.time.Duration.ofSeconds;
import static java.time.Instant.ofEpochSecond;
import java.util.stream.Stream;

@DisplayName("EmbeddableTemporal Tests")
class EmbeddableTemporalTest {

    /**
     * Test case record for normalization tests.
     */
    record NormalizationTestCase(long inputSeconds, int inputNanos, long expectedSeconds, int expectedNanos) {}

    /**
     * Provides test data for nanosecond normalization tests.
     */
    static Stream<NormalizationTestCase> provideNormalizationTestData() {
        return Stream.of(
            new NormalizationTestCase(ZERO_SECONDS, ZERO_NANOS, ZERO_SECONDS, ZERO_NANOS),
            new NormalizationTestCase(HUNDRED_SECONDS, MAX_NANOS, HUNDRED_SECONDS, MAX_NANOS),
            new NormalizationTestCase(HUNDRED_SECONDS, ONE_BILLION_NANOS, 101L, ZERO_NANOS),
            new NormalizationTestCase(HUNDRED_SECONDS, ONE_AND_HALF_BILLION_NANOS, 101L, FIVE_HUNDRED_MILLION_NANOS),
            new NormalizationTestCase(HUNDRED_SECONDS, -ONE_NANOS, 99, MAX_NANOS),
            new NormalizationTestCase(HUNDRED_SECONDS, -MAX_NANOS, 99L, ONE_NANOS),
            new NormalizationTestCase(HUNDRED_SECONDS, -ONE_BILLION_NANOS, 99L, ZERO_NANOS),
            new NormalizationTestCase(HUNDRED_SECONDS, -ONE_AND_HALF_BILLION_NANOS, 98L, FIVE_HUNDRED_MILLION_NANOS)
        );
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with zero values")
        void shouldCreateInstanceWithZeroValues() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(ZERO_SECONDS, ZERO_NANOS);
            
            assertThat(temporal.getSeconds()).isZero();
            assertThat(temporal.getNanos()).isZero();
            assertThat(temporal.isZero()).isTrue();
        }

        @Test
        @DisplayName("Should create instance with positive values")
        void shouldCreateInstanceWithPositiveValues() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(SIMPLE_SECONDS, SIMPLE_NANOS);
            
            assertThat(temporal.getSeconds()).isEqualTo(SIMPLE_SECONDS);
            assertThat(temporal.getNanos()).isEqualTo(SIMPLE_NANOS);
            assertThat(temporal.isPositive()).isTrue();
            assertThat(temporal.isNegative()).isFalse();
        }

        @Test
        @DisplayName("Should normalize nanoseconds greater than one second")
        void shouldNormalizeNanosecondsGreaterThanOneSecond() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(HUNDRED_SECONDS, ONE_AND_HALF_BILLION_NANOS);
            
            assertThat(temporal.getSeconds()).isEqualTo(101L);
            assertThat(temporal.getNanos()).isEqualTo(FIVE_HUNDRED_MILLION_NANOS);
        }

        @Test
        @DisplayName("Should normalize negative nanoseconds")
        void shouldNormalizeNegativeNanoseconds() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(HUNDRED_SECONDS, -FIVE_HUNDRED_MILLION_NANOS);
            
            assertThat(temporal.getSeconds()).isEqualTo(99L);
            assertThat(temporal.getNanos()).isEqualTo(FIVE_HUNDRED_MILLION_NANOS);
        }

        @Test
        @DisplayName("Should normalize large negative nanoseconds")
        void shouldNormalizeLargeNegativeNanoseconds() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(HUNDRED_SECONDS, -ONE_AND_HALF_BILLION_NANOS);
            
            assertThat(temporal.getSeconds()).isEqualTo(98L);
            assertThat(temporal.getNanos()).isEqualTo(FIVE_HUNDRED_MILLION_NANOS);
        }

        @ParameterizedTest
        @MethodSource("org.boava.jpa.temporal.embeddable.EmbeddableTemporalTest#provideNormalizationTestData")
        @DisplayName("Should normalize various nanosecond values")
        void shouldNormalizeVariousNanosecondValues(NormalizationTestCase testCase) {
            EmbeddableTemporal temporal = new EmbeddableTemporal(testCase.inputSeconds(), testCase.inputNanos());
            
            assertThat(temporal.getSeconds()).isEqualTo(testCase.expectedSeconds());
            assertThat(temporal.getNanos()).isEqualTo(testCase.expectedNanos());
        }

    }

    @Nested
    @DisplayName("Factory Method Tests")
    class FactoryMethodTests {

        @Test
        @DisplayName("Should create from Instant")
        void shouldCreateFromInstant() {
            Instant instant = STANDARD_INSTANT;
            EmbeddableTemporal temporal = EmbeddableTemporal.from(instant);
            
            assertThat(temporal.getSeconds()).isEqualTo(STANDARD_SECONDS);
            assertThat(temporal.getNanos()).isEqualTo(STANDARD_NANOS);
        }

        @Test
        @DisplayName("Should create from Duration")
        void shouldCreateFromDuration() {
            Duration duration = ALT_DURATION;
            EmbeddableTemporal temporal = EmbeddableTemporal.from(duration);
            
            assertThat(temporal.getSeconds()).isEqualTo(ALT_SECONDS);
            assertThat(temporal.getNanos()).isEqualTo(ALT_NANOS);
        }

        @Test
        @DisplayName("Should throw exception when creating from null Instant")
        void shouldThrowExceptionWhenCreatingFromNullInstant() {
            assertThatThrownBy(() -> EmbeddableTemporal.from((Instant) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Instant cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when creating from null Duration")
        void shouldThrowExceptionWhenCreatingFromNullDuration() {
            assertThatThrownBy(() -> EmbeddableTemporal.from((Duration) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Duration cannot be null");
        }
    }

    @Nested
    @DisplayName("Conversion Method Tests")
    class ConversionMethodTests {

        @Test
        @DisplayName("Should convert to Instant")
        void shouldConvertToInstant() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(STANDARD_SECONDS, STANDARD_NANOS);
            Instant instant = temporal.toInstant();
            
            assertThat(instant.getEpochSecond()).isEqualTo(STANDARD_SECONDS);
            assertThat(instant.getNano()).isEqualTo(STANDARD_NANOS);
        }

        @Test
        @DisplayName("Should convert to Duration")
        void shouldConvertToDuration() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(ALT_SECONDS, ALT_NANOS);
            Duration duration = temporal.toDuration();
            
            assertThat(duration.getSeconds()).isEqualTo(ALT_SECONDS);
            assertThat(duration.getNano()).isEqualTo(ALT_NANOS);
        }

        @Test
        @DisplayName("Should convert to LocalDateTime")
        void shouldConvertToLocalDateTime() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(STANDARD_SECONDS, STANDARD_NANOS);
            LocalDateTime localDateTime = temporal.toLocalDateTime();
            
            assertThat(localDateTime.toEpochSecond(java.time.ZoneOffset.UTC)).isEqualTo(STANDARD_SECONDS);
            assertThat(localDateTime.getNano()).isEqualTo(STANDARD_NANOS);
        }

        @Test
        @DisplayName("Should convert to ZonedDateTime")
        void shouldConvertToZonedDateTime() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(STANDARD_SECONDS, STANDARD_NANOS);
            ZonedDateTime zonedDateTime = temporal.toZonedDateTime();
            
            assertThat(zonedDateTime.toEpochSecond()).isEqualTo(STANDARD_SECONDS);
            assertThat(zonedDateTime.getNano()).isEqualTo(STANDARD_NANOS);
            assertThat(zonedDateTime.getZone()).isEqualTo(java.time.ZoneOffset.UTC);
        }

        @Test
        @DisplayName("Should convert to OffsetDateTime")
        void shouldConvertToOffsetDateTime() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(STANDARD_SECONDS, STANDARD_NANOS);
            OffsetDateTime offsetDateTime = temporal.toOffsetDateTime();
            
            assertThat(offsetDateTime.toEpochSecond()).isEqualTo(STANDARD_SECONDS);
            assertThat(offsetDateTime.getNano()).isEqualTo(STANDARD_NANOS);
            assertThat(offsetDateTime.getOffset()).isEqualTo(java.time.ZoneOffset.UTC);
        }

        @Test
        @DisplayName("Should convert using custom converter")
        void shouldConvertUsingCustomConverter() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(SIMPLE_SECONDS, SIMPLE_NANOS);
            
            String result = temporal.convert((seconds, nanos) -> 
                String.format("%d seconds and %d nanoseconds", SIMPLE_SECONDS, SIMPLE_NANOS));
            
            assertThat(result).isEqualTo("100 seconds and 500000000 nanoseconds");
        }

        @Test
        @DisplayName("Should throw exception when using null converter")
        void shouldThrowExceptionWhenUsingNullConverter() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(SIMPLE_SECONDS, SIMPLE_NANOS);
            
            assertThatThrownBy(() -> temporal.convert(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Converter cannot be null");
        }
    }

    @Nested
    @DisplayName("Comparison Tests")
    class ComparisonTests {

        @Test
        @DisplayName("Should compare equal instances")
        void shouldCompareEqualInstances() {
            EmbeddableTemporal temporal1 = new EmbeddableTemporal(SIMPLE_SECONDS, SIMPLE_NANOS);
            EmbeddableTemporal temporal2 = new EmbeddableTemporal(SIMPLE_SECONDS, SIMPLE_NANOS);
            
            assertThat(temporal1.compareTo(temporal2)).isZero();
            assertThat(temporal1).isEqualTo(temporal2);
            assertThat(temporal1.hashCode()).isEqualTo(temporal2.hashCode());
        }

        @Test
        @DisplayName("Should compare instances with different seconds")
        void shouldCompareInstancesWithDifferentSeconds() {
            EmbeddableTemporal temporal1 = new EmbeddableTemporal(SIMPLE_SECONDS, SIMPLE_NANOS);
            EmbeddableTemporal temporal2 = new EmbeddableTemporal(SIMPLE_SECONDS + ONE_SECONDS, SIMPLE_NANOS);
            
            assertThat(temporal1.compareTo(temporal2)).isNegative();
            assertThat(temporal2.compareTo(temporal1)).isPositive();
        }

        @Test
        @DisplayName("Should compare instances with different nanoseconds")
        void shouldCompareInstancesWithDifferentNanoseconds() {
            EmbeddableTemporal temporal1 = new EmbeddableTemporal(SIMPLE_SECONDS, FOUR_HUNDRED_MILLION_NANOS);
            EmbeddableTemporal temporal2 = new EmbeddableTemporal(SIMPLE_SECONDS, SIMPLE_NANOS);
            
            assertThat(temporal1.compareTo(temporal2)).isNegative();
            assertThat(temporal2.compareTo(temporal1)).isPositive();
        }

        @Test
        @DisplayName("Should not equal null")
        void shouldNotEqualNull() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(SIMPLE_SECONDS, SIMPLE_NANOS);
            
            assertThat(temporal).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should not equal different class")
        void shouldNotEqualDifferentClass() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(SIMPLE_SECONDS, SIMPLE_NANOS);
            
            assertThat(temporal).isNotEqualTo("not a temporal");
        }
    }

    @Nested
    @DisplayName("Utility Method Tests")
    class UtilityMethodTests {

        @Test
        @DisplayName("Should correctly identify zero values")
        void shouldCorrectlyIdentifyZeroValues() {
            EmbeddableTemporal zero = new EmbeddableTemporal(ZERO_SECONDS, ZERO_NANOS);
            EmbeddableTemporal positive = new EmbeddableTemporal(ONE_SECONDS, ZERO_NANOS);
            EmbeddableTemporal negative = new EmbeddableTemporal(-ONE_SECONDS, ZERO_NANOS);
            
            assertThat(zero.isZero()).isTrue();
            assertThat(positive.isZero()).isFalse();
            assertThat(negative.isZero()).isFalse();
        }

        @Test
        @DisplayName("Should correctly identify positive values")
        void shouldCorrectlyIdentifyPositiveValues() {
            EmbeddableTemporal zero = new EmbeddableTemporal(ZERO_SECONDS, ZERO_NANOS);
            EmbeddableTemporal positiveSeconds = new EmbeddableTemporal(ONE_SECONDS, ZERO_NANOS);
            EmbeddableTemporal positiveNanos = new EmbeddableTemporal(ZERO_SECONDS, ONE_NANOS);
            EmbeddableTemporal negative = new EmbeddableTemporal(-ONE_SECONDS, ZERO_NANOS);
            
            assertThat(zero.isPositive()).isFalse();
            assertThat(positiveSeconds.isPositive()).isTrue();
            assertThat(positiveNanos.isPositive()).isTrue();
            assertThat(negative.isPositive()).isFalse();
        }

        @Test
        @DisplayName("Should correctly identify negative values")
        void shouldCorrectlyIdentifyNegativeValues() {
            EmbeddableTemporal zero = new EmbeddableTemporal(ZERO_SECONDS, ZERO_NANOS);
            EmbeddableTemporal positive = new EmbeddableTemporal(ONE_SECONDS, ZERO_NANOS);
            EmbeddableTemporal negativeSeconds = new EmbeddableTemporal(-ONE_SECONDS, ZERO_NANOS);
            EmbeddableTemporal negativeNanos = new EmbeddableTemporal(ZERO_SECONDS, -ONE_NANOS);
            
            assertThat(zero.isNegative()).isFalse();
            assertThat(positive.isNegative()).isFalse();
            assertThat(negativeSeconds.isNegative()).isTrue();
            assertThat(negativeNanos.isNegative()).isTrue();
        }

        @Test
        @DisplayName("Should add temporal values")
        void shouldAddTemporalValues() {
            EmbeddableTemporal temporal1 = new EmbeddableTemporal(SIMPLE_SECONDS, SIMPLE_NANOS);
            EmbeddableTemporal temporal2 = new EmbeddableTemporal(50L, SIX_HUNDRED_MILLION_NANOS);
            
            EmbeddableTemporal result = temporal1.add(temporal2);
            
            assertThat(result.getSeconds()).isEqualTo(151L);
            assertThat(result.getNanos()).isEqualTo(HUNDRED_MILLION_NANOS);
        }

        @Test
        @DisplayName("Should subtract temporal values")
        void shouldSubtractTemporalValues() {
            EmbeddableTemporal temporal1 = new EmbeddableTemporal(SIMPLE_SECONDS, SIMPLE_NANOS);
            EmbeddableTemporal temporal2 = new EmbeddableTemporal(50L, SIX_HUNDRED_MILLION_NANOS);
            
            EmbeddableTemporal result = temporal1.subtract(temporal2);
            
            assertThat(result.getSeconds()).isEqualTo(49L);
            assertThat(result.getNanos()).isEqualTo(NINE_HUNDRED_MILLION_NANOS);
        }

        @Test
        @DisplayName("Should calculate absolute value")
        void shouldCalculateAbsoluteValue() {
            EmbeddableTemporal positive = new EmbeddableTemporal(SIMPLE_SECONDS, SIMPLE_NANOS);
            EmbeddableTemporal negative = new EmbeddableTemporal(-SIMPLE_SECONDS, -SIMPLE_NANOS);
            
            EmbeddableTemporal positiveAbs = positive.abs();
            EmbeddableTemporal negativeAbs = negative.abs();
            
            assertThat(positiveAbs.getSeconds()).isEqualTo(SIMPLE_SECONDS);
            assertThat(positiveAbs.getNanos()).isEqualTo(SIMPLE_NANOS);
            assertThat(negativeAbs.getSeconds()).isEqualTo(SIMPLE_SECONDS);
            assertThat(negativeAbs.getNanos()).isEqualTo(SIMPLE_NANOS);
        }

        @Test
        @DisplayName("Should throw exception when adding null")
        void shouldThrowExceptionWhenAddingNull() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(SIMPLE_SECONDS, SIMPLE_NANOS);
            
            assertThatThrownBy(() -> temporal.add(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Other temporal value cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when subtracting null")
        void shouldThrowExceptionWhenSubtractingNull() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(SIMPLE_SECONDS, SIMPLE_NANOS);
            
            assertThatThrownBy(() -> temporal.subtract(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Other temporal value cannot be null");
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {

        @Test
        @DisplayName("Should set seconds and normalize")
        void shouldSetSecondsAndNormalize() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(SIMPLE_SECONDS, ONE_AND_HALF_BILLION_NANOS);
            
            assertThat(temporal.getSeconds()).isEqualTo(101L);
            assertThat(temporal.getNanos()).isEqualTo(FIVE_HUNDRED_MILLION_NANOS);
            
            temporal.setSeconds(200L);
            
            assertThat(temporal.getSeconds()).isEqualTo(200L);
            assertThat(temporal.getNanos()).isEqualTo(FIVE_HUNDRED_MILLION_NANOS);
        }

        @Test
        @DisplayName("Should set nanoseconds and normalize")
        void shouldSetNanosecondsAndNormalize() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(SIMPLE_SECONDS, SIMPLE_NANOS);
            
            temporal.setNanos(ONE_AND_HALF_BILLION_NANOS);
            
            assertThat(temporal.getSeconds()).isEqualTo(101L);
            assertThat(temporal.getNanos()).isEqualTo(FIVE_HUNDRED_MILLION_NANOS);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate correct string representation")
        void shouldGenerateCorrectStringRepresentation() {
            EmbeddableTemporal temporal = new EmbeddableTemporal(123L, 456_789_000);
            
            String result = temporal.toString();
            
            assertThat(result).isEqualTo("EmbeddableTemporal{seconds=123, nanos=456789000}");
        }
    }

    @Nested
    @DisplayName("Round-trip Conversion Tests")
    class RoundTripConversionTests {

        @ParameterizedTest
        @MethodSource("org.boava.jpa.temporal.embeddable.EmbeddableTemporalTest#provideInstants")
        @DisplayName("Should maintain precision through round-trip conversions")
        void shouldMaintainPrecisionThroughRoundTrips(Instant instant) {
            // Instant -> EmbeddableTemporal -> Instant
            EmbeddableTemporal temporal = EmbeddableTemporal.from(instant);
            Instant roundTripInstant = temporal.toInstant();
            
            assertThat(roundTripInstant).isEqualTo(instant);
            
            // Duration -> EmbeddableTemporal -> Duration
            Duration duration = ofSeconds(instant.getEpochSecond(), instant.getNano());
            EmbeddableTemporal temporalFromDuration = EmbeddableTemporal.from(duration);
            Duration roundTripDuration = temporalFromDuration.toDuration();
            
            assertThat(roundTripDuration).isEqualTo(duration);
        }
    }

    /**
     * Provides test instances for parameterized tests.
     */
    static Instant[] provideInstants() {
        return new Instant[] {
            Instant.EPOCH,
            ofEpochSecond(ZERO_SECONDS, ONE_NANOS),
            ofEpochSecond(ZERO_SECONDS, MAX_NANOS),
            ofEpochSecond(ONE_SECONDS, ZERO_NANOS),
            ofEpochSecond(STANDARD_SECONDS, STANDARD_NANOS),
            NEGATIVE_INSTANT,
            MAX_INSTANT,
            ofEpochSecond(-31557014167219200L, ZERO_NANOS) // Instant.MIN
        };
    }

}
