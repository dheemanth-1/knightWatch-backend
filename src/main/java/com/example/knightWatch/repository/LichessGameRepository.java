package com.example.knightWatch.repository;

import com.example.knightWatch.model.LichessGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LichessGameRepository extends JpaRepository<LichessGame, Long> {
    List<LichessGame> findByUsername(String username);

    void deleteAllByUsername(String username);

    List<LichessGame> findTop10ByUsernameOrderByPlayedAtDesc(String username);

    @Query("SELECT MAX(g.playedAt) FROM LichessGame g WHERE g.username = :username")
    String findLatestGameDateByUsername(@Param("username") String username);

    @Query(value = "SELECT * FROM lichess_games WHERE username = :username " +
            "ORDER BY strftime('%s', " +
            "  substr(played_at, 1, 4) || '-' || " +     // Year: "2022"
            "  substr(played_at, 6, 2) || '-' || " +     // Month: "09"
            "  substr(played_at, 9, 2) || 'T' || " +     // Day: "09"
            "  substr(played_at, 12, 8)" +               // Time: "14:20:21"
            ") DESC LIMIT 1",
            nativeQuery = true)
    LichessGame findLatestGameByUsername(@Param("username") String username);


    @Query(value = "SELECT * FROM lichess_games WHERE username = :username " +
            "ORDER BY strftime('%s', " +
            "  substr(played_at, 1, 4) || '-' || " +     // Year: "2022"
            "  substr(played_at, 6, 2) || '-' || " +     // Month: "09"
            "  substr(played_at, 9, 2) || 'T' || " +     // Day: "09"
            "  substr(played_at, 12, 8)" +               // Time: "14:20:21"
            ") ASC LIMIT 1",
            nativeQuery = true)
    LichessGame findOldestGameByUsername(@Param("username") String username);

    boolean existsByUsername(String username);

    long countByUsername(String username);
}