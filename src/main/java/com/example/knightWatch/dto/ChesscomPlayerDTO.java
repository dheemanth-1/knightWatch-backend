package com.example.knightWatch.dto;

public record ChesscomPlayerDTO(
        String userId,
        String name,
        String country,
        String accStatus,
        Long joined,
        Long last_online,
        Integer totalGamesBlitz,
        Integer totalGamesRapid,
        Integer totalGamesBullet,
        Integer totalGamesClassical,
        Integer blitzRating,
        Integer rapidRating,
        Integer bulletRating,
        Integer classicRating,
        Integer puzzleRating,
        String url
) {
}
