package org.acme.rental;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.rental.entity.Rental;

import java.time.LocalDate;
import java.util.List;

@Path("/rental")
public class RentalResource {

    @POST
    @Path("/start/{userId}/{reservationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Rental start(@PathParam("userId") final String userId,
                        @PathParam("reservationId") final Long reservationId) {
        Log.infof("Starting rental for %s with reservation %s", userId, reservationId);
        final Rental rental = Rental.builder()
            .userId(userId)
            .reservationId(reservationId)
            .startDate(LocalDate.now())
            .active(true)
            .build();
        rental.persist();
        return rental;
    }

    @PUT
    @Path("/end/{userId}/{reservationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Rental end(@PathParam("userId") final String userId,
                      @PathParam("reservationId") final Long reservationId) {
        Log.infof("Ending rental for %s with reservation %s", userId, reservationId);
        return Rental.findByUserAndReservationIdsOptional(userId, reservationId)
            .map(rental -> {
                rental.setEndDate(LocalDate.now());
                rental.setActive(false);
                rental.update();
                return rental;
            })
            .orElseThrow();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Rental> list() {
        return Rental.listAll();
    }

    @GET
    @Path("/active")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Rental> listActive() {
        return Rental.listActive();
    }
}
