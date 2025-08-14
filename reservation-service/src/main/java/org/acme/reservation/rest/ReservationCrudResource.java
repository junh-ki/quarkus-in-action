package org.acme.reservation.rest;

import io.quarkus.hibernate.reactive.rest.data.panache.PanacheEntityResource;
import io.quarkus.rest.data.panache.ResourceProperties;
import org.acme.reservation.entity.Reservation;

/**
 * This will automatically expose the CRUD endpoints without controller
 */
@ResourceProperties(path = "/admin/reservation")
public interface ReservationCrudResource extends PanacheEntityResource<Reservation, Long> {}
