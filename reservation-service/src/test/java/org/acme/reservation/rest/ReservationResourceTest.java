package org.acme.reservation.rest;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.DisabledOnIntegrationTest;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.GraphQLInventoryClient;
import org.acme.reservation.entity.Reservation;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestHTTPEndpoint(ReservationResource.class)
class ReservationResourceTest {

    @TestHTTPResource
    URL reservationResource;

    @TestHTTPResource("availability")
    URL availability;

    @Test
    @DisabledOnIntegrationTest(forArtifactTypes = DisabledOnIntegrationTest.ArtifactType.NATIVE_BINARY)
    public void testMakingAReservationAndCheckAvailability() {
        // Arrange
        final GraphQLInventoryClient graphQLInventoryClientMock = Mockito.mock(GraphQLInventoryClient.class);
        Mockito.when(graphQLInventoryClientMock.allCars())
            .thenReturn(Uni.createFrom()
                .item(Collections.singletonList(new Car(1L, "ABC123", "Peugeot", "406"))));
        QuarkusMock.installMockForType(graphQLInventoryClientMock, GraphQLInventoryClient.class);
        final String startDate = "2022-01-01";
        final String endDate = "2022-01-10";

        // Act & Assert

        // List available cars for the requested timeslot and choose one
        final Car car = RestAssured.given()
            .queryParam("startDate", startDate)
            .queryParam("endDate", endDate)
            .when()
            .get(this.availability)
            .then()
            .statusCode(200)
            .extract()
            .as(Car[].class)[0];
        // Prepare a Reservation object and submit the reservation
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(Reservation.builder()
                .carId(car.getId())
                .startDay(LocalDate.parse(startDate))
                .endDay(LocalDate.parse(endDate))
                .build())
            .when()
            .post(this.reservationResource)
            .then()
            .statusCode(200)
            .body("carId", is(car.getId().intValue()));
        // Verify that this car doesn't show as available anymore
        RestAssured.given()
            .queryParam("startDate", startDate)
            .queryParam("endDate", endDate)
            .when()
            .get(this.availability)
            .then()
            .statusCode(200)
            .body("findAll { car -> car.id == " + car.getId().intValue() + " }", hasSize(0));
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