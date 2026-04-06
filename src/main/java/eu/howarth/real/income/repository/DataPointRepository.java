package eu.howarth.real.income.repository;

import eu.howarth.real.income.model.DataPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DataPointRepository extends JpaRepository<DataPoint, Long> {
    List<DataPoint> findByDatasetIdOrderByDate(String datasetId);
    void deleteByDatasetId(String datasetId);
}
