package eu.howarth.real.income.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import eu.howarth.real.income.model.RpiEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Loads ONS RPI CHAW annual series.
 * On startup: loads bundled CSV from classpath (data/rpi_chaw.csv).
 * Daily: attempts to refresh from ONS — falls back to bundled data on failure.
 */
@Service
public class RpiService {

    private static final Logger log = LoggerFactory.getLogger(RpiService.class);

    private static final String ONS_URL =
            "https://www.ons.gov.uk/generator?format=csv&uri=/economy/inflationandpriceindices/timeseries/chaw/mm23";

    // year -> RPI index value
    private final Map<Integer, Double> rpiByYear = new TreeMap<>();

    public RpiService() {
        loadBundled();
    }

    public List<RpiEntry> getAll() {
        return rpiByYear.entrySet().stream()
                .map(e -> new RpiEntry(e.getKey(), e.getValue()))
                .toList();
    }

    public double getIndex(int year) {
        Double index = rpiByYear.get(year);
        if (index == null) throw new IllegalArgumentException("No RPI data for year: " + year);
        return index;
    }

    public int getLatestYear() {
        return rpiByYear.isEmpty() ? 0 : ((TreeMap<Integer, Double>) rpiByYear).lastKey();
    }

    /** Refresh from ONS once per day at 03:00 */
    @Scheduled(cron = "0 0 3 * * *")
    public void refreshFromOns() {
        log.info("Refreshing RPI data from ONS...");
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ONS_URL))
                    .header("User-Agent", "real-income-api/1.0")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                parseCsv(response.body().lines().toList());
                log.info("RPI data refreshed — {} annual entries, latest year: {}", rpiByYear.size(), getLatestYear());
            } else {
                log.warn("ONS returned {}, keeping existing data", response.statusCode());
            }
        } catch (Exception e) {
            log.warn("ONS refresh failed: {} — keeping existing data", e.getMessage());
        }
    }

    private void loadBundled() {
        try (var stream = getClass().getResourceAsStream("/data/rpi_chaw.csv")) {
            if (stream == null) {
                log.warn("Bundled rpi_chaw.csv not found — RPI data will be empty until refresh");
                return;
            }
            try (CSVReader reader = new CSVReader(new InputStreamReader(stream))) {
                List<String[]> rows = reader.readAll();
                List<String> lines = rows.stream().map(r -> String.join(",", r)).toList();
                parseCsv(lines);
                log.info("Loaded bundled RPI data — {} annual entries, latest year: {}", rpiByYear.size(), getLatestYear());
            }
        } catch (IOException | CsvException e) {
            log.error("Failed to load bundled RPI data", e);
        }
    }

    /**
     * ONS CSV format (annual rows look like):
     *   1987,100
     *   1988,104.9
     * Rows that don't parse as integer year are skipped (headers, notes, monthly rows etc.)
     */
    private void parseCsv(List<String> lines) {
        List<RpiEntry> parsed = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length < 2) continue;
            try {
                int year = Integer.parseInt(parts[0].trim());
                double index = Double.parseDouble(parts[1].trim());
                if (year >= 1987 && year <= 2100) {
                    parsed.add(new RpiEntry(year, index));
                }
            } catch (NumberFormatException ignored) {
                // skip header/notes rows
            }
        }
        if (!parsed.isEmpty()) {
            rpiByYear.clear();
            parsed.forEach(e -> rpiByYear.put(e.year(), e.index()));
        }
    }
}
