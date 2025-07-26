package org.acme.reservation.reservation;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Reservation {

    private Long id;
    private Long carId;
    private LocalDate startDay;
    private LocalDate endDay;

    /**
     * Check if the given duration overlaps with this reservation
     * @return true if the dates overlap with the reservation, false otherwise
     */
    public boolean isReserved(final LocalDate startDay, final LocalDate endDay) {
        return !(this.endDay.isBefore(startDay) || this.startDay.isAfter(endDay));
    }
}
