package com.example.knightWatch.repository;

import com.example.knightWatch.model.LichessSyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SyncStatusRepository extends JpaRepository<LichessSyncStatus, Long> {
    LichessSyncStatus findFirstByUsernameOrderBySyncIdDesc(String username);
    List<LichessSyncStatus> findAllByUsername(String username);
    void deleteAllByUsername(String username);
    boolean existsByUsername(String username);
    long countByUsername(String username);
}
