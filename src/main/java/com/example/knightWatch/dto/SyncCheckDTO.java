package com.example.knightWatch.dto;

import java.time.ZonedDateTime;

public record SyncCheckDTO(Boolean freshSync,
                           Boolean latestGameMatch,
                           Integer requestedGamesPending,
                           ZonedDateTime earliestGameDateTime) {
}
