package com.example.knightWatch.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sync_status")
public class SyncStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long syncId;
    private String username;
    private String lastSync;
    private String lastLocalGameDate;
    private Integer numberOfGamesSynced;
    private boolean isUptoDate;

    private SyncStatus() {}


    public SyncStatus(String lastSync, String username, String lastLocalGameDate, Integer numberOfGamesSynced, boolean isUptoDate) {
        this.lastSync = lastSync;
        this.username = username;
        this.lastLocalGameDate = lastLocalGameDate;
        this.numberOfGamesSynced = numberOfGamesSynced;
        this.isUptoDate = isUptoDate;
    }

    public String getLastSync() {
        return lastSync;
    }

    public void setLastSync(String lastSync) {
        this.lastSync = lastSync;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastLocalGameDate() {
        return lastLocalGameDate;
    }

    public void setLastLocalGameDate(String lastLocalGameDate) {
        this.lastLocalGameDate = lastLocalGameDate;
    }

    public Integer getNumberOfGamesSynced() {
        return numberOfGamesSynced;
    }

    public void setNumberOfGamesSynced(Integer numberOfGamesSynced) {
        this.numberOfGamesSynced = numberOfGamesSynced;
    }

    public boolean isUptoDate() {
        return isUptoDate;
    }

    public void setUptoDate(boolean uptoDate) {
        isUptoDate = uptoDate;
    }
}
