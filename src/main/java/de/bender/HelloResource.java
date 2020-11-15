package de.bender;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.JsonWebToken;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author airhacks.com
 */
@Path("/hello")
@RequestScoped
public class HelloResource {
    @Inject
    JsonWebToken jwt;

    @Context
    SecurityContext ctx;

    @Inject
    Principal principal;

    @GET
    @RolesAllowed({"yosh"})
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@Context SecurityContext ctx) {
        return getResponseString(ctx);
    }

    private String getResponseString(SecurityContext ctx) {
        String name;
        if (ctx.getUserPrincipal() == null) {
            name = "anonymous";
        } else if (!ctx.getUserPrincipal().getName().equals(jwt.getName())) {
            throw new InternalServerErrorException("Principal and JsonWebToken names do not match");
        } else {
            name = ctx.getUserPrincipal().getName();
        }
        Set<String> groups = hasJwt() ? jwt.getGroups(): Collections.emptySet();

        return String.format("hello + %s,"
                        + " isHttps: %s,"
                        + " authScheme: %s,"
                        + " hasJWT: %s,"
                        + " groups: %s",
                name, ctx.isSecure(), ctx.getAuthenticationScheme(), hasJwt(), groups);
    }

    private boolean hasJwt() {
        return jwt.getClaimNames() != null;
    }
}
