package org.acme.billing.data;

import lombok.Data;
import org.acme.billing.model.Invoice;

@Data
public class ReservationInvoice {

    private Invoice.Reservation reservation;
    private double price;
}
