package eu.howarth.real.income.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Entity
@Table(name = "dataset")
public class Dataset {

    @Id
    private String id;          // client-generated UUID

    @NotBlank
    private String name;        // user-facing label e.g. "Salary history"

    private Instant createdAt;
    private Instant updatedAt;

    public Dataset() {}

    public Dataset(String id, String name) {
        this.id = id;
        this.name = name;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setName(String name) { this.name = name; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
