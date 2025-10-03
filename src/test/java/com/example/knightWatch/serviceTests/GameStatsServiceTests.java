package com.example.knightWatch.serviceTests;


import com.example.knightWatch.dto.GameStatsByOpening;
import com.example.knightWatch.dto.OverallStats;
import com.example.knightWatch.model.LocalGame;
import com.example.knightWatch.repository.LocalGameRepository;
import com.example.knightWatch.service.GameStatsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameStatsServiceTests {


    private GameStatsService gameStatsService;
    private LocalGameRepository gameRepository;

    @BeforeEach
    void setUp() {
        gameRepository = mock(LocalGameRepository.class);
        gameStatsService = new GameStatsService(gameRepository);
    }

    @Test
    void testCalculateStatsByOpening_basicStats() {
        String userId = "testuser";
        LocalGame game1 = new LocalGame();
        game1.setOpeningName("Sicilian Defense: Main Line");
        game1.setResult("won");

        LocalGame game2 = new LocalGame();
        game2.setOpeningName("Sicilian Defense: Main Line");
        game2.setResult("lost");

        LocalGame game3 = new LocalGame();
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
        LocalGame g1 = new LocalGame(); g1.setResult("won");
        LocalGame g2 = new LocalGame(); g2.setResult("lost");
        LocalGame g3 = new LocalGame(); g3.setResult("draw");

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
