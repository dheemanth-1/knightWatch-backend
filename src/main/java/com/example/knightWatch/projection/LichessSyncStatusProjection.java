package com.example.knightWatch.projection;

public interface LichessSyncStatusProjection {
    String getLastSync();
    String getUsername();
    String getLastLocalGameDate();
    Integer getNumberOfGamesSynced();
    boolean isUptoDate();
}
