package com.example.knightWatch.dto.nodetrav;

public class OpeningStatsDTO {
    private Integer total;
    private Integer wins;
    private Integer draws;
    private Integer losses;
    private Double winRate;

    private ColorStatsDTO whiteStats;
    private ColorStatsDTO blackStats;

    public OpeningStatsDTO() {
    }

    public OpeningStatsDTO(Integer total, Integer wins, Integer draws, Integer losses, Double winRate, ColorStatsDTO whiteStats, ColorStatsDTO blackStats) {
        this.total = total;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.winRate = winRate;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getDraws() {
        return draws;
    }

    public void setDraws(Integer draws) {
        this.draws = draws;
    }

    public Integer getLosses() {
        return losses;
    }

    public void setLosses(Integer losses) {
        this.losses = losses;
    }

    public Double getWinRate() {
        return winRate;
    }

    public void setWinRate(Double winRate) {
        this.winRate = winRate;
    }

    public ColorStatsDTO getWhiteStats() {
        return whiteStats;
    }

    public void setWhiteStats(ColorStatsDTO whiteStats) {
        this.whiteStats = whiteStats;
    }

    public ColorStatsDTO getBlackStats() {
        return blackStats;
    }

    public void setBlackStats(ColorStatsDTO blackStats) {
        this.blackStats = blackStats;
    }
}
