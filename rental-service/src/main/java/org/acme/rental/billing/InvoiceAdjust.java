package org.acme.rental.billing;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class InvoiceAdjust {

    private String rentalId;
    private String userId;
    private LocalDate actualEndDate;
    private double price;

    @Override
    public String toString() {
        return "InvoiceAdjust{"
            + "rentalId=" + getRentalId()
            + ", userId=" + getUserId()
            + ", actualEndDate=" + getActualEndDate()
            + ", price=" + getPrice()
            + "}";
    }
}
