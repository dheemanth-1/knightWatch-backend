package com.example.knightWatch.dto.nodetrav;

public class ColorStatsDTO {
    private Integer total;
    private Integer wins;
    private Integer draws;
    private Integer losses;
    private Double winRate;

    public ColorStatsDTO() {
    }

    public ColorStatsDTO(Integer total, Integer wins, Integer draws, Integer losses, Double winRate) {
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
}
