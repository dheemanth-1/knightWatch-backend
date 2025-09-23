package com.example.knightWatch.model;

import chariot.model.Game;
import com.example.knightWatch.dto.OpeningInfo;
import jakarta.persistence.*;

@Entity
@Table(name = "lichess_games")
public class LichessGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getOpeningName() {
        return openingName;
    }

    public void setOpeningName(String openingName) {
        this.openingName = openingName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(String playedAt) {
        this.playedAt = playedAt;
    }

    public String getPgn() {
        return pgn;
    }

    public void setPgn(String pgn) {
        this.pgn = pgn;
    }

    public String getEco() {
        return eco;
    }

    public void setEco(String eco) {
        this.eco = eco;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String username;
    private String gameId;
    private String openingName;
    private String result;
    private String playedAt;
    private String pgn;
    private String eco;
    private String status;

    public LichessGame() {}

    public LichessGame(chariot.model.Game game, String username) {
        if (username == null || game == null) {
            throw new IllegalArgumentException("Invalid game data");
        }
        this.username = username;
        this.gameId = game.id();
        this.openingName = game.opening()
                .map(Game.Opening::name)
                .orElse(null);
        this.status = game.status().name();
        this.playedAt = game.createdAt().toString(); // ISO date
    }


    public LichessGame(OpeningInfo openingInfo, String username) {
        this.username = username;
        this.gameId = openingInfo.getGameId();
        this.openingName = openingInfo.getBestOpeningName();
        this.result = openingInfo.getResultFromColor(username);
        this.playedAt = openingInfo.getPlayedAt();
        this.pgn = openingInfo.getPgn();
        this.eco = openingInfo.getEco();
        this.status = openingInfo.getStatus();
    }


    @Override
    public String toString() {
        return "LichessGame{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", gameId='" + gameId + '\'' +
                ", openingName='" + openingName + '\'' +
                ", result='" + result + '\'' +
                ", playedAt='" + playedAt + '\'' +
                ", pgn='" + pgn + '\'' +
                ", eco='" + eco + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}