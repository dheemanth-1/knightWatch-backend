package com.example.knightWatch.model;

import chariot.model.Game;
import com.example.knightWatch.dto.OpeningInfo;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "local_games")
public class LocalGame {

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

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    public LocalProfile getLocalProfile() {
        return localProfile;
    }

    public void setLocalProfile(LocalProfile localProfile) {
        this.localProfile = localProfile;
    }

    private String username;
    private String gameId;
    private String openingName;
    private String result;
    private LocalDateTime playedAt;
    @Column(name = "pgn", columnDefinition = "text")
    private String pgn;
    private String eco;
    private String status;
    private String source;
    @Column(name="player_color")
    private String playerColor;

    @ManyToOne
    @JoinColumn(name = "local_profile_id")
    private LocalProfile localProfile;

    public LocalGame() {}

    public LocalGame(chariot.model.Game game, String username) {
        if (username == null || game == null) {
            throw new IllegalArgumentException("Invalid game data");
        }
        this.username = username;
        this.gameId = game.id();
        this.openingName = game.opening()
                .map(Game.Opening::name)
                .orElse(null);
        this.status = game.status().name();
        this.playedAt = LocalDateTime.parse(game.createdAt().toString()); // ISO date
    }


    public LocalGame(OpeningInfo openingInfo) {
        this.username = openingInfo.getUsername();
        this.gameId = openingInfo.getGameId();
        this.openingName = openingInfo.getBestOpeningName();
        this.result = openingInfo.getResultFromColor(username);
        this.playedAt = LocalDateTime.parse(openingInfo.getPlayedAt(), DateTimeFormatter.ofPattern("yyyy.MM.dd'T'HH:mm:ss"));
        this.pgn = openingInfo.getPgn();
        this.eco = openingInfo.getEco();
        this.status = openingInfo.getStatus();
        this.source = openingInfo.getSource();
        this.playerColor = openingInfo.getPlayerColor();
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
                ", source='" + source + '\'' +
                '}';
    }
}