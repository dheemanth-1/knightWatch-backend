package com.example.knightWatch.controller;

import com.example.knightWatch.model.ChesscomSyncStatus;
import com.example.knightWatch.repository.ChesscomSyncStatusRepository;
import com.example.knightWatch.service.ChesscomSyncService;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/chesscom/sync")
public class ChesscomSyncController {
    private final ChesscomSyncStatusRepository chesscomSyncStatusRepo;
    private final ChesscomSyncService chesscomSyncService;

    public ChesscomSyncController(ChesscomSyncStatusRepository chesscomSyncStatusRepo, ChesscomSyncService chesscomSyncService) {
        this.chesscomSyncStatusRepo = chesscomSyncStatusRepo;
        this.chesscomSyncService = chesscomSyncService;
    }

    @GetMapping("/isSynced/{username}/{year}/{month}")
    public ResponseEntity<ChesscomSyncStatus> isSynced(@PathVariable String username, @PathVariable Integer year, @PathVariable Integer month) {
        var status = this.chesscomSyncStatusRepo.findByUsernameAndYearAndMonth(username, year, month);
        if(status == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(status);
    }

    @GetMapping("/syncUserAndGames/{username}/{year}/{month}")
    public ResponseEntity<ChesscomSyncStatus> syncUserAndGames(@PathVariable String username, @PathVariable Integer year, @PathVariable Integer month) throws ChessComPubApiException, IOException {
        try {
            var status = this.chesscomSyncService.syncUser(username, year, month);
            return ResponseEntity.ok(status);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/syncHistory/{username}")
    public ResponseEntity<List<ChesscomSyncStatus>> getSyncHistory(String username) {
        List<ChesscomSyncStatus> list = chesscomSyncStatusRepo.findAllByUsername(username);

        if(list.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }
}
