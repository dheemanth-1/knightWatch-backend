package com.example.knightWatch.repository;

import com.example.knightWatch.model.SyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SyncStatusRepository extends JpaRepository<SyncStatus, Long> {
    SyncStatus findFirstByUsernameOrderBySyncIdDesc(String username);
    List<SyncStatus> findAllByUsername(String username);
    void deleteAllByUsername(String username);
    boolean existsByUsername(String username);
    long countByUsername(String username);
}
