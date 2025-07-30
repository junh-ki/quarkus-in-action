package org.acme.inventory.service;

import lombok.RequiredArgsConstructor;
import org.acme.inventory.database.CarInventory;
import org.acme.inventory.model.Car;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

@GraphQLApi
@RequiredArgsConstructor
public class GraphQLInventoryService {

    private final CarInventory carInventory;

    @Query
    public List<Car> cars() {
        return this.carInventory.getCars();
    }

    @Mutation
    public Car register(final Car car) {
        car.setId(this.carInventory.incrementAndGet());
        this.carInventory.getCars().add(car);
        return car;
    }

    @Mutation
    public boolean remove(final String licensePlateNumber) {
        final List<Car> cars = this.carInventory.getCars();
        return cars.stream()
                .filter(car -> car.getLicensePlateNumber()
                        .equals(licensePlateNumber))
                .findAny()
                .map(cars::remove)
                .orElse(false);
    }
}
