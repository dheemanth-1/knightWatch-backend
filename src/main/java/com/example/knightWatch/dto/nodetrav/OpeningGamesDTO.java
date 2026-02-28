package com.example.knightWatch.dto.nodetrav;


public class OpeningGamesDTO {
    private OpeningNodeDTO opening;
    private GamesByColorDTO games;

    public OpeningGamesDTO() {
    }

    public OpeningGamesDTO(OpeningNodeDTO opening, GamesByColorDTO games) {
        this.opening = opening;
        this.games = games;
    }

    public OpeningNodeDTO getOpening() {
        return opening;
    }

    public void setOpening(OpeningNodeDTO opening) {
        this.opening = opening;
    }

    public GamesByColorDTO getGames() {
        return games;
    }

    public void setGames(GamesByColorDTO games) {
        this.games = games;
    }
}