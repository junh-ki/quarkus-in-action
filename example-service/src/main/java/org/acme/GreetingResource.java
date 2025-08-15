package org.acme;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;

@Path("/hello")
public class GreetingResource {

    private static final String CHANNEL_TICKS = "ticks";
    private static final String CHANNEL_TIMES = "times";

    @ConfigProperty(name = "greeting")
    String greeting;

    @GET
    @Path("/virtualThread")
    @RunOnVirtualThread
    public String virtualThread() {
        final String message = "Running on " + Thread.currentThread().getName();
        Log.info(message);
        return message;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        Multi.createFrom().items("a", "b", "c")
            .onItem()
            .transform(String::toUpperCase)
            .onItem()
            .invoke(string -> Log.info("Intermediate stage " + string))
            .onItem()
            .transform(string -> string + " item")
            .filter(string -> !string.startsWith("B"))
            .onCompletion()
            .invoke(() -> Log.info("Stream completed"))
            .subscribe()
            .with(string -> Log.info("Subscriber received " + string));
        return this.greeting;
    }

    @Outgoing(CHANNEL_TICKS)
    public Multi<Long> aFewTicks() {
        return Multi.createFrom()
            .ticks().every(Duration.ofSeconds(1))
            .select().first(5);
    }

    @Incoming(CHANNEL_TICKS)
    @Outgoing(CHANNEL_TIMES)
    public Multi<String> processor(final Multi<Long> ticks) {
        return ticks.map(tick -> Instant.now().toString());
    }

    @Incoming(CHANNEL_TIMES)
    public void consumer(final String payload) {
        Log.info(payload);
    }

    @GET
    @Path("/whoami")
    @Produces(MediaType.TEXT_PLAIN)
    public String whoAmI(@Context final SecurityContext securityContext) {
        final Principal userPrincipal = securityContext.getUserPrincipal();
        if (userPrincipal != null) {
            return userPrincipal.getName();
        }
        return "anonymous";
    }
}
