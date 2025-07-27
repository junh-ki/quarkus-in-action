package org.acme.rental;

import jakarta.annotation.Nonnull;

import java.time.LocalDate;

public record Rental(Long id, String userId, Long reservationId, LocalDate startDate) {

    @Nonnull
    @Override
    public String toString() {
        return "Rental{"
                + "id=" + id
                + ", userId=" + userId
                + ", reservationId=" + reservationId
                + ", startDate=" + startDate
                + "}";
    }
}
