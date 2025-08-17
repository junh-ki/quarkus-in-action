package org.acme.billing.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.acme.billing.model.Invoice;

@Data
@AllArgsConstructor
public class InvoiceConfirmation {

    private Invoice invoice;
    private boolean paid;

    @Override
    public String toString() {
        return "InvoiceConfirmation{"
            + "invoice=" + getInvoice()
            + ", paid=" + isPaid()
            + '}';
    }
}
