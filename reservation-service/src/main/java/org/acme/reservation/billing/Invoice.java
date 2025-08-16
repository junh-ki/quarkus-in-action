package org.acme.reservation.billing;

import lombok.Data;
import org.acme.reservation.entity.Reservation;

import java.time.temporal.ChronoUnit;

@Data
public class Invoice {

    private Reservation reservation;
    private double price;

    public Invoice(final Reservation reservation, final double ratePerDay) {
        setReservation(reservation);
        setPrice(computePrice(getReservation(), ratePerDay));
    }

    private double computePrice(final Reservation reservation, final double ratePerDay) {
        final int rentalDays = (int) ChronoUnit.DAYS.between(reservation.getStartDay(), reservation.getEndDay()) + 1;
        return rentalDays * ratePerDay;
    }

    @Override
    public String toString() {
        return "Invoice {"
            + "reservation=" + getReservation()
            + ", price=" + getPrice()
            + "}";
    }
}
