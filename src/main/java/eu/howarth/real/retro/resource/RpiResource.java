package eu.howarth.real.retro.resource;

import eu.howarth.fin.rpi.RpiDataset;
import eu.howarth.fin.rpi.RpiDatasetLoader;
import eu.howarth.fin.rpi.RpiEntry;
import eu.howarth.real.retro.dto.AnnualRpiEntry;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

/**
 * Annual ONS RPI CHAW series, aggregated from fin-model-rpi's bundled monthly
 * dataset. Stateless — no database, no dataset persistence (that lives client-side
 * in the UI's localStorage, see kanban #874 comment #1586).
 */
@Path("/api/rpi")
public class RpiResource {

    private static final RpiDataset DATASET = RpiDatasetLoader.bundled();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<AnnualRpiEntry> getAll() {
        return DATASET.entries().stream()
                .map(RpiEntry::year)
                .distinct()
                .sorted()
                .map(year -> new AnnualRpiEntry(year, DATASET.indexForYear(year)))
                .toList();
    }

    @GET
    @Path("/latest-year")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Integer> latestYear() {
        int latest = DATASET.entries().stream()
                .mapToInt(RpiEntry::year)
                .max()
                .orElseThrow(() -> new IllegalStateException("RPI dataset is empty"));
        return Map.of("year", latest);
    }
}
