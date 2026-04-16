package com.example.knightWatch.dto.nodetrav;

import java.util.List;

public class GamesByColorDTO {
    private List<GameSummaryDTO> white;
    private List<GameSummaryDTO> black;

    public GamesByColorDTO() {
    }

    public GamesByColorDTO(List<GameSummaryDTO> white, List<GameSummaryDTO> black) {
        this.white = white;
        this.black = black;
    }

    public List<GameSummaryDTO> getWhite() {
        return white;
    }

    public void setWhite(List<GameSummaryDTO> white) {
        this.white = white;
    }

    public List<GameSummaryDTO> getBlack() {
        return black;
    }

    public void setBlack(List<GameSummaryDTO> black) {
        this.black = black;
    }
}