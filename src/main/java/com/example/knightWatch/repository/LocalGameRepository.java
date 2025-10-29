package com.example.knightWatch.repository;

import com.example.knightWatch.model.LocalGame;
import com.example.knightWatch.projection.LocalGameProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocalGameRepository extends JpaRepository<LocalGame, Long> {
    List<LocalGame> findByUsername(String username);
    List<LocalGame> findByUsernameAndSource(String username, String source);
    List<LocalGame> findByUsernameAndSourceAndLocalProfile_User_Id(String username, String source, long userId);
    List<LocalGameProjection> findProjectedByUsername(String username);
    List<LocalGameProjection> findProjectedByUsernameAndSource(String username, String source);
    List<LocalGameProjection> findProjectedByUsernameAndLocalProfile_User_Id(String username, long userId);
    List<LocalGameProjection> findProjectedByUsernameAndSourceAndLocalProfile_User_Id(String username, String source, long userId);
    void deleteAllByUsername(String username);
    void deleteAllByUsernameAndLocalProfile_User_Id(String username, long userId);
    void deleteAllByUsernameAndSourceAndLocalProfile_User_Id(String username, String source, long userId);
    List<LocalGame> findTop10ByUsernameOrderByPlayedAtDesc(String username);
    List<LocalGameProjection> findTop10ProjectedByUsernameAndLocalProfile_User_IdOrderByPlayedAtDesc(String username, long userId);

    @Query("SELECT MAX(g.playedAt) FROM LocalGame g WHERE g.username = :username")
    String findLatestGameDateByUsername(@Param("username") String username);

    @Query("""
    SELECT MAX(g.playedAt)
    FROM LocalGame g
    WHERE g.username = :username
      AND g.localProfile.user.id = :userId
""")
    String findLatestGameDateByUsernameAndUserId(@Param("username") String username,
                                                 @Param("userId") long userId);

    @Query(value = "SELECT * FROM local_games WHERE username = :username " +
            "ORDER BY strftime('%s', " +
            "  substr(played_at, 1, 4) || '-' || " +     // Year: "2022"
            "  substr(played_at, 6, 2) || '-' || " +     // Month: "09"
            "  substr(played_at, 9, 2) || 'T' || " +     // Day: "09"
            "  substr(played_at, 12, 8)" +               // Time: "14:20:21"
            ") DESC LIMIT 1",
            nativeQuery = true)
    LocalGame findLatestGameByUsername(@Param("username") String username);

    @Query(value = """
    SELECT g.*
    FROM local_games g
    JOIN local_profile p ON g.local_profile_id = p.id
    WHERE g.username = :username
      AND p.user_id = :userId
    ORDER BY strftime('%s',
        substr(g.played_at, 1, 4) || '-' ||
        substr(g.played_at, 6, 2) || '-' ||
        substr(g.played_at, 9, 2) || 'T' ||
        substr(g.played_at, 12, 8)
    ) DESC
    LIMIT 1
    """, nativeQuery = true)
    LocalGame findLatestGameByUsernameAndUserId(@Param("username") String username,
                                                @Param("userId") Long userId);


    @Query(value = "SELECT * FROM local_games WHERE username = :username " +
            "ORDER BY strftime('%s', " +
            "  substr(played_at, 1, 4) || '-' || " +     // Year: "2022"
            "  substr(played_at, 6, 2) || '-' || " +     // Month: "09"
            "  substr(played_at, 9, 2) || 'T' || " +     // Day: "09"
            "  substr(played_at, 12, 8)" +               // Time: "14:20:21"
            ") ASC LIMIT 1",
            nativeQuery = true)
    LocalGame findOldestGameByUsername(@Param("username") String username);

    @Query(value = """
    SELECT g.*
    FROM local_games g
    JOIN local_profile p ON g.local_profile_id = p.id
    WHERE g.username = :username
      AND p.user_id = :userId
    ORDER BY strftime('%s',
        substr(g.played_at, 1, 4) || '-' ||
        substr(g.played_at, 6, 2) || '-' ||
        substr(g.played_at, 9, 2) || 'T' ||
        substr(g.played_at, 12, 8)
    ) ASC
    LIMIT 1
    """, nativeQuery = true)
    LocalGame findOldestGameByUsernameAndUserId(@Param("username") String username,
                                                @Param("userId") Long userId);

    boolean existsByUsername(String username);
    boolean existsByUsernameAndLocalProfile_User_Id(String username, long userId);

    long countByUsername(String username);
    long countByUsernameAndLocalProfile_User_Id(String username, long userId);

    long countByUsernameAndSource(String username, String source);
    long countByUsernameAndSourceAndLocalProfile_User_Id(String username, String source, long userId);
}