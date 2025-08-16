package org.acme.reservation.rest;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.acme.reservation.billing.Invoice;
import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.GraphQLInventoryClient;
import org.acme.reservation.rental.RentalClient;
import org.acme.reservation.entity.Reservation;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

    private static final double STANDARD_RATE_PER_DAY = 19.99;
    private final GraphQLInventoryClient graphQLInventoryClient;
    private final RentalClient rentalClient;

    @Inject
    SecurityContext securityContext;

    @Inject
    @Channel("invoices")
    MutinyEmitter<Invoice> invoiceEmitter;

    // Quarkus CDI doesn't inject qualifiers like @RestClient automatically with Lombok-generated constructors.
    public ReservationResource(
        @GraphQLClient("inventory") final GraphQLInventoryClient graphQLInventoryClient,
        @RestClient final RentalClient rentalClient) {
        this.graphQLInventoryClient = graphQLInventoryClient;
        this.rentalClient = rentalClient;
    }

    @GET
    @Path("availability")
    public Uni<List<Car>> availability(@RestQuery final LocalDate startDate,
                                       @RestQuery final LocalDate endDate) {
        final Uni<List<Car>> availableCarsUni = this.graphQLInventoryClient.allCars();
        final Uni<List<Reservation>> reservationsUni = Reservation.listAll();
        return Uni.combine().all().unis(availableCarsUni, reservationsUni)
            .with((availableCars, reservations) -> {
                final Map<Long, Car> carsById = availableCars.stream()
                    .collect(Collectors.toMap(Car::getId, Function.identity()));
                reservations.forEach(reservation -> {
                    if (reservation.isReserved(startDate, endDate)) {
                        carsById.remove(reservation.getCarId());
                    }
                });
                return carsById.values().stream().toList();
            });
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @WithTransaction
    public Uni<Reservation> make(final Reservation reservation) {
        reservation.setUserId(
            Optional.ofNullable(this.securityContext.getUserPrincipal())
                .map(Principal::getName)
                .orElse("anonymous"));
        return reservation.<Reservation>persist().onItem()
            .call(persistedReservation -> {
                Log.info("Successfully reserved reservation " + persistedReservation);
                final Uni<Void> invoiceUni = this.invoiceEmitter
                    .send(new Invoice(reservation, computePrice(reservation)))
                    .onFailure()
                    .invoke(throwable -> Log.errorf(
                        "Couldn't create invoice for %s. %s&n",
                        persistedReservation,
                        throwable.getMessage()));
                if (persistedReservation.getStartDay().equals(LocalDate.now())) {
                    return invoiceUni.chain(() -> this.rentalClient.start(
                            persistedReservation.getUserId(), persistedReservation.getId())
                        .onItem().invoke(rental -> Log.info("Successfully started rental: " + rental))
                        .replaceWith(persistedReservation));
                }
                return invoiceUni.replaceWith(persistedReservation);
            });
    }

    private double computePrice(final Reservation reservation) {
        final int rentalDays = (int) ChronoUnit.DAYS.between(reservation.getStartDay(), reservation.getEndDay()) + 1;
        return rentalDays * STANDARD_RATE_PER_DAY;
    }

    @GET
    @Path("all")
    public Uni<List<Reservation>> allReservations() {
        final String userId = Optional.ofNullable(this.securityContext.getUserPrincipal())
            .map(Principal::getName)
            .orElse(null);
        return Reservation.<Reservation>listAll()
            .onItem().transform(reservations -> reservations.stream()
                .filter(reservation -> userId == null
                    || userId.equals(reservation.getUserId()))
                .toList());
    }
}
