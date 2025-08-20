package org.acme.reservation.faulttolerance;

import io.smallrye.mutiny.Uni;
import org.acme.reservation.inventory.Car;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

import java.util.Collections;
import java.util.List;

public class EmptyListFallback implements FallbackHandler<Uni<List<Car>>> {

    @Override
    public Uni<List<Car>> handle(final ExecutionContext context) {
        return Uni.createFrom().item(Collections.emptyList());
    }
}
