package eu.howarth.real.income.controller;

import eu.howarth.real.income.model.RpiEntry;
import eu.howarth.real.income.service.RpiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rpi")
public class RpiController {

    private final RpiService rpiService;

    public RpiController(RpiService rpiService) {
        this.rpiService = rpiService;
    }

    /** All annual RPI entries — used by UI to populate slider range and calculate adjustments */
    @GetMapping
    public List<RpiEntry> getAll() {
        return rpiService.getAll();
    }

    /** Latest available year — UI defaults slider to this */
    @GetMapping("/latest-year")
    public Map<String, Integer> latestYear() {
        return Map.of("year", rpiService.getLatestYear());
    }
}
