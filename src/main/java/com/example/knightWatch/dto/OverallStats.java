package com.example.knightWatch.dto;

public class OverallStats {
    private double winRate;
    private double lossRate;
    private double drawRate;

    public OverallStats() {
    }

    public OverallStats(double winRate, double lossRate, double drawRate) {
        this.winRate = winRate;
        this.lossRate = lossRate;
        this.drawRate = drawRate;
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

}