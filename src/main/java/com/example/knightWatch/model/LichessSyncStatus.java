package com.example.knightWatch.model;

import jakarta.persistence.*;


@Entity
@Table(name = "lichess_sync_status")
public class LichessSyncStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long syncId;
    @Column(nullable = false)
    private String username;
    private String lastSync;
    private String lastLocalGameDate;
    private Integer numberOfGamesSynced;
    private boolean uptoDate;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    private LichessSyncStatus() {}

    public LichessSyncStatus(String lastSync, String username, String lastLocalGameDate, Integer numberOfGamesSynced, boolean uptoDate) {
        this.lastSync = lastSync;
        this.username = username;
        this.lastLocalGameDate = lastLocalGameDate;
        this.numberOfGamesSynced = numberOfGamesSynced;
        this.uptoDate = uptoDate;
    }

    @Override
    public String toString() {
        return "SyncStatus{" +
                "syncId=" + syncId +
                ", username='" + username + '\'' +
                ", lastSync='" + lastSync + '\'' +
                ", lastLocalGameDate='" + lastLocalGameDate + '\'' +
                ", numberOfGamesSynced=" + numberOfGamesSynced +
                ", uptoDate=" + uptoDate +
                '}';
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
        return uptoDate;
    }

    public void setUptoDate(boolean uptoDate) {
        this.uptoDate = uptoDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
