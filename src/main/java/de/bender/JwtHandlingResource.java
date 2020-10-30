package de.bender;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.net.URI;

@Path("jwt")
@RequestScoped
public class JwtHandlingResource {

    @Inject
    @ConfigProperty(name = "de.bender.authprovider.uri.auth")
    URI authUri;

    @Inject
    @ConfigProperty(name = "de.bender.authprovider.uri.token")
    URI tokenUri;

    @Inject
    @ConfigProperty(name = "de.bender.authprovider.clientId")
    String clientId;

    @Inject
    @ConfigProperty(name = "de.bender.authprovider.grandType")
    String grantType;

    @GET
    @Path("auth")
    public Response auth() {
        return Response.temporaryRedirect(authUri).build();
    }

    @GET
    @Path("callback")
    public Response callback(@QueryParam("code") String authCode) {
        System.out.println(authCode);
        Form form = new Form();
        form.param("grant_type", grantType)
                .param("client_id", clientId)
                .param("code", authCode);

        final Response response = ClientBuilder.newClient()
                .target(tokenUri)
                .request(MediaType.APPLICATION_FORM_URLENCODED)
                .post(Entity.form(form));


        final JsonObject result = Json
                .createReader(new StringReader(response.readEntity(String.class)))
                .readObject();

        return Response.temporaryRedirect(URI.create("../index.html?id=" + result.getString("access_token"))).build();
    }
}
