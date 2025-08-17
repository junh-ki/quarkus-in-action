package org.acme.inventory.health;

import io.smallrye.health.api.Wellness;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.inventory.repository.CarRepository;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Wellness
public class CarCountCheck implements HealthCheck {

    @Inject
    CarRepository carRepository;

    @Override
    @Transactional
    public HealthCheckResponse call() {
        final long carCount = this.carRepository.count();
        return HealthCheckResponse.builder()
            .name("car-count-check")
            .status(carCount > 0)
            .withData("car-count", carCount)
            .build();
    }
}
