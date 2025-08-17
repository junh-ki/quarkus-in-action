package org.acme.billing;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.billing.data.InvoiceConfirmation;
import org.acme.billing.model.Invoice;
import org.acme.billing.model.InvoiceAdjust;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
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

    @Blocking
    @Incoming("invoices-adjust")
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public void requestAdjustment(final InvoiceAdjust invoiceAdjust) {
        Log.info("Received invoice adjustment: " + invoiceAdjust);
        payment(invoiceAdjust.getUserId(), invoiceAdjust.getPrice(), invoiceAdjust);
        Log.infof("Invoice adjustment %s is paid.", invoiceAdjust);
    }

    private void payment(final String user, final double price, final Object data) {
        Log.infof("Request for payment user: %s, price: %f, data: %s", user, price, data);
        try {
            Thread.sleep(this.random.nextInt(1000, 5000));
            if (data instanceof Invoice invoice) {
                invoice.setPaid(true);
                invoice.update();
            }
            if (data instanceof InvoiceAdjust invoiceAdjust) {
                invoiceAdjust.setPaid(true);
                invoiceAdjust.persist();
            }
        } catch (final InterruptedException interruptedException) {
            Log.error("Sleep interrupted.", interruptedException);
        }
    }
}
