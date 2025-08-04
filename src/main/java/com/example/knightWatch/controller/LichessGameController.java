package com.example.knightWatch.controller;

import com.example.knightWatch.model.LichessGame;
import com.example.knightWatch.repository.LichessGameRepository;
import com.example.knightWatch.service.LichessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/db/lichess/games")
public class LichessGameController {

    private final LichessGameRepository gameRepo;
    private final LichessService lichessService;

    public LichessGameController(LichessGameRepository gameRepo,LichessService lichessService) {
        this.lichessService = lichessService;
        this.gameRepo = gameRepo;
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getCachedGames(@PathVariable String username) {
        List<LichessGame> games = lichessService.getRecentGames(username);
        if (games.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(games);
    }
}