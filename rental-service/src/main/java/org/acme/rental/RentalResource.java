package org.acme.rental;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.rental.billing.InvoiceAdjust;
import org.acme.rental.entity.Rental;
import org.acme.rental.reservation.Reservation;
import org.acme.rental.reservation.ReservationClient;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Path("/rental")
public class RentalResource {

    private static final double STANDARD_REFUND_RATE_PER_DAY = -10.99;
    protected static final double STANDARD_PRICE_FOR_PROLONGED_DAY = 25.99;

    @Inject
    @RestClient
    ReservationClient reservationClient;

    @Inject
    @Channel("invoices-adjust")
    Emitter<InvoiceAdjust> adjustmentEmitter;

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
                final LocalDate today = LocalDate.now();
                final Reservation reservation = this.reservationClient.getById(reservationId);
                if (!reservation.getEndDay().isEqual(today)) {
                    Log.infof("Adjusting price for rental %s. Original "
                        + "reservation end day was %s.", rental, reservation.getEndDay());
                    this.adjustmentEmitter.send(
                        new InvoiceAdjust(
                            rental.getId().toString(),
                            rental.getUserId(),
                            today,
                            computePrice(reservation.getEndDay(), today)));
                }
                rental.setEndDate(today);
                rental.setActive(false);
                rental.update();
                return rental;
            })
            .orElseThrow();
    }

    private double computePrice(final LocalDate endDate, final LocalDate today) {
        return endDate.isBefore(today)
            ? ChronoUnit.DAYS.between(endDate, today) * STANDARD_PRICE_FOR_PROLONGED_DAY
            : ChronoUnit.DAYS.between(today, endDate) * STANDARD_REFUND_RATE_PER_DAY;
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
