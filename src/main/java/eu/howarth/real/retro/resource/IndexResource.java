package eu.howarth.real.retro.resource;

import eu.howarth.fin.priceindex.IndexSeries;
import eu.howarth.fin.priceindex.PriceIndexDataset;
import eu.howarth.fin.priceindex.PriceIndexSplicer;
import eu.howarth.fin.priceindex.PriceIndexEntry;
import eu.howarth.real.retro.dto.AnnualIndexEntry;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Annual ONS price-index series, aggregated from fin-model-rpi's bundled
 * monthly dataset — one route for every {@link IndexSeries} rather than a
 * hand-copied resource per series (kanban #1007, replaces RpiResource +
 * CpiResource). A series with a {@link IndexSeries#splicesInto()} target
 * (e.g. {@code RPI_MILLENNIUM}) is served already spliced onto its live
 * counterpart, so it reaches the present day rather than stopping wherever
 * its own static bundle ends (kanban #1003). Stateless — no database, no
 * dataset persistence (that lives client-side in the UI's localStorage,
 * see kanban #874 comment #1586).
 */
@Path("/api/index/{series}")
public class IndexResource {

    private static final Map<IndexSeries, PriceIndexDataset> DATASETS = new EnumMap<>(IndexSeries.class);

    static {
        for (IndexSeries series : IndexSeries.values()) {
            DATASETS.put(series, PriceIndexSplicer.spliceToPresent(series));
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<AnnualIndexEntry> getAll(@PathParam("series") String series) {
        PriceIndexDataset dataset = dataset(series);
        return dataset.entries().stream()
                .map(PriceIndexEntry::year)
                .distinct()
                .sorted()
                .map(year -> new AnnualIndexEntry(year, dataset.indexForYear(year)))
                .toList();
    }

    @GET
    @Path("/latest-year")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Integer> latestYear(@PathParam("series") String series) {
        PriceIndexDataset dataset = dataset(series);
        int latest = dataset.entries().stream()
                .mapToInt(PriceIndexEntry::year)
                .max()
                .orElseThrow(() -> new IllegalStateException(series + " dataset is empty"));
        return Map.of("year", latest);
    }

    private PriceIndexDataset dataset(String series) {
        IndexSeries parsed;
        try {
            // Path segments are kebab-case (e.g. "rpi-millennium"); IndexSeries
            // constants use underscores.
            parsed = IndexSeries.valueOf(series.toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Unknown index series: " + series);
        }
        return DATASETS.get(parsed);
    }
}
