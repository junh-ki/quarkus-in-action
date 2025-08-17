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
public class InvoiceAdjust extends PanacheMongoEntityBase {

    private ObjectId id;
    private String rentalId;
    private String userId;
    private LocalDate actualEndDate;
    private double price;
    private boolean paid;

    @Override
    public String toString() {
        return "InvoiceAdjust{"
            + "id=" + getId()
            + ", rentalId='" + getRentalId() + '\''
            + ", userId='" + getUserId() + '\''
            + ", actualEndDate=" + getActualEndDate()
            + ", price=" + getPrice()
            + ", paid=" + isPaid()
            + '}';
    }
}
