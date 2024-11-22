package com.MOF_ITMD.DepWeb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "specification_doc")
public class SpecifyDoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "doc_name")
    private String docName;

    @Column(name = "doc_type")
    private String docType;

    @Column(name = "doc_path")
    private String docPath;

    @Column(name = "doc_status")
    private int docStatus;

    @Column(name = "latest_update_at")
    private LocalDateTime latestUpdateAt;

    @Column(name = "uploaded_at")
    private LocalDateTime createdAt;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "hard_speci_id")
    @JsonBackReference
    private HardwareSpecification hardwareSpecification;

    public SpecifyDoc() {
    }

    public SpecifyDoc(String docName, String docType, String docPath, int docStatus, LocalDateTime latestUpdateAt, LocalDateTime createdAt) {
        this.docName = docName;
        this.docType = docType;
        this.docPath = docPath;
        this.docStatus = docStatus;
        this.latestUpdateAt = latestUpdateAt;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocPath() {
        return docPath;
    }

    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public HardwareSpecification getHardwareSpecification() {
        return hardwareSpecification;
    }

    public void setHardwareSpecification(HardwareSpecification hardwareSpecification) {
        this.hardwareSpecification = hardwareSpecification;
    }

    public int getDocStatus() {
        return docStatus;
    }

    public void setDocStatus(int docStatus) {
        this.docStatus = docStatus;
    }

    public LocalDateTime getLatestUpdateAt() {
        return latestUpdateAt;
    }

    public void setLatestUpdateAt(LocalDateTime latestUpdateAt) {
        this.latestUpdateAt = latestUpdateAt;
    }
}
