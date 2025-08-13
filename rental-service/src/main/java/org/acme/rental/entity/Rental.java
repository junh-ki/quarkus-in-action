package org.acme.rental.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Rental extends PanacheMongoEntityBase {

    private ObjectId id;
    private String userId;
    private Long reservationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;

    @Nonnull
    @Override
    public String toString() {
        return "Rental{"
                + "id=" + this.getId()
                + ", userId=" + this.getUserId()
                + ", reservationId=" + this.getReservationId()
                + ", startDate=" + this.getStartDate()
                + ", endDate=" + this.getEndDate()
                + ", active=" + this.isActive()
                + "}";
    }

    public static Optional<Rental> findByUserAndReservationIdsOptional(final String userId,
                                                                       final Long reservationId) {
        return find("userId = ?1 and reservationId = ?2", userId, reservationId)
            .firstResultOptional();
    }

    public static List<Rental> listActive() {
        return list("active", true);
    }
}
