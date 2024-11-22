package com.MOF_ITMD.DepWeb.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hardware_specification")
public class HardwareSpecification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "specification_type")
    private String specificationType;

    @Column(name = "status")
    private int status;

    @Column(name = "specification_added_date")
    private LocalDateTime speCreatedAt;

    @Column(name = "latest_update_at")
    private LocalDateTime latestUpdateAt;

    @OneToMany(mappedBy = "hardwareSpecification", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<SpecifyDoc> specifyDocs = new ArrayList<>();

    public HardwareSpecification() {
    }

    public HardwareSpecification(String specificationType, int status, LocalDateTime speCreatedAt, LocalDateTime latestUpdateAt) {
        this.specificationType = specificationType;
        this.status = status;
        this.speCreatedAt = speCreatedAt;
        this.latestUpdateAt = latestUpdateAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecificationType() {
        return specificationType;
    }

    public void setSpecificationType(String specificationType) {
        this.specificationType = specificationType;
    }

    public LocalDateTime getSpeCreatedAt() {
        return speCreatedAt;
    }

    public void setSpeCreatedAt(LocalDateTime speCreatedAt) {
        this.speCreatedAt = speCreatedAt;
    }

    public List<SpecifyDoc> getSpecifyDocs() {
        return specifyDocs;
    }

    public void setSpecifyDocs(List<SpecifyDoc> specifyDocs) {
        this.specifyDocs = specifyDocs;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getLatestUpdateAt() {
        return latestUpdateAt;
    }

    public void setLatestUpdateAt(LocalDateTime latestUpdateAt) {
        this.latestUpdateAt = latestUpdateAt;
    }
}
