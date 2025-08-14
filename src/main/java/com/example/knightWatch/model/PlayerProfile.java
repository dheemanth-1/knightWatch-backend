package com.example.knightWatch.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "player_profile")
public class PlayerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private int totalGames;

    private double winRate;

    private LocalDateTime lastSyncTime;

    public PlayerProfile() {}

    public PlayerProfile(String username, int totalGames, double winRate, LocalDateTime lastSyncTime) {
        this.username = username;
        this.totalGames = totalGames;
        this.winRate = winRate;
        this.lastSyncTime = lastSyncTime;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public LocalDateTime getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(LocalDateTime lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }
}