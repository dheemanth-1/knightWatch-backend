package com.example.knightWatch.dto;

public class TotalGamesCount {
    private String username;
    private Integer totalRatedGames;

    public TotalGamesCount(String username, Integer totalRatedGames) {
        this.username = username;
        this.totalRatedGames = totalRatedGames;
    }

    public Integer getTotalRatedGames() {
        return totalRatedGames;
    }

    public void setTotalRatedGames(Integer totalRatedGames) {
        this.totalRatedGames = totalRatedGames;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
