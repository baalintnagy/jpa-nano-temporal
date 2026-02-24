package org.boava.jpa.temporal.test;

import java.time.Duration;
import java.time.Instant;

import static java.time.Duration.ofSeconds;
import static java.time.Instant.ofEpochSecond;
import static org.boava.jpa.temporal.embeddable.EmbeddableTemporal.MAX_NANOS;

/**
 * Constants used across temporal converter tests.
 * This class centralizes test data to avoid duplication and improve maintainability.
 */
public final class TestConstants {

    private TestConstants() {
        // Utility class - prevent instantiation
    }

    // Standard test values
    public static final long STANDARD_SECONDS = 123456789L;
    public static final int STANDARD_NANOS = 123456789;
    
    public static final long ALT_SECONDS = 98765L;
    public static final int ALT_NANOS = 987654321;
    
    // Edge case values
    public static final long NEGATIVE_SECONDS = -123456789L;
    public static final int NEGATIVE_NANOS = -123456789;
    
    public static final long MAX_INSTANT_SECONDS = 31556889864403199L;

    public static final long ZERO_SECONDS = 0L;
    public static final int ZERO_NANOS = 0;

    public static final long ONE_SECONDS = 1L;
    public static final int ONE_NANOS = 1;

    public static final long HUNDRED_SECONDS = 100L;
    
    // Common nanosecond values
    public static final int HUNDRED_MILLION_NANOS = 100_000_000;
    public static final int FOUR_HUNDRED_MILLION_NANOS = 4 * HUNDRED_MILLION_NANOS;
    public static final int FIVE_HUNDRED_MILLION_NANOS = 5 * HUNDRED_MILLION_NANOS;
    public static final int SIX_HUNDRED_MILLION_NANOS = 6 * HUNDRED_MILLION_NANOS;
    public static final int NINE_HUNDRED_MILLION_NANOS = 9 * HUNDRED_MILLION_NANOS;
    public static final int ONE_BILLION_NANOS = 10 * HUNDRED_MILLION_NANOS;
    public static final int ONE_AND_HALF_BILLION_NANOS = 15 * HUNDRED_MILLION_NANOS;
    
    // Pre-created temporal objects
    public static final Instant STANDARD_INSTANT = ofEpochSecond(STANDARD_SECONDS, STANDARD_NANOS);
    public static final Instant ALT_INSTANT = ofEpochSecond(ALT_SECONDS, ALT_NANOS);
    public static final Instant SIMPLE_INSTANT = ofEpochSecond(HUNDRED_SECONDS, FIVE_HUNDRED_MILLION_NANOS);
    public static final Instant NEGATIVE_INSTANT = ofEpochSecond(NEGATIVE_SECONDS, NEGATIVE_NANOS);
    public static final Instant MAX_INSTANT = ofEpochSecond(MAX_INSTANT_SECONDS, MAX_NANOS);
    
    public static final Duration STANDARD_DURATION = ofSeconds(STANDARD_SECONDS, STANDARD_NANOS);
    public static final Duration ALT_DURATION = ofSeconds(ALT_SECONDS, ALT_NANOS);
    public static final Duration SIMPLE_DURATION = ofSeconds(HUNDRED_SECONDS, FIVE_HUNDRED_MILLION_NANOS);
    public static final Duration NEGATIVE_DURATION = ofSeconds(NEGATIVE_SECONDS, NEGATIVE_NANOS);
    public static final Duration MAX_DURATION = ofSeconds(MAX_INSTANT_SECONDS, MAX_NANOS);
    
    }
