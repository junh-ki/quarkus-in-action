package org.acme.billing.model;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Invoice extends PanacheMongoEntityBase {

    private ObjectId id;
    private double totalPrice;
    private boolean paid;
    private Reservation reservation;

    public Invoice(final double totalPrice, final boolean paid, final Reservation reservation) {
        this.totalPrice = totalPrice;
        this.paid = paid;
        this.reservation = reservation;
    }

    @Override
    public String toString() {
        return "Invoice{"
            + "totalPrice=" + getTotalPrice()
            + ", paid=" + isPaid()
            + ", reservation=" + getReservation()
            + ", id=" + getId()
            + "}";
    }

    @Data
    public static final class Reservation {

        private Long id;
        private String userId;
        private Long carId;
        private LocalDate startDay;
        private LocalDate endDay;

        @Override
        public String toString() {
            return "Reservation{"
                + "id=" + getId()
                + ", userId=" + getUserId()
                + ", carId=" + getCarId()
                + ", startDay=" + getStartDay()
                + ", endDay=" + getEndDay()
                + "}";
        }
    }
}
