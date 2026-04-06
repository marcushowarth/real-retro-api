package eu.howarth.real.income.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Entity
@Table(name = "data_point")
public class DataPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String datasetId;   // groups points into a named dataset (user-defined UUID)

    @NotBlank
    private String seriesName;  // e.g. "My Salary", "House Price"

    private String colour;      // optional hex colour

    @NotNull
    private LocalDate date;

    @NotNull
    @Positive
    private double amount;

    public DataPoint() {}

    public DataPoint(String datasetId, String seriesName, String colour, LocalDate date, double amount) {
        this.datasetId = datasetId;
        this.seriesName = seriesName;
        this.colour = colour;
        this.date = date;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public String getDatasetId() { return datasetId; }
    public String getSeriesName() { return seriesName; }
    public String getColour() { return colour; }
    public LocalDate getDate() { return date; }
    public double getAmount() { return amount; }

    public void setDatasetId(String datasetId) { this.datasetId = datasetId; }
    public void setSeriesName(String seriesName) { this.seriesName = seriesName; }
    public void setColour(String colour) { this.colour = colour; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setAmount(double amount) { this.amount = amount; }
}
