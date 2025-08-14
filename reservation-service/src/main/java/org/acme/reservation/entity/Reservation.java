package org.acme.reservation.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Reservation extends PanacheEntityBase {

    @Id
    @GeneratedValue
    private Long id;
    private Long carId;
    private String userId;
    private LocalDate startDay;
    private LocalDate endDay;

    /**
     * Check if the given duration overlaps with this reservation
     * @return true if the dates overlap with the reservation, false otherwise
     */
    public boolean isReserved(final LocalDate startDay, final LocalDate endDay) {
        return !(this.getEndDay().isBefore(startDay) || this.getStartDay().isAfter(endDay));
    }

    @Override
    public String toString() {
        return "Reservation{"
            + "id=" + this.getId()
            + ", carId=" + this.getCarId()
            + ", userId=" + this.getUserId()
            + ", startDay=" + this.getStartDay()
            + ", endDay=" + this.getEndDay()
            + "}";
    }
}
