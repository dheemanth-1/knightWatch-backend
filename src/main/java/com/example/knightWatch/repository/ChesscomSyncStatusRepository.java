package com.example.knightWatch.repository;

import com.example.knightWatch.model.ChesscomSyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChesscomSyncStatusRepository extends JpaRepository<ChesscomSyncStatus, Long> {
    boolean existsByUsernameAndYearAndMonth(String username, Integer year, Integer month);

    boolean existsByUsername(String username);

    void deleteAllByUsername(String username);

    List<ChesscomSyncStatus> findAllByUsername(String username);

    Long countByUsername(String username);

    Optional<ChesscomSyncStatus> findFirstByUsernameOrderBySyncIdDesc(String username);

    ChesscomSyncStatus findByUsernameAndYearAndMonth(String username, Integer year, Integer month);

}
