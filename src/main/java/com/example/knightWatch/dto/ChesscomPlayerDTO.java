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
        Integer blitzRating,
        Integer rapidRating,
        String url
) {
}
