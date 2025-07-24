package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.security.Principal;

@Path("/hello")
public class GreetingResource {

    @ConfigProperty(name = "greeting")
    String greeting;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return this.greeting;
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
