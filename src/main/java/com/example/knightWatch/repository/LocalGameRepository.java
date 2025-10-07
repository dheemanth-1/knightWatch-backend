package com.example.knightWatch.repository;

import com.example.knightWatch.model.LocalGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocalGameRepository extends JpaRepository<LocalGame, Long> {
    List<LocalGame> findByUsername(String username);
    List<LocalGame> findByUsernameAndSource(String username, String source);

    void deleteAllByUsername(String username);

    List<LocalGame> findTop10ByUsernameOrderByPlayedAtDesc(String username);

    @Query("SELECT MAX(g.playedAt) FROM LocalGame g WHERE g.username = :username")
    String findLatestGameDateByUsername(@Param("username") String username);

    @Query(value = "SELECT * FROM local_games WHERE username = :username " +
            "ORDER BY strftime('%s', " +
            "  substr(played_at, 1, 4) || '-' || " +     // Year: "2022"
            "  substr(played_at, 6, 2) || '-' || " +     // Month: "09"
            "  substr(played_at, 9, 2) || 'T' || " +     // Day: "09"
            "  substr(played_at, 12, 8)" +               // Time: "14:20:21"
            ") DESC LIMIT 1",
            nativeQuery = true)
    LocalGame findLatestGameByUsername(@Param("username") String username);


    @Query(value = "SELECT * FROM local_games WHERE username = :username " +
            "ORDER BY strftime('%s', " +
            "  substr(played_at, 1, 4) || '-' || " +     // Year: "2022"
            "  substr(played_at, 6, 2) || '-' || " +     // Month: "09"
            "  substr(played_at, 9, 2) || 'T' || " +     // Day: "09"
            "  substr(played_at, 12, 8)" +               // Time: "14:20:21"
            ") ASC LIMIT 1",
            nativeQuery = true)
    LocalGame findOldestGameByUsername(@Param("username") String username);

    boolean existsByUsername(String username);

    long countByUsername(String username);

    long countByUsernameAndSource(String username, String source);
}