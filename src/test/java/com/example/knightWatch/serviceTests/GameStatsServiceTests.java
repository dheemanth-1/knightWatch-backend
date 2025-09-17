package com.example.knightWatch.serviceTests;


import com.example.knightWatch.dto.GameStatsByOpening;
import com.example.knightWatch.dto.OverallStats;
import com.example.knightWatch.model.LichessGame;
import com.example.knightWatch.repository.LichessGameRepository;
import com.example.knightWatch.service.GameStatsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameStatsServiceTests {


    private GameStatsService gameStatsService;
    private LichessGameRepository gameRepository;

    @BeforeEach
    void setUp() {
        gameRepository = mock(LichessGameRepository.class);
        gameStatsService = new GameStatsService(gameRepository);
    }

    @Test
    void testCalculateStatsByOpening_basicStats() {
        String userId = "testuser";
        LichessGame game1 = new LichessGame();
        game1.setOpeningName("Sicilian Defense: Main Line");
        game1.setResult("won");

        LichessGame game2 = new LichessGame();
        game2.setOpeningName("Sicilian Defense: Main Line");
        game2.setResult("lost");

        LichessGame game3 = new LichessGame();
        game3.setOpeningName("French Defense");
        game3.setResult("draw");

        when(gameRepository.findByUsername(userId)).thenReturn(Arrays.asList(game1, game2, game3));

        List<GameStatsByOpening> stats = gameStatsService.calculateStatsByOpening(userId);


        Assertions.assertEquals(2, stats.size());

        GameStatsByOpening sicilianStats = stats.stream().filter(s -> s.getOpeningName().equals("Sicilian Defense")).findFirst().orElse(null);
        Assertions.assertNotNull(sicilianStats);
        Assertions.assertEquals(2, sicilianStats.getNumWins() + sicilianStats.getNumLosses());
        Assertions.assertEquals(1, sicilianStats.getNumWins());
        Assertions.assertEquals(1, sicilianStats.getNumLosses());

        GameStatsByOpening frenchStats = stats.stream().filter(s -> s.getOpeningName().equals("French Defense")).findFirst().orElse(null);
        Assertions.assertNotNull(frenchStats);
        Assertions.assertEquals(1, frenchStats.getNumDraws());
    }

    @Test
    void testCalculateOverallStats_mixedResults() {
        String userId = "testuser2";
        LichessGame g1 = new LichessGame(); g1.setResult("won");
        LichessGame g2 = new LichessGame(); g2.setResult("lost");
        LichessGame g3 = new LichessGame(); g3.setResult("draw");

        when(gameRepository.findByUsername(userId)).thenReturn(Arrays.asList(g1, g2, g3));

        OverallStats overall = gameStatsService.calculateOverallStats(userId);

        Assertions.assertEquals(1.0/3, overall.getWinRate(), 1e-6);
        Assertions.assertEquals(1.0/3, overall.getLossRate(), 1e-6);
        Assertions.assertEquals(1.0/3, overall.getDrawRate(), 1e-6);
        Assertions.assertEquals(3, overall.getNumberOfGames(), 1e-6);
    }

    @Test
    void testCalculateOverallStats_noGames_returnsZero() {
        String userId = "emptyuser";
        when(gameRepository.findByUsername(userId)).thenReturn(Collections.emptyList());
        OverallStats overall = gameStatsService.calculateOverallStats(userId);
        Assertions.assertEquals(0.0, overall.getWinRate(), 1e-6);
        Assertions.assertEquals(0.0, overall.getLossRate(), 1e-6);
        Assertions.assertEquals(0.0, overall.getDrawRate(), 1e-6);
        Assertions.assertEquals(0, overall.getNumberOfGames(), 1e-6);
    }

}
