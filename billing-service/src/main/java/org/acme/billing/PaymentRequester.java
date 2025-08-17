package org.acme.billing;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.billing.data.InvoiceConfirmation;
import org.acme.billing.model.Invoice;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.util.Random;

@ApplicationScoped
public class PaymentRequester {

    private final Random random = new Random();

    @Blocking
    @Incoming("invoices-requests")
    @Outgoing("invoices-confirmations")
    public InvoiceConfirmation requestPayment(final Invoice invoice) {
        payment(invoice.getReservation().getUserId(), invoice.getTotalPrice(), invoice);
        Log.infof("Invoice %s is paid.", invoice);
        return new InvoiceConfirmation(invoice, true);
    }

    private void payment(final String user, final double price, final Object data) {
        Log.infof("Request for payment user: %s, price: %f, data: %s", user, price, data);
        try {
            Thread.sleep(this.random.nextInt(1000, 5000));
            if (data instanceof Invoice invoice) {
                invoice.setPaid(true);
                invoice.update();
            }
        } catch (final InterruptedException interruptedException) {
            Log.error("Sleep interrupted.", interruptedException);
        }
    }

    @Incoming("invoices-confirmations")
    public void consume(final InvoiceConfirmation invoiceConfirmation) {
        Log.info(invoiceConfirmation);
    }
}
