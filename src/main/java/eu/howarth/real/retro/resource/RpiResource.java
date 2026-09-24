package eu.howarth.real.retro.resource;

import eu.howarth.fin.priceindex.IndexSeries;
import eu.howarth.fin.priceindex.PriceIndexDataset;
import eu.howarth.fin.priceindex.PriceIndexDatasetLoader;
import eu.howarth.fin.priceindex.PriceIndexEntry;
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

    private static final PriceIndexDataset DATASET = PriceIndexDatasetLoader.bundled(IndexSeries.RPI);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<AnnualRpiEntry> getAll() {
        return DATASET.entries().stream()
                .map(PriceIndexEntry::year)
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
                .mapToInt(PriceIndexEntry::year)
                .max()
                .orElseThrow(() -> new IllegalStateException("RPI dataset is empty"));
        return Map.of("year", latest);
    }
}
