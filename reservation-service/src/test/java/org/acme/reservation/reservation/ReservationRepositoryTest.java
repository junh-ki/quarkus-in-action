package org.acme.reservation.reservation;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@QuarkusTest
class ReservationRepositoryTest {

    @Inject
    ReservationRepository reservationRepository;

    @Test
    public void testCreateReservation() {
        // Arrange
        final Reservation reservation = Reservation.builder()
            .startDay(LocalDate.now().plusDays(5))
            .endDay(LocalDate.now().plusDays(12))
            .carId(384L)
            .build();

        // Act
        this.reservationRepository.save(reservation);

        // Assert
        Assertions.assertNotNull(reservation.getId());
        Assertions.assertTrue(this.reservationRepository.findAll().contains(reservation));
    }
}