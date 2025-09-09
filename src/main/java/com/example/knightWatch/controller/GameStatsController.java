package com.example.knightWatch.controller;

import com.example.knightWatch.dto.GameStatsByOpening;
import com.example.knightWatch.dto.OverallStats;
import com.example.knightWatch.service.GameStatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        if(ex.getMessage().equals("Service error")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}

