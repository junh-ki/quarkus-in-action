package org.acme.users;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;
import java.util.Optional;

@Path("/whoami")
public class WhoAmIResource {

    @Inject
    Template whoami;

    @Inject
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        final String userId = Optional.ofNullable(this.securityContext.getUserPrincipal())
            .map(Principal::getName)
            .orElse(null);
        return this.whoami.data("name", userId);
    }
}
