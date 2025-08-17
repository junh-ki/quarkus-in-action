package org.acme.billing;

import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.billing.data.ReservationInvoice;
import org.acme.billing.model.Invoice;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class InvoiceProcessor {

    @Incoming("invoices")
    @Outgoing("invoices-requests")
    public Message<Invoice> processInvoice(final Message<JsonObject> message) {
        final ReservationInvoice reservationInvoice = message.getPayload().mapTo(ReservationInvoice.class);
        final Invoice.Reservation reservation = reservationInvoice.getReservation();
        final Invoice invoice = new Invoice(reservationInvoice.getPrice(), false, reservation);
        invoice.persist();
        Log.info("Processing invoice: " + invoice);
        return message.withPayload(invoice);
    }
}
