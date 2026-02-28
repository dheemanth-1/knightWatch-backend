package com.example.knightWatch.service;

import com.example.knightWatch.dto.nodetrav.*;
import com.example.knightWatch.model.Opening;
import com.example.knightWatch.repository.LocalGameRepository;
import com.example.knightWatch.repository.OpeningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.postgresql.util.PGobject;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpeningTreeService {

    @Autowired
    private LocalGameRepository gameRepository;

    @Autowired
    private OpeningRepository openingRepository;

    /*
     Get root level openings for the user
     */
    public List<OpeningNodeDTO> getRootOpenings(Long userId, Long localProfileId) {
        List<Object[]> results = gameRepository.findRootOpeningsByUserAndProfile(userId, localProfileId);
        return results.stream()
                .map(this::mapToOpeningNode)
                .toList();
    }

    /*
     Navigate to children of a specific opening
    */
    public List<OpeningNodeDTO> getChildOpenings(Long userId, Long openingId, Long localProfileId) {
        Opening opening = openingRepository.findById(openingId)
                .orElseThrow(() -> new RuntimeException("Opening not found"));
        System.out.println(opening.getPgnPath());
        List<Object[]> results = gameRepository.findChildOpenings(userId, opening.getPgnPath(), localProfileId);
        return results.stream()
                .map(this::mapToOpeningNode)
                .toList();
    }

    /*
     Navigate back up the tree
     */
    public OpeningNodeDTO getParentOpening(Long userId, Long openingId, Long localProfileId) {
        Opening opening = openingRepository.findById(openingId)
                .orElseThrow(() -> new RuntimeException("Opening not found"));

        Object[] result = gameRepository.findParentOpening(userId, opening.getPgnPath(), localProfileId);
        if (result == null) return null;

        return mapToOpeningNode(result);
    }

    /*
     Get full node with games and stats
    */
    public OpeningGamesDTO getOpeningWithGames(Long userId, Long openingId, Long localProfileId) {
        Opening opening = openingRepository.findById(openingId)
                .orElseThrow(() -> new RuntimeException("Opening not found"));

        OpeningNodeDTO node = new OpeningNodeDTO();
        node.setOpeningId(opening.getOpeningId());
        node.setName(opening.getName());
        node.setEco(opening.getEco());
        node.setPgnPath(opening.getPgnPath());
        node.setDepth(countDepth(opening.getPgnPath()));

        /*
         Get stats with color breakdown
        */
        List<Object[]> statsResults = gameRepository.getOpeningStatsWithColor(userId, openingId, localProfileId);
        if (!statsResults.isEmpty()) {
            node.setStats(mapToStatsWithColor(statsResults.get(0)));
        }

        /*
         Get games grouped by color
        */
        List<Object[]> gamesResults = gameRepository.findGamesByOpening(userId, openingId, localProfileId);
        GamesByColorDTO gamesByColor = groupGamesByColor(gamesResults);

        OpeningGamesDTO response = new OpeningGamesDTO();
        response.setOpening(node);
        response.setGames(gamesByColor);

        return response;
    }

    private OpeningNodeDTO mapToOpeningNode(Object[] row) {
        OpeningNodeDTO dto = new OpeningNodeDTO();
        dto.setOpeningId(((Number) row[0]).longValue());
        dto.setName((String) row[1]);
        dto.setEco((String) row[2]);


        Object pgnPathObj = row[3];
        if (pgnPathObj instanceof PGobject) {
            dto.setPgnPath(((PGobject) pgnPathObj).getValue());
        } else {
            dto.setPgnPath((String) pgnPathObj);
        }

        dto.setDepth(((Number) row[4]).intValue());
        dto.setGameCount(((Number) row[5]).intValue());
        return dto;
    }

    private OpeningStatsDTO mapToStats(Object[] row) {
        if (row == null) return null;

        OpeningStatsDTO stats = new OpeningStatsDTO();
        stats.setTotal(((Number) row[0]).intValue());
        stats.setWins(((Number) row[1]).intValue());
        stats.setDraws(((Number) row[2]).intValue());
        stats.setLosses(((Number) row[3]).intValue());

        int total = stats.getTotal();
        stats.setWinRate(total > 0 ? (stats.getWins() * 100.0 / total) : 0.0);

        return stats;
    }

    private OpeningStatsDTO mapToStatsWithColor(Object[] row) {
        OpeningStatsDTO stats = new OpeningStatsDTO();


        stats.setTotal(((Number) row[0]).intValue());
        stats.setWins(((Number) row[1]).intValue());
        stats.setDraws(((Number) row[2]).intValue());
        stats.setLosses(((Number) row[3]).intValue());
        stats.setWinRate(stats.getTotal() > 0 ? (stats.getWins() * 100.0 / stats.getTotal()) : 0.0);


        ColorStatsDTO whiteStats = new ColorStatsDTO();
        whiteStats.setTotal(((Number) row[4]).intValue());
        whiteStats.setWins(((Number) row[5]).intValue());
        whiteStats.setDraws(((Number) row[6]).intValue());
        whiteStats.setLosses(((Number) row[7]).intValue());
        whiteStats.setWinRate(whiteStats.getTotal() > 0 ? (whiteStats.getWins() * 100.0 / whiteStats.getTotal()) : 0.0);
        stats.setWhiteStats(whiteStats);


        ColorStatsDTO blackStats = new ColorStatsDTO();
        blackStats.setTotal(((Number) row[8]).intValue());
        blackStats.setWins(((Number) row[9]).intValue());
        blackStats.setDraws(((Number) row[10]).intValue());
        blackStats.setLosses(((Number) row[11]).intValue());
        blackStats.setWinRate(blackStats.getTotal() > 0 ? (blackStats.getWins() * 100.0 / blackStats.getTotal()) : 0.0);
        stats.setBlackStats(blackStats);

        return stats;
    }

    private GamesByColorDTO groupGamesByColor(List<Object[]> gamesResults) {
        List<GameSummaryDTO> whiteGames = new ArrayList<>();
        List<GameSummaryDTO> blackGames = new ArrayList<>();

        for (Object[] row : gamesResults) {
            GameSummaryDTO game = mapToGameSummary(row);
            String color = (String) row[6]; // player_color is at index 6

            if ("white".equalsIgnoreCase(color)) {
                whiteGames.add(game);
            } else {
                blackGames.add(game);
            }
        }

        GamesByColorDTO gamesByColor = new GamesByColorDTO();
        gamesByColor.setWhite(whiteGames);
        gamesByColor.setBlack(blackGames);

        return gamesByColor;
    }

    private GameSummaryDTO mapToGameSummary(Object[] row) {
        GameSummaryDTO dto = new GameSummaryDTO();
        dto.setGameId(((Number) row[0]).longValue());
        dto.setGameIdExternal((String) row[1]);
        dto.setPlayer((String) row[2]);
        dto.setResult((String) row[3]);
        dto.setPlayedAt(((java.sql.Timestamp) row[4]).toLocalDateTime());
        dto.setSource((String) row[5]);

        return dto;
    }

    private int countDepth(String pgnPath) {
        return pgnPath.split("\\.").length;
    }
}