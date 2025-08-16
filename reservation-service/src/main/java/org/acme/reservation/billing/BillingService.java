package org.acme.reservation.billing;

import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class BillingService {

    @Incoming("invoices-rabbitmq")
    public void processInvoice(final JsonObject jsonObject) {
        Log.info("Processing received invoice: " + jsonObject.mapTo(Invoice.class));
    }
}
