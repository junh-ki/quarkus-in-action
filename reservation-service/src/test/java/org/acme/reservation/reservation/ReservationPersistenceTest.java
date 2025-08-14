package org.acme.reservation.reservation;

import io.quarkus.logging.Log;
import io.quarkus.test.hibernate.reactive.panache.TransactionalUniAsserter;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import org.acme.reservation.entity.Reservation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@QuarkusTest
class ReservationPersistenceTest {

    @Test
    @RunOnVertxContext
    public void testCreateReservation(final TransactionalUniAsserter transactionalUniAsserter) {
        // Arrange
        final Reservation reservation = Reservation.builder()
            .startDay(LocalDate.now().plusDays(5))
            .endDay(LocalDate.now().plusDays(12))
            .carId(384L)
            .build();

        // Act & Assert
        transactionalUniAsserter.<Reservation>assertThat(
            reservation::persist, r -> {
                Assertions.assertNotNull(r.getId());
                transactionalUniAsserter.putData("reservation.id", r.getId());
            });
        transactionalUniAsserter.assertEquals(() -> {
            Log.info("Using a lambda instead of Reservation::count to avoid Panache method reference issues in reactive context");
            return Reservation.count();
        }, 1L);
        transactionalUniAsserter.assertThat(() -> Reservation.<Reservation>findById(
                transactionalUniAsserter.getData("reservation.id")),
            persistedReservation -> {
                Assertions.assertNotNull(persistedReservation);
                Assertions.assertEquals(reservation.getCarId(), persistedReservation.getCarId());
            });
    }
}