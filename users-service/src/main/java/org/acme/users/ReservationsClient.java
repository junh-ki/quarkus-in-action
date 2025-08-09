package org.acme.users;

import io.quarkus.oidc.token.propagation.common.AccessToken;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.acme.users.model.Car;
import org.acme.users.model.Reservation;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.Collection;

@AccessToken
@Path("reservation")
@RegisterRestClient
public interface ReservationsClient {

    @GET
    @Path("all")
    Collection<Reservation> allReservations();

    @POST
    Reservation make(final Reservation reservation);

    @GET
    @Path("availability")
    Collection<Car> availability(
        @RestQuery final LocalDate startDate,
        @RestQuery final LocalDate endDate);
}
