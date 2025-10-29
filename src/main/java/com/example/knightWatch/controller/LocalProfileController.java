package com.example.knightWatch.controller;

import com.example.knightWatch.model.User;
import com.example.knightWatch.projection.LocalProfileProjection;
import com.example.knightWatch.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final UserRepository userRepository;

    public LocalProfileController(LocalProfileRepository profileRepo, LocalGameRepository gameRepo, SyncStatusRepository syncRepo, ChesscomSyncStatusRepository chesscomSyncRepo, UserRepository userRepository) {
        this.profileRepo = profileRepo;
        this.gameRepo = gameRepo;
        this.syncRepo = syncRepo;
        this.chesscomSyncRepo = chesscomSyncRepo;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Get cached Lichess profile by username")
    @GetMapping("/{username}")
    public ResponseEntity<?> getCachedProfile(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @PathVariable String username) {
        User loggedInUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        return profileRepo.findProjectedByUserIdAndUsername(loggedInUser.getId(), username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get cached profile by username and source")
    @GetMapping("/{source}/{username}")
    public ResponseEntity<?> getCachedProfileFromSource(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @PathVariable String username, @PathVariable String source) {
        User loggedInUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        return profileRepo.findProjectedByUserIdAndUsernameAndSource(loggedInUser.getId(), username, source)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all cached profiles")
    @GetMapping("/all")
    public ResponseEntity<List<LocalProfileProjection>> getAllCachedProfiles(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        User loggedInUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        List<LocalProfileProjection> profiles = profileRepo.findProjectedByUserId(loggedInUser.getId());

        if (profiles.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(profiles);
        }
    }

    @Operation(summary = "Delete all data for a user")
    @Transactional
    @DeleteMapping("/{username}")
    public ResponseEntity<Map<String, Object>> deleteUserData(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @PathVariable String username) {
        Map<String, Object> response = new HashMap<>();

        try {
            User loggedInUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
            boolean userHasData = profileRepo.existsByUsernameAndUserId(username, loggedInUser.getId()) ||
                    gameRepo.existsByUsernameAndLocalProfile_User_Id(username, loggedInUser.getId());

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
            String source = profileRepo.findProjectedByUserIdAndUsername(loggedInUser.getId(), username).get().getSource();

            long profileCount = profileRepo.countByUsernameAndSourceAndUserId(username, source, loggedInUser.getId());
            long gameCount = gameRepo.countByUsernameAndLocalProfile_User_Id(username, loggedInUser.getId());

            long syncCount;
            if(source.equals("chesscom")) {
                syncCount = chesscomSyncRepo.countByUsernameAndUserId(username, loggedInUser.getId());
            } else {
                syncCount = syncRepo.countByUsernameAndUserId(username, loggedInUser.getId());
            }
            profileRepo.deleteByUsernameAndSourceAndUserId(username, source, loggedInUser.getId());
            gameRepo.deleteAllByUsernameAndLocalProfile_User_Id(username, loggedInUser.getId());

            if(source.equals("chesscom")) {
                chesscomSyncRepo.deleteAllByUsernameAndUserId(username, loggedInUser.getId());
            }
            else {
                syncRepo.deleteAllByUsernameAndUserId(username, loggedInUser.getId());
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
    public ResponseEntity<Map<String, Object>> deleteUserDataFromUsernameAndSource(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @PathVariable String username, @PathVariable String source) {
        Map<String, Object> response = new HashMap<>();

        try {
            User loggedInUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
            boolean statusExists;
            if(source.equals("chesscom")) {
                statusExists = chesscomSyncRepo.existsByUsernameAndUserId(username, loggedInUser.getId());
            } else {
                statusExists = syncRepo.existsByUsernameAndUserId(username, loggedInUser.getId());
            }
            boolean userHasData = profileRepo.existsByUsernameAndSourceAndUserId(username, source, loggedInUser.getId()) ||
                    gameRepo.existsByUsernameAndLocalProfile_User_Id(username, loggedInUser.getId()) ||
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

            long profileCount = profileRepo.countByUsernameAndSourceAndUserId(username, source, loggedInUser.getId());
            long gameCount = gameRepo.countByUsernameAndSourceAndLocalProfile_User_Id(username, source, loggedInUser.getId());

            long syncCount;
            if(source.equals("chesscom")) {
                syncCount = syncRepo.countByUsernameAndUserId(username, loggedInUser.getId());
            } else {
                syncCount = chesscomSyncRepo.countByUsernameAndUserId(username, loggedInUser.getId());
            }
            profileRepo.deleteByUsernameAndSourceAndUserId(username, source, loggedInUser.getId());
            gameRepo.deleteAllByUsernameAndSourceAndLocalProfile_User_Id(username, source, loggedInUser.getId());

            if(source.equals("chesscom")) {
                chesscomSyncRepo.deleteAllByUsernameAndUserId(username, loggedInUser.getId());
            }
            else {
                syncRepo.deleteAllByUsernameAndUserId(username, loggedInUser.getId());
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