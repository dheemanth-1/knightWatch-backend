package com.example.knightWatch.projection;

public interface LocalGameProjection {
    Long getId();
    String getUsername();
    String getGameId();
    String getOpeningName();
    String getResult();
    String getPlayedAt();
    String getPgn();
    String getEco();
    String getStatus();
    String getSource();
}
