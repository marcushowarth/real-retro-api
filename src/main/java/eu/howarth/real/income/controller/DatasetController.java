package eu.howarth.real.income.controller;

import eu.howarth.real.income.model.DataPoint;
import eu.howarth.real.income.model.Dataset;
import eu.howarth.real.income.service.DatasetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/datasets")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @GetMapping
    public List<Dataset> listAll() {
        return datasetService.listAll();
    }

    @GetMapping("/{id}")
    public Dataset getById(@PathVariable String id) {
        return datasetService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public Dataset createOrUpdate(@PathVariable String id, @Valid @RequestBody Dataset dataset) {
        return datasetService.createOrUpdate(dataset);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        datasetService.deleteDataset(id);
    }

    // --- Data points ---

    @GetMapping("/{id}/points")
    public List<DataPoint> getPoints(@PathVariable String id) {
        return datasetService.getPoints(id);
    }

    /** Replace all points for a dataset — used by CSV import */
    @PutMapping("/{id}/points")
    public List<DataPoint> replacePoints(@PathVariable String id, @RequestBody List<DataPoint> points) {
        return datasetService.replacePoints(id, points);
    }

    @PostMapping("/{id}/points")
    @ResponseStatus(HttpStatus.CREATED)
    public DataPoint addPoint(@PathVariable String id, @Valid @RequestBody DataPoint point) {
        point.setDatasetId(id);
        return datasetService.addPoint(point);
    }

    @DeleteMapping("/{id}/points/{pointId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePoint(@PathVariable String id, @PathVariable Long pointId) {
        datasetService.deletePoint(pointId);
    }
}
