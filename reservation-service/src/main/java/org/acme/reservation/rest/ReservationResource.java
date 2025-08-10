package org.acme.reservation.rest;

import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.GraphQLInventoryClient;
import org.acme.reservation.rental.Rental;
import org.acme.reservation.rental.RentalClient;
import org.acme.reservation.entity.Reservation;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

    private final GraphQLInventoryClient graphQLInventoryClient;
    private final RentalClient rentalClient;
    @Inject
    SecurityContext securityContext;

    // Quarkus CDI doesn't inject qualifiers like @RestClient automatically with Lombok-generated constructors.
    public ReservationResource(
        @GraphQLClient("inventory") final GraphQLInventoryClient graphQLInventoryClient,
        @RestClient final RentalClient rentalClient) {
        this.graphQLInventoryClient = graphQLInventoryClient;
        this.rentalClient = rentalClient;
    }

    @GET
    @Path("availability")
    public List<Car> availability(@RestQuery final LocalDate startDate,
                                  @RestQuery final LocalDate endDate) {
        final Map<Long, Car> carsById = this.graphQLInventoryClient.allCars().stream()
            .collect(Collectors.toMap(Car::getId, Function.identity()));
        Reservation.listAll()
            .stream()
            .map(reservation -> (Reservation) reservation)
            .forEach(reservation -> {
                if (reservation.isReserved(startDate, endDate)) {
                    carsById.remove(reservation.getCarId());
                }
            });
        return carsById.values().stream().toList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Reservation make(final Reservation reservation) {
        reservation.setUserId(
            Optional.ofNullable(this.securityContext.getUserPrincipal())
                .map(Principal::getName)
                .orElse("anonymous"));
        reservation.persist();
        Log.info("Successfully reserved reservation " + reservation);
        if (reservation.getStartDay().equals(LocalDate.now())) {
            final Rental rental = this.rentalClient.start(reservation.getUserId(), reservation.getId());
            Log.info("Successfully started rental: " + rental);
        }
        return reservation;
    }

    @GET
    @Path("all")
    public Collection<Reservation> allReservations() {
        final String userId = Optional.ofNullable(this.securityContext.getUserPrincipal())
            .map(Principal::getName)
            .orElse(null);
        return Reservation.<Reservation>streamAll()
            .filter(reservation -> userId == null
                || userId.equals(reservation.getUserId()))
            .toList();
    }
}
