package org.acme.reservation.reservation;

import jakarta.inject.Singleton;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
public class InMemoryReservationRepository implements ReservationRepository {

    private final AtomicLong ids = new AtomicLong(0);
    private final List<Reservation> store = new CopyOnWriteArrayList<>();

    @Override
    public List<Reservation> findAll() {
        return Collections.unmodifiableList(this.store);
    }

    @Override
    public Reservation save(final Reservation reservation) {
        reservation.setId(this.ids.incrementAndGet());
        store.add(reservation);
        return reservation;
    }
}
