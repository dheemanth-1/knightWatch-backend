package com.example.knightWatch.controller;

import com.example.knightWatch.model.ChesscomSyncStatus;
import com.example.knightWatch.model.User;
import com.example.knightWatch.repository.ChesscomSyncStatusRepository;
import com.example.knightWatch.repository.UserRepository;
import com.example.knightWatch.service.ChesscomSyncService;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final UserRepository userRepository;

    public ChesscomSyncController(ChesscomSyncStatusRepository chesscomSyncStatusRepo, ChesscomSyncService chesscomSyncService, UserRepository userRepository) {
        this.chesscomSyncStatusRepo = chesscomSyncStatusRepo;
        this.chesscomSyncService = chesscomSyncService;
        this.userRepository = userRepository;
    }

    @GetMapping("/isSynced/{username}/{year}/{month}")
    public ResponseEntity<ChesscomSyncStatus> isSynced(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @PathVariable String username, @PathVariable Integer year, @PathVariable Integer month) {
        String loggedInUsername = userDetails.getUsername();
        User loggedInUser = userRepository.findByUsername(loggedInUsername).orElseThrow(() -> new RuntimeException("User not found"));
        var status = this.chesscomSyncStatusRepo.findByUsernameAndYearAndMonthAndUserId(username, year, month, loggedInUser.getId());
        if(status == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(status);
    }

    @GetMapping("/syncUserAndGames/{username}/{year}/{month}")
    public ResponseEntity<ChesscomSyncStatus> syncUserAndGames(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @PathVariable String username, @PathVariable Integer year, @PathVariable Integer month) throws ChessComPubApiException, IOException {
        try {
            String loggedInUsername = userDetails.getUsername();
            User loggedInUser = userRepository.findByUsername(loggedInUsername).orElseThrow(() -> new RuntimeException("User not found"));
            var status = this.chesscomSyncService.syncUser(username, year, month, loggedInUser);
            return ResponseEntity.ok(status);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/syncHistory/{username}")
    public ResponseEntity<List<ChesscomSyncStatus>> getSyncHistory(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, String username) {
        String loggedInUsername = userDetails.getUsername();
        User loggedInUser = userRepository.findByUsername(loggedInUsername).orElseThrow(() -> new RuntimeException("User not found"));
        List<ChesscomSyncStatus> list = chesscomSyncStatusRepo.findAllByUsernameAndUserId(username, loggedInUser.getId());

        if(list.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }
}
