package org.acme.reservation.reservation;

import java.util.List;

public interface ReservationRepository {

    List<Reservation> findAll() ;

    Reservation save(final Reservation reservation);
}
