package org.acme.reservation.rental;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Rental {

    private Long id;
    private String userId;
    private Long reservationId;
    private LocalDate startDate;

    @Override
    public String toString() {
        return "Rental{"
                + "id=" + this.id
                + ", userId=" + this.userId
                + ", reservationId=" + this.reservationId
                + ", startDate=" + this.startDate
                + "}";
    }
}
