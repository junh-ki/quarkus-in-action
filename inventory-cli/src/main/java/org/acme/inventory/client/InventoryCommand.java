package org.acme.inventory.client;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.logging.Log;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.acme.inventory.model.InsertCarRequest;
import org.acme.inventory.model.InventoryService;
import org.acme.inventory.model.RemoveCarRequest;

@QuarkusMain
public class InventoryCommand implements QuarkusApplication {

    private static final String USAGE =
        "Usage: inventory <add>|<remove> <license plate number> <manufacturer> <model>";
    private static final String ACTION_ADD  = "add";
    private static final String ACTION_REMOVE = "remove";

    @GrpcClient("inventory")
    InventoryService inventoryService;

    @Override
    public int run(final String... args) {
        final String action = args.length > 0
            ? args[0]
            : null;
        if (ACTION_ADD.equalsIgnoreCase(action) && args.length >= 4) {
            add(args[1], args[2], args[3]);
            return 0;
        } else if (ACTION_REMOVE.equalsIgnoreCase(action) && args.length >= 2) {
            remove(args[1]);
            return 0;
        }
        Log.error(USAGE);
        return 1;
    }

    private void add(final String licensePlateNumber, final String manufacturer, final String model) {
        this.inventoryService.add(InsertCarRequest.newBuilder()
                .setLicensePlateNumber(licensePlateNumber)
                .setManufacturer(manufacturer)
                .setModel(model)
                .build())
            .onItem()
            .invoke(carResponse -> Log.info("Inserted new car " + carResponse))
            .await()
            .indefinitely();
    }

    private void remove(final String licensePlateNumber) {
        this.inventoryService.remove(RemoveCarRequest.newBuilder()
                .setLicensePlateNumber(licensePlateNumber)
                .build())
            .onItem()
            .invoke(carResponse -> Log.info("Removed car " + carResponse))
            .await()
            .indefinitely();
    }
}
