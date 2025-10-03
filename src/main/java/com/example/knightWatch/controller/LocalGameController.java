package com.example.knightWatch.controller;

import com.example.knightWatch.model.LocalGame;
import com.example.knightWatch.repository.LocalGameRepository;
import com.example.knightWatch.service.LichessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/db/local/games")
public class LocalGameController {


    private final LichessService lichessService;

    public LocalGameController(LichessService lichessService) {
        this.lichessService = lichessService;

    }

    @GetMapping("/recent/{username}")
    public ResponseEntity<?> getRecentCachedGames(@PathVariable String username) {
        List<LocalGame> games = lichessService.getRecentGames(username);
        if (games.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getCachedGames(@PathVariable String username) {
        List<LocalGame> games = lichessService.getAllGames(username);
        if (games.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(games);

    }

    @GetMapping("/{source}/{username}")
    public ResponseEntity<?> getCachedGamesFromUsernameAndSource(@PathVariable String username, @PathVariable String source) {
        List<LocalGame> games = lichessService.getAllGamesByUsernameAndSource(username, source);
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