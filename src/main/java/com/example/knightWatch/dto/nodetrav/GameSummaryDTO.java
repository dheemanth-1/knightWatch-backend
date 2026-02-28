package com.example.knightWatch.dto.nodetrav;


import java.time.LocalDateTime;

public class GameSummaryDTO {
    private Long gameId;
    private String gameIdExternal;
    private String player;
    private String result;
    private LocalDateTime playedAt;
    private String source;

    public GameSummaryDTO() {
    }

    public GameSummaryDTO(Long gameId, String gameIdExternal, String player, String result, LocalDateTime playedAt, String source) {
        this.gameId = gameId;
        this.gameIdExternal = gameIdExternal;
        this.player = player;
        this.result = result;
        this.playedAt = playedAt;
        this.source = source;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getGameIdExternal() {
        return gameIdExternal;
    }

    public void setGameIdExternal(String gameIdExternal) {
        this.gameIdExternal = gameIdExternal;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}