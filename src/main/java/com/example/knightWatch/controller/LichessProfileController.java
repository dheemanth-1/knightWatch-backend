package com.example.knightWatch.controller;

import com.example.knightWatch.model.LichessProfile;
import com.example.knightWatch.repository.LichessGameRepository;
import com.example.knightWatch.repository.LichessProfileRepository;
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
@RequestMapping("/api/db/lichess/profile")
@Tag(name = "Lichess Profile", description = "APIs for cached Lichess profile data")
public class LichessProfileController {

    private final LichessProfileRepository profileRepo;
    private final LichessGameRepository gameRepo;
    private final SyncStatusRepository syncRepo;

    public LichessProfileController(LichessProfileRepository profileRepo, LichessGameRepository gameRepo, SyncStatusRepository syncRepo) {
        this.profileRepo = profileRepo;
        this.gameRepo = gameRepo;
        this.syncRepo = syncRepo;
    }

    @Operation(summary = "Get cached Lichess profile by username")
    @GetMapping("/{username}")
    public ResponseEntity<?> getCachedProfile(@PathVariable String username) {
        return profileRepo.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all cached Lichess profiles")
    @GetMapping("/all")
    public ResponseEntity<List<LichessProfile>> getAllCachedProfiles() {
        List<LichessProfile> profiles = profileRepo.findAll();

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
                    gameRepo.existsByUsername(username) ||
                    syncRepo.existsByUsername(username);

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

            long profileCount = profileRepo.countByUsername(username);
            long gameCount = gameRepo.countByUsername(username);
            long syncCount = syncRepo.countByUsername(username);

            profileRepo.deleteByUsername(username);
            gameRepo.deleteAllByUsername(username);
            syncRepo.deleteAllByUsername(username);

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