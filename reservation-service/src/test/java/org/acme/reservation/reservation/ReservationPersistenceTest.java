package org.acme.reservation.reservation;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.acme.reservation.entity.Reservation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@QuarkusTest
class ReservationPersistenceTest {

    @Test
    @Transactional
    public void testCreateReservation() {
        // Arrange
        final Reservation reservation = Reservation.builder()
            .startDay(LocalDate.now().plusDays(5))
            .endDay(LocalDate.now().plusDays(12))
            .carId(384L)
            .build();

        // Act
        Reservation.persist(reservation);

        // Assert
        Assertions.assertNotNull(reservation.getId());
        Assertions.assertEquals(1, Reservation.count());
        final Reservation persistedReservation = Reservation.findById(reservation.getId());
        Assertions.assertNotNull(persistedReservation);
        Assertions.assertEquals(reservation.getCarId(), persistedReservation.getCarId());
    }
}