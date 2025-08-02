package org.acme.inventory.database;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.acme.inventory.model.Car;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class CarInventory {

    private final AtomicLong ids = new AtomicLong(0);

    @Getter
    private List<Car> cars;

    @PostConstruct
    void initialize() {
        this.cars = new CopyOnWriteArrayList<>();
        initialData();
    }

    private void initialData() {
        this.cars.add(new Car(incrementAndGet(), "ABC123", "Mazda", "6"));
        this.cars.add(new Car(incrementAndGet(), "XYZ987", "Ford", "Mustang"));
    }

    public Long incrementAndGet() {
        return this.ids.incrementAndGet();
    }

    public void add(final Car car) {
        this.cars.add(car);
    }

    public void remove(final Car car) {
        this.cars.remove(car);
    }
}
