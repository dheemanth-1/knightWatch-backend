package com.example.knightWatch.service;

import com.example.knightWatch.dto.GameStatsByOpening;
import com.example.knightWatch.dto.OverallStats;
import com.example.knightWatch.model.LichessGame;
import com.example.knightWatch.repository.LichessGameRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GameStatsService {
    private final LichessGameRepository gameRepository;

    public GameStatsService(LichessGameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameStatsByOpening> calculateStatsByOpening(String userId) {
        List<LichessGame> games = gameRepository.findByUsername(userId);

        // Group by opening name
        Map<String, List<LichessGame>> byOpening = games.stream()
                .filter(g -> g.getOpeningName() != null && g.getResult() != null)
                .collect(Collectors.groupingBy(g -> {
                    String opening = g.getOpeningName();
                    int colonIndex = opening.indexOf(':');
                    return colonIndex > -1 ? opening.substring(0, colonIndex).trim() : opening.trim();
                }));

        List<GameStatsByOpening> statsList = new ArrayList<>();
        for (Map.Entry<String, List<LichessGame>> entry : byOpening.entrySet()) {
            String baseOpeningName = entry.getKey();
            List<LichessGame> gamesForOpening = entry.getValue();

            long wins = gamesForOpening.stream().filter(g -> "won".equals(g.getResult())).count();
            long losses = gamesForOpening.stream().filter(g -> "lost".equals(g.getResult())).count();
            long draws = gamesForOpening.stream().filter(g -> "draw".equals(g.getResult())).count();
            long total = gamesForOpening.size();

            GameStatsByOpening stats = new GameStatsByOpening();
            stats.setOpeningName(baseOpeningName);
            stats.setNumWins(wins);
            stats.setNumLosses(losses);
            stats.setNumDraws(draws);
            stats.setWinRate(total > 0 ? (double) wins / total : 0.0);
            stats.setLossRate(total > 0 ? (double) losses / total : 0.0);
            stats.setDrawRate(total > 0 ? (double) draws / total : 0.0);

            statsList.add(stats);
        }
        return statsList;
    }

    public OverallStats calculateOverallStats(String userId) {
        List<LichessGame> games = gameRepository.findByUsername(userId)
                .stream()
                .filter(g -> g.getResult() != null)
                .toList();

        long total = games.size();
        long wins = games.stream().filter(g -> "won".equals(g.getResult().trim())).count();
        long losses = games.stream().filter(g -> "lost".equals(g.getResult().trim())).count();
        long draws = games.stream().filter(g -> "draw".equals(g.getResult().trim())).count();

        double winRate = total > 0 ? (double) wins / total : 0.0;
        double lossRate = total > 0 ? (double) losses / total : 0.0;
        double drawRate = total > 0 ? (double) draws / total : 0.0;

        return new OverallStats(winRate, lossRate, drawRate, total);
    }
}
