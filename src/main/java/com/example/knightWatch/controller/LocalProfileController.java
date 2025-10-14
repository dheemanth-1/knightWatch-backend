package com.example.knightWatch.controller;

import com.example.knightWatch.model.LocalProfile;
import com.example.knightWatch.repository.ChesscomSyncStatusRepository;
import com.example.knightWatch.repository.LocalGameRepository;
import com.example.knightWatch.repository.LocalProfileRepository;
import com.example.knightWatch.repository.SyncStatusRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/db/local/profile")
@Tag(name = "Local Profile", description = "APIs for cached local profile data")
public class LocalProfileController {

    private final LocalProfileRepository profileRepo;
    private final LocalGameRepository gameRepo;
    private final SyncStatusRepository syncRepo;
    private final ChesscomSyncStatusRepository chesscomSyncRepo;

    public LocalProfileController(LocalProfileRepository profileRepo, LocalGameRepository gameRepo, SyncStatusRepository syncRepo, ChesscomSyncStatusRepository chesscomSyncRepo) {
        this.profileRepo = profileRepo;
        this.gameRepo = gameRepo;
        this.syncRepo = syncRepo;
        this.chesscomSyncRepo = chesscomSyncRepo;
    }

    @Operation(summary = "Get cached Lichess profile by username")
    @GetMapping("/{username}")
    public ResponseEntity<?> getCachedProfile(@PathVariable String username) {
        return profileRepo.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get cached profile by username and source")
    @GetMapping("/{source}/{username}")
    public ResponseEntity<?> getCachedProfileFromSource(@PathVariable String username, @PathVariable String source) {
        return profileRepo.findByUsernameAndSource(username, source)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all cached Lichess profiles")
    @GetMapping("/all")
    public ResponseEntity<List<LocalProfile>> getAllCachedProfiles() {
        List<LocalProfile> profiles = profileRepo.findAll();

        if (profiles.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(profiles);
        }
    }

    @Operation(summary = "Delete all data for a Lichess user")
    @Transactional
    @DeleteMapping("/{username}")
    public ResponseEntity<Map<String, Object>> deleteUserData(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean userHasData = profileRepo.existsByUsername(username) ||
                    gameRepo.existsByUsername(username);

            if (!userHasData) {
                response.put("success", false);
                response.put("message", "No data found for username: " + username);
                response.put("deletedItems", Map.of(
                        "profiles", 0,
                        "games", 0,
                        "syncStatus", 0
                ));
                return ResponseEntity.notFound().build();
            }
            String source = profileRepo.findByUsername(username).get().getSource();

            long profileCount = profileRepo.countByUsernameAndSource(username, source);
            long gameCount = gameRepo.countByUsername(username);

            long syncCount;
            if(source.equals("chesscom")) {
                syncCount = syncRepo.countByUsername(username);
            } else {
                syncCount = chesscomSyncRepo.countByUsername(username);
            }
            profileRepo.deleteByUsernameAndSource(username, source);
            gameRepo.deleteAllByUsername(username);

            if(source.equals("chesscom")) {
                chesscomSyncRepo.deleteAllByUsername(username);
            }
            else {
                syncRepo.deleteAllByUsername(username);
            }

            response.put("success", true);
            response.put("message", "Successfully deleted all data for user: " + username);
            response.put("deletedItems", Map.of(
                    "profiles", profileCount,
                    "games", gameCount,
                    "syncStatus", syncCount
            ));
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
                System.out.println("Deletion Error: " + e.getMessage());

                response.put("success", false);
                response.put("message", "Failed to delete data for user: " + username);
                response.put("error", e.getMessage());
                response.put("timestamp", LocalDateTime.now().toString());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }

    @Operation(summary = "Delete all data for a user from username and source")
    @Transactional
    @DeleteMapping("{source}/{username}")
    public ResponseEntity<Map<String, Object>> deleteUserDataFromUsernameAndSource(@PathVariable String username, @PathVariable String source) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean statusExists;
            if(source.equals("chesscom")) {
                statusExists = chesscomSyncRepo.existsByUsername(username);
            } else {
                statusExists = syncRepo.existsByUsername(username);
            }
            boolean userHasData = profileRepo.existsByUsernameAndSource(username, source) ||
                    gameRepo.existsByUsername(username) ||
                    statusExists;

            if (!userHasData) {
                response.put("success", false);
                response.put("message", "No data found for username: " + username);
                response.put("deletedItems", Map.of(
                        "profiles", 0,
                        "games", 0,
                        "syncStatus", 0
                ));
                return ResponseEntity.notFound().build();
            }

            long profileCount = profileRepo.countByUsernameAndSource(username, source);
            long gameCount = gameRepo.countByUsername(username);

            long syncCount;
            if(source.equals("chesscom")) {
                syncCount = syncRepo.countByUsername(username);
            } else {
                syncCount = chesscomSyncRepo.countByUsername(username);
            }
            profileRepo.deleteByUsernameAndSource(username, source);
            gameRepo.deleteAllByUsername(username);

            if(source.equals("chesscom")) {
                chesscomSyncRepo.deleteAllByUsername(username);
            }
            else {
                syncRepo.deleteAllByUsername(username);
            }
            response.put("success", true);
            response.put("message", "Successfully deleted all data for user: " + username);
            response.put("deletedItems", Map.of(
                    "profiles", profileCount,
                    "games", gameCount,
                    "syncStatus", syncCount
            ));
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Deletion Error: " + e.getMessage());

            response.put("success", false);
            response.put("message", "Failed to delete data for user: " + username);
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}