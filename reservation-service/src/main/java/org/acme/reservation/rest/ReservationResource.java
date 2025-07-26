package org.acme.reservation.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.InventoryClient;
import org.acme.reservation.reservation.Reservation;
import org.acme.reservation.reservation.ReservationRepository;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class ReservationResource {

    private final ReservationRepository reservationRepository;
    private final InventoryClient inventoryClient;

    @GET
    @Path("availability")
    public List<Car> availability(@RestQuery final LocalDate startDate,
                                  @RestQuery final LocalDate endDate) {
        final List<Car> availableCars = this.inventoryClient.findAllCars();
        final Map<Long, Car> carsById = new HashMap<>();
        availableCars.forEach(availableCar -> carsById.put(availableCar.id(),  availableCar));
        final List<Reservation> reservations = reservationRepository.findAll();
        reservations.forEach(reservation -> {
            if (reservation.isReserved(startDate, endDate)) {
                carsById.remove(reservation.getCarId());
            }
        });
        return carsById.values().stream().toList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Reservation make(final Reservation reservation) {
        return reservationRepository.save(reservation);
    }
}
