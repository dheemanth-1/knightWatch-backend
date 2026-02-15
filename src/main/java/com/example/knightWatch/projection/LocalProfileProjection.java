package com.example.knightWatch.projection;

public interface LocalProfileProjection {
    Long getId();
    String getUsername();
    int getTotalGames();
    int getRatedGames();
    int getBlitzRating();
    int getBulletRating();
    int getRapidRating();
    int getClassicalRating();
    int getPuzzleRating();
    String getSource();
}
