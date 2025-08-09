package org.acme.users;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.acme.users.model.Car;
import org.acme.users.model.Reservation;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.LocalDate;
import java.util.Collection;

@Path("/")
public class ReservationsResource {

    @CheckedTemplate
    private static class Templates {
        public static native TemplateInstance index(
            final LocalDate startDate,
            final LocalDate endDate,
            final String name);

        public static native TemplateInstance listofreservations(
            final Collection<Reservation> reservations);

        public static native TemplateInstance availablecars(
            final Collection<Car> cars,
            final LocalDate startDate,
            final LocalDate endDate);
    }

    @Inject
    SecurityContext securityContext;

    @RestClient
    ReservationsClient reservationsClient;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance index(@RestQuery LocalDate startDate,
                                  @RestQuery LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().plusDays(1L);
        }
        if (endDate == null) {
            endDate = LocalDate.now().plusDays(7L);
        }
        return Templates.index(startDate, endDate, securityContext.getUserPrincipal().getName());
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/get")
    public TemplateInstance getReservations() {
        return Templates.listofreservations(this.reservationsClient.allReservations());
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/available")
    public TemplateInstance getAvailableCars(
        @RestQuery final LocalDate startDate,
        @RestQuery final LocalDate endDate) {
        return Templates.availablecars(
            this.reservationsClient.availability(startDate, endDate),
            startDate,
            endDate);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/reserve")
    public RestResponse<TemplateInstance> create(
        @RestForm final LocalDate startDate,
        @RestForm final LocalDate endDate,
        @RestForm final Long carId) {
        this.reservationsClient.make(
            Reservation.builder()
                .startDay(startDate)
                .endDay(endDate)
                .carId(carId)
                .build());
        return RestResponse.ResponseBuilder
            .ok(getReservations())
            .header("HX-Trigger-After-Swap",
                "update-available-cars-list")
            .build();
    }
}
