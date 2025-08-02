package org.acme.inventory.grpc;

import io.quarkus.grpc.GrpcService;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.acme.inventory.database.CarInventory;
import org.acme.inventory.model.Car;
import org.acme.inventory.model.CarResponse;
import org.acme.inventory.model.InsertCarRequest;
import org.acme.inventory.model.InventoryService;
import org.acme.inventory.model.RemoveCarRequest;

@GrpcService
public class GrpcInventoryService implements InventoryService {

    @Inject
    CarInventory carInventory;

    @Override
    public Uni<CarResponse> add(final InsertCarRequest insertCarRequest) {
        final Car car = Car.builder()
            .licensePlateNumber(insertCarRequest.getLicensePlateNumber())
            .manufacturer(insertCarRequest.getManufacturer())
            .model(insertCarRequest.getModel())
            .id(this.carInventory.incrementAndGet())
            .build();
        Log.info("Persisting " + car);
        this.carInventory.add(car);
        return Uni.createFrom()
            .item(CarResponse.newBuilder()
                .setLicensePlateNumber(car.getLicensePlateNumber())
                .setManufacturer(car.getManufacturer())
                .setModel(car.getModel())
                .setId(car.getId())
                .build());
    }

    @Override
    public Uni<CarResponse> remove(final RemoveCarRequest removeCarRequest) {
        return this.carInventory.getCars().stream()
            .filter(car -> removeCarRequest.getLicensePlateNumber()
                .equals(car.getLicensePlateNumber()))
            .findFirst()
            .map(removedCar -> {
                this.carInventory.remove(removedCar);
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
