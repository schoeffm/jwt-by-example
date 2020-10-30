package de.bender;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class AuthFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println(requestContext.getHeaders().get("Authorization"));
    }
}
