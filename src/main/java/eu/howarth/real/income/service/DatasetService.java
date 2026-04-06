package eu.howarth.real.income.service;

import eu.howarth.real.income.model.DataPoint;
import eu.howarth.real.income.model.Dataset;
import eu.howarth.real.income.repository.DataPointRepository;
import eu.howarth.real.income.repository.DatasetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class DatasetService {

    private final DatasetRepository datasetRepo;
    private final DataPointRepository dataPointRepo;

    public DatasetService(DatasetRepository datasetRepo, DataPointRepository dataPointRepo) {
        this.datasetRepo = datasetRepo;
        this.dataPointRepo = dataPointRepo;
    }

    public List<Dataset> listAll() {
        return datasetRepo.findAll();
    }

    public Optional<Dataset> findById(String id) {
        return datasetRepo.findById(id);
    }

    @Transactional
    public Dataset createOrUpdate(Dataset dataset) {
        dataset.setUpdatedAt(Instant.now());
        return datasetRepo.save(dataset);
    }

    @Transactional
    public void deleteDataset(String id) {
        dataPointRepo.deleteByDatasetId(id);
        datasetRepo.deleteById(id);
    }

    public List<DataPoint> getPoints(String datasetId) {
        return dataPointRepo.findByDatasetIdOrderByDate(datasetId);
    }

    @Transactional
    public List<DataPoint> replacePoints(String datasetId, List<DataPoint> points) {
        dataPointRepo.deleteByDatasetId(datasetId);
        points.forEach(p -> p.setDatasetId(datasetId));
        return dataPointRepo.saveAll(points);
    }

    @Transactional
    public DataPoint addPoint(DataPoint point) {
        return dataPointRepo.save(point);
    }

    @Transactional
    public void deletePoint(Long pointId) {
        dataPointRepo.deleteById(pointId);
    }
}
