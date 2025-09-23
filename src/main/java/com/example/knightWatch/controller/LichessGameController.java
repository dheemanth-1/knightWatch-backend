package com.example.knightWatch.controller;

import com.example.knightWatch.model.LichessGame;
import com.example.knightWatch.repository.LichessGameRepository;
import com.example.knightWatch.service.LichessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/db/lichess/games")
public class LichessGameController {

    private final LichessGameRepository gameRepo;
    private final LichessService lichessService;

    public LichessGameController(LichessGameRepository gameRepo, LichessService lichessService) {
        this.lichessService = lichessService;
        this.gameRepo = gameRepo;
    }

    @GetMapping("/recent/{username}")
    public ResponseEntity<?> getRecentCachedGames(@PathVariable String username) {
        List<LichessGame> games = lichessService.getRecentGames(username);
        if (games.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getCachedGames(@PathVariable String username) {
        List<LichessGame> games = lichessService.getAllGames(username);
        if (games.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(games);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        if(ex.getMessage().equals("Service error")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}