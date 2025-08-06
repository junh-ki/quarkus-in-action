package org.acme.reservation.rest;

import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import jakarta.inject.Inject;
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
import org.acme.reservation.reservation.Reservation;
import org.acme.reservation.reservation.ReservationRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

    private final ReservationRepository reservationRepository;
    private final GraphQLInventoryClient graphQLInventoryClient;
    private final RentalClient rentalClient;
    @Inject
    SecurityContext securityContext;

    // Quarkus CDI doesn't inject qualifiers like @RestClient automatically with Lombok-generated constructors.
    public ReservationResource(
        final ReservationRepository reservationRepository,
        @GraphQLClient("inventory") final GraphQLInventoryClient graphQLInventoryClient,
        @RestClient final RentalClient rentalClient) {
        this.reservationRepository = reservationRepository;
        this.graphQLInventoryClient = graphQLInventoryClient;
        this.rentalClient = rentalClient;
    }

    @GET
    @Path("availability")
    public List<Car> availability(@RestQuery final LocalDate startDate,
                                  @RestQuery final LocalDate endDate) {
        final List<Car> availableCars = this.graphQLInventoryClient.allCars();
        final Map<Long, Car> carsById = new HashMap<>();
        availableCars.forEach(availableCar -> carsById.put(availableCar.getId(), availableCar));
        final List<Reservation> reservations = this.reservationRepository.findAll();
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
        final String userId = Optional.ofNullable(this.securityContext.getUserPrincipal())
            .map(Principal::getName)
            .orElse("anonymous");
        final Reservation savedReservation = this.reservationRepository.save(reservation);
        if (reservation.getStartDay().equals(LocalDate.now())) {
            final Rental rental = this.rentalClient.start(userId, savedReservation.getId());
            Log.info("Successfully started rental: " + rental);
        }
        return savedReservation;
    }

    @GET
    @Path("all")
    public Collection<Reservation> allReservations() {
        final String userId = Optional.ofNullable(this.securityContext.getUserPrincipal())
            .map(Principal::getName)
            .orElse(null);
        return this.reservationRepository.findAll()
            .stream()
            .filter(reservation -> userId == null
                || userId.equals(reservation.getUserId()))
            .toList();
    }
}
