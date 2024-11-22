package com.MOF_ITMD.DepWeb.dto;

import java.time.LocalDateTime;

public class UpdateStatusDTO {
    private int status;
    private LocalDateTime updateAt;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
}
