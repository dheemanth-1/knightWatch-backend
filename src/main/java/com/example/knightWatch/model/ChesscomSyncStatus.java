package com.example.knightWatch.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chesscom_sync_status")
public class ChesscomSyncStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long syncId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private LocalDateTime syncedAt;

    private Integer year;

    private Integer month;

    private Integer gameCountThisMonth;

    private Long totalGames;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public ChesscomSyncStatus() {}

    public ChesscomSyncStatus(String username, LocalDateTime syncedAt, Integer year, Integer month, Integer gameCountThisMonth, Long totalGames) {
        this.username = username;
        this.syncedAt = syncedAt;
        this.year = year;
        this.month = month;
        this.gameCountThisMonth = gameCountThisMonth;
        this.totalGames = totalGames;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getSyncedAt() {
        return syncedAt;
    }

    public void setSyncedAt(LocalDateTime syncedAt) {
        this.syncedAt = syncedAt;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getGameCountThisMonth() {
        return gameCountThisMonth;
    }

    public void setGameCountThisMonth(Integer gameCountThisMonth) {
        this.gameCountThisMonth = gameCountThisMonth;
    }

    public Long getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(Long totalGames) {
        this.totalGames = totalGames;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
