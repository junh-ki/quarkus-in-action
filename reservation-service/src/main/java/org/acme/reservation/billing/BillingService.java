package org.acme.reservation.billing;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class BillingService {

    @Incoming("invoices")
    public void processInvoice(final Invoice invoice) {
        Log.info("Processing received invoice: " + invoice);
    }
}
