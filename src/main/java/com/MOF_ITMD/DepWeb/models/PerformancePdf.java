package com.MOF_ITMD.DepWeb.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "performance_pdf")
public class PerformancePdf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "pdf_name")
    private String performancePdfName;
    @Column(name = "pdf_path")
    private String performancePdfPath;
    @Column(name = "image_file_name")
    private String performanceImageName;
    @Column(name = "image_file_path")
    private String performanceImagePath;
    @Column(name = "status")
    private int performanceStatus;
    @Column(name = "added_pdf_at")
    private LocalDateTime addedPdfAt;
    @Column(name = "latest_update_at")
    private LocalDateTime latestUpdateAt;

    public PerformancePdf() {
    }

    public PerformancePdf(String performancePdfName, String performancePdfPath, String performanceImageName, String performanceImagePath, int performanceStatus, LocalDateTime addedPdfAt, LocalDateTime latestUpdateAt) {
        this.performancePdfName = performancePdfName;
        this.performancePdfPath = performancePdfPath;
        this.performanceImageName = performanceImageName;
        this.performanceImagePath = performanceImagePath;
        this.performanceStatus = performanceStatus;
        this.addedPdfAt = addedPdfAt;
        this.latestUpdateAt = latestUpdateAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPerformancePdfName() {
        return performancePdfName;
    }

    public void setPerformancePdfName(String performancePdfName) {
        this.performancePdfName = performancePdfName;
    }

    public String getPerformancePdfPath() {
        return performancePdfPath;
    }

    public void setPerformancePdfPath(String performancePdfPath) {
        this.performancePdfPath = performancePdfPath;
    }

    public String getPerformanceImageName() {
        return performanceImageName;
    }

    public void setPerformanceImageName(String performanceImageName) {
        this.performanceImageName = performanceImageName;
    }

    public String getPerformanceImagePath() {
        return performanceImagePath;
    }

    public void setPerformanceImagePath(String performanceImagePath) {
        this.performanceImagePath = performanceImagePath;
    }

    public int getPerformanceStatus() {
        return performanceStatus;
    }

    public void setPerformanceStatus(int performanceStatus) {
        this.performanceStatus = performanceStatus;
    }

    public LocalDateTime getAddedPdfAt() {
        return addedPdfAt;
    }

    public void setAddedPdfAt(LocalDateTime addedPdfAt) {
        this.addedPdfAt = addedPdfAt;
    }

    public LocalDateTime getLatestUpdateAt() {
        return latestUpdateAt;
    }

    public void setLatestUpdateAt(LocalDateTime latestUpdateAt) {
        this.latestUpdateAt = latestUpdateAt;
    }
}
