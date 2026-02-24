package org.boava.jpa.temporal.integration;

import org.boava.jpa.temporal.integration.TestEntity;
import static org.assertj.core.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Temporal Converter Integration Tests")
class TemporalConverterIntegrationTest {

    private EntityManagerFactory emf;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("test-pu");
        em = emf.createEntityManager();
    }

    @AfterEach
    void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Nested
    @DisplayName("Instant Converter Integration Tests")
    class InstantConverterTests {

        @Test
        @DisplayName("Should persist and retrieve Instant with full precision")
        void shouldPersistAndRetrieveInstantWithFullPrecision() {
            Instant originalTimestamp = Instant.ofEpochSecond(123456789, 123456789);
            
            TestEntity event = new TestEntity(
                "Test Event", 
                "Test Description", 
                originalTimestamp, 
                Duration.ofHours(2)
            );

            // Persist the entity
            em.getTransaction().begin();
            em.persist(event);
            em.getTransaction().commit();
            em.clear(); // Clear persistence context

            // Retrieve the entity
            TestEntity retrieved = em.find(TestEntity.class, event.getId());

            assertThat(retrieved).isNotNull();
            assertThat(retrieved.getTimestamp()).isEqualTo(originalTimestamp);
            assertThat(retrieved.getTimestamp().getEpochSecond()).isEqualTo(123456789);
            assertThat(retrieved.getTimestamp().getNano()).isEqualTo(123456789);
        }

        @Test
        @DisplayName("Should handle null Instant values")
        void shouldHandleNullInstantValues() {
            TestEntity event = new TestEntity("Null Test", "Test with null timestamp", null, Duration.ofMinutes(30));

            em.getTransaction().begin();
            em.persist(event);
            em.getTransaction().commit();
            em.clear();

            TestEntity retrieved = em.find(TestEntity.class, event.getId());

            assertThat(retrieved).isNotNull();
            assertThat(retrieved.getTimestamp()).isNull();
        }

        @Test
        @DisplayName("Should maintain precision across multiple operations")
        void shouldMaintainPrecisionAcrossMultipleOperations() {
            Instant highPrecision = Instant.ofEpochSecond(987654321, 987654321);
            
            TestEntity event = new TestEntity(
                "Precision Test", 
                "Testing high precision", 
                highPrecision, 
                Duration.ofSeconds(12345, 678901234)
            );

            em.getTransaction().begin();
            em.persist(event);
            em.getTransaction().commit();
            
            // Update the entity
            em.getTransaction().begin();
            TestEntity managed = em.find(TestEntity.class, event.getId());
            managed.setName("Updated Precision Test");
            em.getTransaction().commit();
            em.clear();

            TestEntity retrieved = em.find(TestEntity.class, event.getId());

            assertThat(retrieved.getTimestamp()).isEqualTo(highPrecision);
            assertThat(retrieved.getName()).isEqualTo("Updated Precision Test");
        }
    }

    @Nested
    @DisplayName("Duration Converter Integration Tests")
    class DurationConverterTests {

        @Test
        @DisplayName("Should persist and retrieve Duration with full precision")
        void shouldPersistAndRetrieveDurationWithFullPrecision() {
            Duration originalDuration = Duration.ofSeconds(98765, 987654321);
            
            TestEntity event = new TestEntity(
                "Duration Test", 
                "Testing duration persistence", 
                Instant.now(), 
                originalDuration
            );

            em.getTransaction().begin();
            em.persist(event);
            em.getTransaction().commit();
            em.clear();

            TestEntity retrieved = em.find(TestEntity.class, event.getId());

            assertThat(retrieved).isNotNull();
            assertThat(retrieved.getDuration()).isEqualTo(originalDuration);
            assertThat(retrieved.getDuration().getSeconds()).isEqualTo(98765);
            assertThat(retrieved.getDuration().getNano()).isEqualTo(987654321);
        }

        @Test
        @DisplayName("Should handle null Duration values")
        void shouldHandleNullDurationValues() {
            TestEntity event = new TestEntity("Null Duration Test", "Test with null duration", Instant.now(), null);

            em.getTransaction().begin();
            em.persist(event);
            em.getTransaction().commit();
            em.clear();

            TestEntity retrieved = em.find(TestEntity.class, event.getId());

            assertThat(retrieved).isNotNull();
            assertThat(retrieved.getDuration()).isNull();
        }

        @Test
        @DisplayName("Should handle negative durations")
        void shouldHandleNegativeDurations() {
            Duration negativeDuration = Duration.ofSeconds(-12345, -678901234);
            
            TestEntity event = new TestEntity(
                "Negative Duration Test", 
                "Testing negative duration", 
                Instant.now(), 
                negativeDuration
            );

            em.getTransaction().begin();
            em.persist(event);
            em.getTransaction().commit();
            em.clear();

            TestEntity retrieved = em.find(TestEntity.class, event.getId());

            assertThat(retrieved).isNotNull();
            assertThat(retrieved.getDuration()).isEqualTo(negativeDuration);
        }
    }

    @Nested
    @DisplayName("Mixed Usage Tests")
    class MixedUsageTests {

        @Test
        @DisplayName("Should handle multiple entities with different temporal values")
        void shouldHandleMultipleEntitiesWithDifferentTemporalValues() {
            TestEntity event1 = new TestEntity(
                "Event 1", 
                "First event", 
                Instant.ofEpochSecond(1000000000L, 100000000), 
                Duration.ofHours(1)
            );
            
            TestEntity event2 = new TestEntity(
                "Event 2", 
                "Second event", 
                Instant.ofEpochSecond(2000000000L, 200000000), 
                Duration.ofHours(2)
            );
            
            TestEntity event3 = new TestEntity(
                "Event 3", 
                "Third event", 
                Instant.ofEpochSecond(3000000000L, 300000000), 
                Duration.ofHours(3)
            );

            em.getTransaction().begin();
            em.persist(event1);
            em.persist(event2);
            em.persist(event3);
            em.getTransaction().commit();
            em.clear();

            // Query all events
            List<TestEntity> events = em.createQuery("SELECT e FROM TestEntity e ORDER BY e.name", TestEntity.class)
                    .getResultList();

            assertThat(events).hasSize(3);
            assertThat(events.get(0).getName()).isEqualTo("Event 1");
            assertThat(events.get(0).getTimestamp().getNano()).isEqualTo(100000000);
            assertThat(events.get(0).getDuration().toHours()).isEqualTo(1);
            
            assertThat(events.get(1).getName()).isEqualTo("Event 2");
            assertThat(events.get(1).getTimestamp().getNano()).isEqualTo(200000000);
            assertThat(events.get(1).getDuration().toHours()).isEqualTo(2);
            
            assertThat(events.get(2).getName()).isEqualTo("Event 3");
            assertThat(events.get(2).getTimestamp().getNano()).isEqualTo(300000000);
            assertThat(events.get(2).getDuration().toHours()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle epoch values")
        void shouldHandleEpochValues() {
            TestEntity event = new TestEntity(
                "Epoch Test", 
                "Testing epoch values", 
                Instant.EPOCH, 
                Duration.ZERO
            );

            em.getTransaction().begin();
            em.persist(event);
            em.getTransaction().commit();
            em.clear();

            TestEntity retrieved = em.find(TestEntity.class, event.getId());

            assertThat(retrieved).isNotNull();
            assertThat(retrieved.getTimestamp()).isEqualTo(Instant.EPOCH);
            assertThat(retrieved.getDuration()).isEqualTo(Duration.ZERO);
        }

        @Test
        @DisplayName("Should handle maximum precision values within limits")
        void shouldHandleMaximumPrecisionValuesWithinLimits() {
            // Use values that are within Instant limits but still test high precision
            Instant highPrecision = Instant.ofEpochSecond(1000000000L, 999999999);
            Duration highPrecisionDuration = Duration.ofSeconds(1000000000L, 999999999);
            
            TestEntity event = new TestEntity(
                "Max Precision Test", 
                "Testing maximum precision", 
                highPrecision, 
                highPrecisionDuration
            );

            em.getTransaction().begin();
            em.persist(event);
            em.getTransaction().commit();
            em.clear();

            TestEntity retrieved = em.find(TestEntity.class, event.getId());

            assertThat(retrieved).isNotNull();
            assertThat(retrieved.getTimestamp()).isEqualTo(highPrecision);
            assertThat(retrieved.getDuration()).isEqualTo(highPrecisionDuration);
        }
    }
}
