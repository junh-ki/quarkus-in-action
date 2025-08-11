package org.acme.inventory.grpc;

import io.quarkus.grpc.GrpcService;
import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.acme.inventory.model.Car;
import org.acme.inventory.model.CarResponse;
import org.acme.inventory.model.InsertCarRequest;
import org.acme.inventory.model.InventoryService;
import org.acme.inventory.model.RemoveCarRequest;
import org.acme.inventory.repository.CarRepository;

@GrpcService
@RequiredArgsConstructor
public class GrpcInventoryService implements InventoryService {

    private final CarRepository carRepository;

    @Override
    @Blocking
    public Multi<CarResponse> add(final Multi<InsertCarRequest> insertCarRequests) {
        return insertCarRequests
            .map(insertCarRequest -> Car.builder()
                .licensePlateNumber(insertCarRequest.getLicensePlateNumber())
                .manufacturer(insertCarRequest.getManufacturer())
                .model(insertCarRequest.getModel())
                .build())
            .onItem()
            .invoke(car -> QuarkusTransaction.requiringNew()
                .run(() -> {
                    this.carRepository.persist(car);
                    Log.info("Persisting " + car);
                }))
            .map(car -> CarResponse.newBuilder()
                .setLicensePlateNumber(car.getLicensePlateNumber())
                .setManufacturer(car.getManufacturer())
                .setModel(car.getModel())
                .setId(car.getId())
                .build());
    }

    @Override
    @Blocking
    @Transactional
    public Uni<CarResponse> remove(final RemoveCarRequest removeCarRequest) {
        return this.carRepository
            .findByLicensePlateNumberOptional(removeCarRequest.getLicensePlateNumber())
            .map(removedCar -> {
                this.carRepository.delete(removedCar);
                return Uni.createFrom()
                    .item(CarResponse.newBuilder()
                        .setLicensePlateNumber(removedCar.getLicensePlateNumber())
                        .setManufacturer(removedCar.getManufacturer())
                        .setModel(removedCar.getModel())
                        .setId(removedCar.getId())
                        .build());
            })
            .orElse(Uni.createFrom().nullItem());
    }
}
