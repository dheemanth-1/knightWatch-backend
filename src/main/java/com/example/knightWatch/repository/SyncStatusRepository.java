package com.example.knightWatch.repository;

import com.example.knightWatch.model.LichessSyncStatus;
import com.example.knightWatch.projection.LichessSyncStatusProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SyncStatusRepository extends JpaRepository<LichessSyncStatus, Long> {
    LichessSyncStatus findFirstByUsernameOrderBySyncIdDesc(String username);
    List<LichessSyncStatus> findAllByUsername(String username);
    void deleteAllByUsername(String username);
    boolean existsByUsername(String username);
    long countByUsername(String username);

    LichessSyncStatus findFirstByUsernameAndUserIdOrderBySyncIdDesc(String username, long userId);
    List<LichessSyncStatus> findAllByUsernameAndUserId(String username, long userId);
    List<LichessSyncStatusProjection> findAllProjectedByUsernameAndUserId(String username, long userId);
    void deleteAllByUsernameAndUserId(String username, long userId);
    boolean existsByUsernameAndUserId(String username, long userId);
    long countByUsernameAndUserId(String username, long userId);
}
