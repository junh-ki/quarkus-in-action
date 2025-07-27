package org.acme.rental;

import io.quarkus.logging.Log;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

@Path("/rental")
public class RentalResource {

    private final AtomicLong id = new AtomicLong(0);

    @POST
    @Path("/start/{userId}/{reservationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Rental start(@PathParam("userId") final String userId,
                        @PathParam("reservationId") final Long reservationId) {
        Log.infof("Starting rental for %s with reservation %s", userId, reservationId);
        return new Rental(this.id.incrementAndGet(), userId, reservationId, LocalDate.now());
    }
}
