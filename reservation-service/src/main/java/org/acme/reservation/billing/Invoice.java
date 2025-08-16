package org.acme.reservation.billing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acme.reservation.entity.Reservation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    private Reservation reservation;
    private double price;

    @Override
    public String toString() {
        return "Invoice {"
            + "reservation=" + getReservation()
            + ", price=" + getPrice()
            + "}";
    }
}
