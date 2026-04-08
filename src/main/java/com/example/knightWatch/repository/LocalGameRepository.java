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

    @Query(value = """
             SELECT * FROM local_game
             WHERE username = :username
             ORDER BY played_at DESC
             LIMIT 1
             """,
            nativeQuery = true)
    LocalGame findLatestGameByUsername(@Param("username") String username);

    @Query(value = """
            SELECT g.*
                FROM local_game g
                JOIN local_profile p ON g.local_profile_id = p.id
                WHERE g.username = :username
                  AND p.user_id = :userId
                ORDER BY g.played_at DESC
                LIMIT 1
    """, nativeQuery = true)
    LocalGame findLatestGameByUsernameAndUserId(@Param("username") String username,
                                                @Param("userId") Long userId);


    @Query(value = """
             SELECT * FROM local_game
                 WHERE username = :username
                 ORDER BY played_at ASC
                 LIMIT 1
            """,
            nativeQuery = true)
    LocalGame findOldestGameByUsername(@Param("username") String username);

    @Query(value = """
            SELECT g.*
                FROM local_game g
                JOIN local_profile p ON g.local_profile_id = p.id
                WHERE g.username = :username
                  AND p.user_id = :userId
                ORDER BY g.played_at ASC
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

    @Query(value = """
            WITH user_games AS (
                    SELECT DISTINCT g.pgn_path
                    FROM local_game g
                    WHERE g.user_id = :userId
                      AND g.local_profile_id = :localProfileId
                ),
                root_openings AS (
                    SELECT DISTINCT o.opening_id, o.name, o.eco, o.pgn_path
                    FROM opening o
                    WHERE EXISTS (
                        -- This opening has games that are descendants
                        SELECT 1
                        FROM user_games ug
                        WHERE ug.pgn_path <@ o.pgn_path
                    )
                    AND NOT EXISTS (
                        -- No parent opening exists that also has descendant games
                        SELECT 1
                        FROM opening o_parent
                        WHERE o.pgn_path <@ o_parent.pgn_path  -- o is descendant of o_parent
                          AND o.pgn_path != o_parent.pgn_path  -- not the same
                          AND nlevel(o_parent.pgn_path) < nlevel(o.pgn_path)  -- o_parent is shallower
                          AND EXISTS (
                              -- o_parent also has descendant games
                              SELECT 1
                              FROM user_games ug2
                              WHERE ug2.pgn_path <@ o_parent.pgn_path
                          )
                    )
                )
                SELECT\s
                    ro.opening_id,
                    ro.name,
                    ro.eco,
                    ro.pgn_path::text,
                    nlevel(ro.pgn_path) as depth,
                    COUNT(g.id) as game_count
                FROM root_openings ro
                JOIN local_game g ON g.pgn_path <@ ro.pgn_path
                WHERE g.user_id = :userId
                  AND g.local_profile_id = :localProfileId
                GROUP BY ro.opening_id, ro.name, ro.eco, ro.pgn_path
                ORDER BY nlevel(ro.pgn_path) ASC, game_count DESC
    """, nativeQuery = true)
    List<Object[]> findRootOpeningsByUserAndProfile(@Param("userId") Long userId,
                                                    @Param("localProfileId") Long localProfileId);

    // Get child openings from a specific node
    @Query(value = """
            SELECT\s
                o.opening_id,\s
                o.name,\s
                o.eco,\s
                o.pgn_path::text,\s
                nlevel(o.pgn_path) AS depth,
                COUNT(g.id) AS game_count
            FROM opening o
            JOIN local_game g ON g.pgn_path <@ o.pgn_path\s
                AND g.user_id = :userId\s
                AND g.local_profile_id = :localProfileId
            WHERE o.pgn_path <@ CAST(:parentPath AS ltree)
              AND nlevel(o.pgn_path) = (
                  SELECT MIN(nlevel(o2.pgn_path))
                  FROM opening o2
                  JOIN local_game g2 ON g2.pgn_path <@ o2.pgn_path\s
                      AND g2.user_id = :userId\s
                      AND g2.local_profile_id = :localProfileId
                  WHERE o2.pgn_path <@ CAST(:parentPath AS ltree)
                    AND nlevel(o2.pgn_path) > nlevel(CAST(:parentPath AS ltree))
              )
            GROUP BY o.opening_id, o.name, o.eco, o.pgn_path
            ORDER BY game_count DESC
        """, nativeQuery = true)
    List<Object[]> findChildOpenings(@Param("userId") Long userId,
                                     @Param("parentPath") String parentPath,
                                     @Param("localProfileId") Long localProfileId);



    // Get parent opening (go back up the tree)
    @Query(value = """
           SELECT\s
               o.opening_id,\s
               o.name,\s
               o.eco,\s
               o.pgn_path::text,\s
               nlevel(o.pgn_path) AS depth,
               COUNT(g.id) AS game_count
           FROM opening o
           JOIN local_game g ON g.pgn_path <@ o.pgn_path\s
               AND g.user_id = :userId\s
               AND g.local_profile_id = :localProfileId
           WHERE o.pgn_path @> CAST(:childPath AS ltree)
             AND nlevel(o.pgn_path) = (
                 SELECT MAX(nlevel(o2.pgn_path))
                 FROM opening o2
                 JOIN local_game g2 ON g2.pgn_path <@ o2.pgn_path\s
                     AND g2.user_id = :userId\s
                     AND g2.local_profile_id = :localProfileId
                 WHERE o2.pgn_path @> CAST(:childPath AS ltree)
                   AND nlevel(o2.pgn_path) < nlevel(CAST(:childPath AS ltree))
             )
           GROUP BY o.opening_id, o.name, o.eco, o.pgn_path
           LIMIT 1
        """, nativeQuery = true)
    List<Object[]> findParentOpeningRaw(@Param("userId") Long userId, @Param("childPath") String childPath, @Param("localProfileId") Long localProfileId);

    // Get all games for a specific opening node
//    @Query(value = """
//        SELECT g.id, g.game_id, g.username, g.result, g.played_at, g.source
//        FROM local_game g
//        WHERE g.user_id = :userId
//          AND g.opening_id = :openingId
//        ORDER BY g.played_at DESC
//        """, nativeQuery = true)
//    List<Object[]> findGamesByOpening(@Param("userId") Long userId,
//                                      @Param("openingId") Long openingId);

    // Get stats for an opening node
    @Query(value = """
        SELECT 
            COUNT(*) as total,
            SUM(CASE WHEN result = 'win' THEN 1 ELSE 0 END) as wins,
            SUM(CASE WHEN result = 'draw' THEN 1 ELSE 0 END) as draws,
            SUM(CASE WHEN result = 'loss' THEN 1 ELSE 0 END) as losses
        FROM local_game
        WHERE user_id = :userId
          AND opening_id = :openingId
        """, nativeQuery = true)
    List<Object[]> getOpeningStatsRaw(@Param("userId") Long userId,
                             @Param("openingId") Long openingId);


    default Object[] getOpeningStats(Long userId, Long openingId) {
        List<Object[]> results = getOpeningStatsRaw(userId, openingId);
        return results.isEmpty() ? null : results.get(0);
    }

    default Object[] findParentOpening(Long userId, String childPath, Long localProfileId) {
        List<Object[]> results = findParentOpeningRaw(userId, childPath, localProfileId);
        return results.isEmpty() ? null : results.get(0);
    }

    @Query(value = """
    SELECT 
        COUNT(*) as total,
        SUM(CASE WHEN result = 'won' THEN 1 ELSE 0 END) as wins,
        SUM(CASE WHEN result = 'draw' THEN 1 ELSE 0 END) as draws,
        SUM(CASE WHEN result = 'lost' THEN 1 ELSE 0 END) as losses,
        -- White stats
        SUM(CASE WHEN player_color = 'white' THEN 1 ELSE 0 END) as white_total,
        SUM(CASE WHEN player_color = 'white' AND result = 'won' THEN 1 ELSE 0 END) as white_wins,
        SUM(CASE WHEN player_color = 'white' AND result = 'draw' THEN 1 ELSE 0 END) as white_draws,
        SUM(CASE WHEN player_color = 'white' AND result = 'lost' THEN 1 ELSE 0 END) as white_losses,
        -- Black stats
        SUM(CASE WHEN player_color = 'black' THEN 1 ELSE 0 END) as black_total,
        SUM(CASE WHEN player_color = 'black' AND result = 'won' THEN 1 ELSE 0 END) as black_wins,
        SUM(CASE WHEN player_color = 'black' AND result = 'draw' THEN 1 ELSE 0 END) as black_draws,
        SUM(CASE WHEN player_color = 'black' AND result = 'lost' THEN 1 ELSE 0 END) as black_losses
    FROM local_game g
    JOIN local_profile lp ON g.local_profile_id = lp.id
    WHERE lp.user_id = :userId
      AND g.opening_id = :openingId
      AND lp.id = :localProfileId
    """, nativeQuery = true)
    List<Object[]> getOpeningStatsWithColor(@Param("userId") Long userId,
                                            @Param("openingId") Long openingId,
                                            @Param("localProfileId") Long localProfileId);


    @Query(value = """
            SELECT g.id, g.game_id, g.username, g.result, g.played_at, g.source, g.player_color
                FROM local_game g
                WHERE g.user_id = :userId
                  AND g.opening_id = :openingId
                  AND g.local_profile_id = :localProfileId
                ORDER BY g.played_at DESC
    """, nativeQuery = true)
    List<Object[]> findGamesByOpening(@Param("userId") Long userId,
                                      @Param("openingId") Long openingId, @Param("localProfileId") Long localProfileId);
}
