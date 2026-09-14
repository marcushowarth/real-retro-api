package eu.howarth.real.retro.resource;

import eu.howarth.fin.priceindex.IndexSeries;
import eu.howarth.fin.priceindex.PriceIndexDataset;
import eu.howarth.fin.priceindex.PriceIndexDatasetLoader;
import eu.howarth.fin.priceindex.PriceIndexEntry;
import eu.howarth.real.retro.dto.AnnualCpiEntry;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

/**
 * Annual ONS CPI D7BT series, aggregated from fin-model-rpi's bundled
 * monthly dataset — the CPI counterpart to {@link RpiResource} (kanban
 * #981). Stateless — no database, no dataset persistence (that lives
 * client-side in the UI's localStorage, see kanban #874 comment #1586).
 */
@Path("/api/cpi")
public class CpiResource {

    private static final PriceIndexDataset DATASET = PriceIndexDatasetLoader.bundled(IndexSeries.CPI);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<AnnualCpiEntry> getAll() {
        return DATASET.entries().stream()
                .map(PriceIndexEntry::year)
                .distinct()
                .sorted()
                .map(year -> new AnnualCpiEntry(year, DATASET.indexForYear(year)))
                .toList();
    }

    @GET
    @Path("/latest-year")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Integer> latestYear() {
        int latest = DATASET.entries().stream()
                .mapToInt(PriceIndexEntry::year)
                .max()
                .orElseThrow(() -> new IllegalStateException("CPI dataset is empty"));
        return Map.of("year", latest);
    }
}
