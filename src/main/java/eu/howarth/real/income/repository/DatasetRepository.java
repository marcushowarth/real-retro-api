package eu.howarth.real.income.repository;

import eu.howarth.real.income.model.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatasetRepository extends JpaRepository<Dataset, String> {}
