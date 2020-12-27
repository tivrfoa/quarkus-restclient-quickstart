package org.acme.rest.client;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.smallrye.mutiny.Uni;

@Path("/country")
public class CountriesResource {

    private static final Logger LOG = Logger.getLogger(CountriesResource.class);

    @Inject
    @RestClient
    CountriesService countriesService;


    @GET
    @Path("/name/{name}")
    public Set<Country> name(@PathParam String name) {
        return countriesService.getByName(name);
    }

    @GET
    @Path("/name-async/{name}")
    public CompletionStage<Set<Country>> nameAsync(@PathParam String name) {
        return countriesService.getByNameAsync(name);
    }

    @GET
    @Path("/name-uni/{name}")
    public Uni<Set<Country>> nameAsyncRetry(@PathParam String name) {
        return countriesService
                .getByNameAsUni(name)
                .onFailure().retry().atMost(2)
                .onFailure().invoke(f -> LOG.error(f.getMessage()))
                .onFailure().recoverWithNull();
    }
}