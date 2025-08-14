package org.acme.reservation.rental;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/rental")
@RegisterRestClient
public interface RentalClient {

    @POST
    @Path("/start/{userId}/{reservationId}")
    Uni<Rental> start(@PathParam("userId") final String userId,
                      @PathParam("reservationId") final Long reservationId);
}
