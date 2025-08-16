package org.acme.rental.reservation;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class Reservation {

    private LocalDate endDay;
}
