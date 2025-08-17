package org.acme.rental.invoice;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.rental.entity.Rental;
import org.acme.rental.invoice.data.InvoiceConfirmation;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class InvoiceConfirmationService {

    @Incoming("invoices-confirmations")
    public void invoicePaid(final InvoiceConfirmation invoiceConfirmation) {
        Log.info("Received invoice confirmation " + invoiceConfirmation);
        if (!invoiceConfirmation.isPaid()) {
            Log.warn("Received unpaid invoice confirmation - " + invoiceConfirmation);
            // retry handling omitted
        }
        final InvoiceConfirmation.InvoiceReservation invoiceReservation = invoiceConfirmation.getInvoice().getReservation();
        Rental.findByUserAndReservationIdsOptional(invoiceReservation.getUserId(), invoiceReservation.getId())
            .ifPresentOrElse(rental -> {
                // mark the already started rental as paid
                rental.setPaid(true);
                rental.update();
            }, () -> {
                // create a new rental starting in the future
                Rental.builder()
                    .userId(invoiceReservation.getUserId())
                    .reservationId(invoiceReservation.getId())
                    .startDate(invoiceReservation.getStartDay())
                    .active(false)
                    .paid(true)
                    .build()
                    .persist();
            });
    }
}
