package com.example.knightWatch.controller;

import com.example.knightWatch.dto.GameStatsByOpening;
import com.example.knightWatch.dto.OverallStats;
import com.example.knightWatch.service.GameStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class GameStatsController {
    private final GameStatsService gameStatsService;

    public GameStatsController(GameStatsService gameStatsService) {
        this.gameStatsService = gameStatsService;
    }

    @GetMapping("/openings/{userId}")
    public List<GameStatsByOpening> getStatsByOpening(@PathVariable String userId) {
        return gameStatsService.calculateStatsByOpening(userId);
    }

    @GetMapping("/overall/{userId}")
    public OverallStats getOverallStats(@PathVariable String userId) {
        return gameStatsService.calculateOverallStats(userId);
    }
}

