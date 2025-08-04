package com.example.knightWatch.controller;

import chariot.Client;
import chariot.model.User;
import com.example.knightWatch.model.LichessGame;
import com.example.knightWatch.service.LichessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lichess")
public class LichessController {

    private final LichessService lichessService;

    //private final Client lichessClient;

    public LichessController(LichessService lichessService) {
        this.lichessService = lichessService;
    }

    @GetMapping("/{username}/games")
    public ResponseEntity<List<?>> getRecentGames(@PathVariable String username) {
        List<?> games = lichessService.getRecentGames(username);

        if (games.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(games);
    }
    @GetMapping("/{username}/profile")
    public ResponseEntity<?> getProfile(@PathVariable String username) {
        return lichessService.getUserProfile(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}