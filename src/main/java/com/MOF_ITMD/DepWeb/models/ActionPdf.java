package com.MOF_ITMD.DepWeb.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "action_pdf")
public class ActionPdf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_pdf_id")
    private Long id;
    @Column(name = "file_name")
    private String actionPdfName;
    @Column(name = "file_path")
    private String actionPdfPath;
    @Column(name = "image_file_name")
    private String actionImageName;
    @Column(name = "image_file_path")
    private String actionImagePath;
    @Column(name = "status")
    private int actionStatus;
    @Column(name = "added_pdf_at")
    private LocalDateTime addedPdfAt;
    @Column(name = "latest_status_update_at")
    private LocalDateTime latestStatusUpdateAt;

    public ActionPdf() {
    }

    public ActionPdf(String actionPdfName, String actionPdfPath, String actionImageName, String actionImagePath, int actionStatus, LocalDateTime addedPdfAt, LocalDateTime latestStatusUpdateAt) {
        this.actionPdfName = actionPdfName;
        this.actionPdfPath = actionPdfPath;
        this.actionImageName = actionImageName;
        this.actionImagePath = actionImagePath;
        this.actionStatus = actionStatus;
        this.addedPdfAt = addedPdfAt;
        this.latestStatusUpdateAt = latestStatusUpdateAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActionPdfName() {
        return actionPdfName;
    }

    public void setActionPdfName(String actionPdfName) {
        this.actionPdfName = actionPdfName;
    }

    public String getActionPdfPath() {
        return actionPdfPath;
    }

    public void setActionPdfPath(String actionPdfPath) {
        this.actionPdfPath = actionPdfPath;
    }

    public String getActionImageName() {
        return actionImageName;
    }

    public void setActionImageName(String actionImageName) {
        this.actionImageName = actionImageName;
    }

    public String getActionImagePath() {
        return actionImagePath;
    }

    public void setActionImagePath(String actionImagePath) {
        this.actionImagePath = actionImagePath;
    }

    public LocalDateTime getAddedPdfAt() {
        return addedPdfAt;
    }

    public void setAddedPdfAt(LocalDateTime addedPdfAt) {
        this.addedPdfAt = addedPdfAt;
    }

    public int getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(int actionStatus) {
        this.actionStatus = actionStatus;
    }

    public LocalDateTime getLatestStatusUpdateAt() {
        return latestStatusUpdateAt;
    }

    public void setLatestStatusUpdateAt(LocalDateTime latestStatusUpdateAt) {
        this.latestStatusUpdateAt = latestStatusUpdateAt;
    }
}
