package com.example.knightWatch.dto;

import java.time.LocalDateTime;

public class SyncStatusDTO {
    private LocalDateTime lastSync;
    private boolean isSyncUpToDate;
    private String lastLocalGameDate;
    private Integer gamesSynced;

    public SyncStatusDTO(){

    }

    public SyncStatusDTO(LocalDateTime lastSync, boolean isSyncUpToDate, String lastLocalGameDate, Integer gamesSynced) {
        this.lastSync = lastSync;
        this.isSyncUpToDate = isSyncUpToDate;
        this.lastLocalGameDate = lastLocalGameDate;
        this.gamesSynced = gamesSynced;
    }

    public LocalDateTime getLastSync() {
        return lastSync;
    }

    public void setLastSync(LocalDateTime lastSync) {
        this.lastSync = lastSync;
    }

    public boolean isSyncUpToDate() {
        return isSyncUpToDate;
    }

    public void setSyncUpToDate(boolean syncUpToDate) {
        isSyncUpToDate = syncUpToDate;
    }

    public String getLastLocalGameDate() {
        return lastLocalGameDate;
    }

    public void setLastLocalGameDate(String lastLocalGameDate) {
        this.lastLocalGameDate = lastLocalGameDate;
    }

    public Integer getGamesSynced() {
        return gamesSynced;
    }

    public void setGamesSynced(Integer gamesSynced) {
        this.gamesSynced = gamesSynced;
    }
}


