package org.acme.rental.invoice.data;

import lombok.Data;

import java.time.LocalDate;

@Data
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

    @Data
    public static final class Invoice {

        private boolean paid;
        private InvoiceReservation reservation;

        @Override
        public String toString() {
            return "Invoice{"
                + "paid=" + isPaid()
                + ", reservation=" + getReservation()
                + '}';
        }
    }

    @Data
    public static final class InvoiceReservation {

        private Long id;
        private String userId;
        private LocalDate startDay;

        @Override
        public String toString() {
            return "InvoiceReservation{" +
                "id=" + getId()
                + ", userId='" + getUserId() + '\''
                + ", startDay=" + getStartDay()
                + '}';
        }
    }
}
