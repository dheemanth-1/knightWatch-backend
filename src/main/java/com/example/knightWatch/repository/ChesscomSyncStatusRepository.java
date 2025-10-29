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


    boolean existsByUsernameAndYearAndMonthAndUserId(String username, Integer year, Integer month, long userId);
    boolean existsByUsernameAndUserId(String username, long userId);
    void deleteAllByUsernameAndUserId(String username, long userId);
    List<ChesscomSyncStatus> findAllByUsernameAndUserId(String username, long userId);
    Long countByUsernameAndUserId(String username, long userId);
    Optional<ChesscomSyncStatus> findFirstByUsernameAndUserIdOrderBySyncIdDesc(String username, long userId);
    ChesscomSyncStatus findByUsernameAndYearAndMonthAndUserId(String username, Integer year, Integer month, long userId);

}
