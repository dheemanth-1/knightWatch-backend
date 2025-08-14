package com.example.knightWatch.dto;

public class GameStatsByOpening {
    private String openingName;
    private long numWins;
    private long numLosses;
    private long numDraws;
    private double winRate;
    private double lossRate;
    private double drawRate;

    public GameStatsByOpening(String openingName, long numWins, long numLosses, long numDraws, double winRate, double lossRate, double drawRate) {
        this.openingName = openingName;
        this.numWins = numWins;
        this.numLosses = numLosses;
        this.numDraws = numDraws;
        this.winRate = winRate;
        this.lossRate = lossRate;
        this.drawRate = drawRate;
    }

    public GameStatsByOpening(){

    }

    public String getOpeningName() {
        return openingName;
    }

    public void setOpeningName(String openingName) {
        this.openingName = openingName;
    }

    public long getNumWins() {
        return numWins;
    }

    public void setNumWins(long numWins) {
        this.numWins = numWins;
    }

    public long getNumLosses() {
        return numLosses;
    }

    public void setNumLosses(long numLosses) {
        this.numLosses = numLosses;
    }

    public long getNumDraws() {
        return numDraws;
    }

    public void setNumDraws(long numDraws) {
        this.numDraws = numDraws;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
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
}
