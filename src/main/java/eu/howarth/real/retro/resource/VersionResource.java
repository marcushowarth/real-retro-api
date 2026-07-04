package eu.howarth.real.retro.resource;

import eu.howarth.real.retro.config.AppConfig;
import eu.howarth.real.retro.dto.VersionInfo;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/version")
public class VersionResource {

    private final AppConfig config;

    public VersionResource(AppConfig config) {
        this.config = config;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public VersionInfo version() {
        return new VersionInfo(config.version(), config.gitSha(), config.builtAt());
    }
}
