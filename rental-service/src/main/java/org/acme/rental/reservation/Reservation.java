package org.acme.rental.reservation;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Reservation {

    private LocalDate endDay;
}
