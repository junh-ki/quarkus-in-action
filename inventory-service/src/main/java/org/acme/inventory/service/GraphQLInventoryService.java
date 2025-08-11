package org.acme.inventory.service;

import io.quarkus.logging.Log;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.acme.inventory.model.Car;
import org.acme.inventory.repository.CarRepository;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

@GraphQLApi
@RequiredArgsConstructor
public class GraphQLInventoryService {

    private final CarRepository carRepository;

    @Query
    public List<Car> cars() {
        return this.carRepository.listAll();
    }

    @Mutation
    @Transactional
    public Car register(final Car car) {
        this.carRepository.persist(car);
        Log.info("Persisting " + car);
        return car;
    }

    @Mutation
    @Transactional
    public boolean remove(final String licensePlateNumber) {
        return this.carRepository
            .findByLicensePlateNumberOptional(licensePlateNumber)
            .map(toBeRemoved -> {
                this.carRepository.delete(toBeRemoved);
                return true;
            })
            .orElse(false);
    }
}
