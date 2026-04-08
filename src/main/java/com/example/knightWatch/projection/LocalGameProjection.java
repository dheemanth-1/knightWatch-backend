package com.example.knightWatch.projection;

import java.time.LocalDateTime;

public interface LocalGameProjection {
    Long getId();
    String getUsername();
    String getGameId();
    String getOpeningName();
    String getResult();
    LocalDateTime getPlayedAt();
    String getPgn();
    String getEco();
    String getStatus();
    String getSource();
    String getPlayerColor();
    String getPgnPath();
    String getEvent();
    Integer getTimeControl();
    Integer getPlayerElo();
    Integer getOpponentElo();
}
