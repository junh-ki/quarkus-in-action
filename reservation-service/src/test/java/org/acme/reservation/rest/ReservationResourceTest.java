package org.acme.reservation.rest;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.acme.reservation.reservation.Reservation;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.time.LocalDate;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestHTTPEndpoint(ReservationResource.class)
class ReservationResourceTest {

    @TestHTTPResource
    URL reservationResource;

    @Test
    public void testAvailability() {
        // Arrange
        final String startDate = "2025-03-20";
        final String endDate = "2025-03-29";

        // Act & Assert
        RestAssured
            .given()
            .queryParam("startDate", startDate)
            .queryParam("endDate", endDate)
            .when()
            .get(this.reservationResource + "/availability")
            .then()
            .statusCode(200)
            .body("$.size()", is(1))
            .body("[0].id", is(1))
            .body("[0].licensePlateNumber", equalTo("ABC123"))
            .body("[0].manufacturer", equalTo("Peugeot"))
            .body("[0].model", equalTo("406"));
    }

    @Test
    public void testReservationIds() {
        // Arrange
        final Reservation reservation = Reservation.builder()
            .carId(12345L)
            .startDay(LocalDate.parse("2025-03-20"))
            .endDay(LocalDate.parse("2025-03-29"))
            .build();

        // Act & Assert
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(reservation)
            .when()
            .post(this.reservationResource)
            .then()
            .statusCode(200)
            .body("id", notNullValue());
    }
}