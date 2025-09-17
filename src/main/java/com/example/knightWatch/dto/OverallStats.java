package com.example.knightWatch.dto;

public class OverallStats {
    private double winRate;
    private double lossRate;
    private double drawRate;
    private long numberOfGames;

    public OverallStats() {
    }

    public OverallStats(double winRate, double lossRate, double drawRate, long numberOfGames) {
        this.winRate = winRate;
        this.lossRate = lossRate;
        this.drawRate = drawRate;
        this.numberOfGames = numberOfGames;

    }

    public double getLossRate() {
        return lossRate;
    }

    public void setLossRate(double lossRate) {
        this.lossRate = lossRate;
    }

    public double getDrawRate() {
        return drawRate;
    }

    public void setDrawRate(double drawRate) {
        this.drawRate = drawRate;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public long getNumberOfGames() {
        return numberOfGames;
    }

    public void setNumberOfGames(long numberOfGames) {
        this.numberOfGames = numberOfGames;
    }
}