package com.example.knightWatch.controller;

import com.example.knightWatch.dto.SyncStatusDTO;
import com.example.knightWatch.dto.TotalGamesCount;
import com.example.knightWatch.model.LocalProfile;
import com.example.knightWatch.model.PlayerProfile;
import com.example.knightWatch.model.LichessSyncStatus;
import com.example.knightWatch.model.User;
import com.example.knightWatch.projection.LichessSyncStatusProjection;
import com.example.knightWatch.repository.LocalGameRepository;
import com.example.knightWatch.repository.LocalProfileRepository;
import com.example.knightWatch.repository.PlayerProfileRepository;
import com.example.knightWatch.repository.UserRepository;
import com.example.knightWatch.service.LichessSyncService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/lichess")
public class LichessSyncController {
    private static final Logger log = LoggerFactory.getLogger(LichessSyncController.class);
    private final LichessSyncService syncService;
    private final PlayerProfileRepository playerProfileRepository;
    private final LocalProfileRepository localProfileRepository;
    private final LocalGameRepository gameRepo;
    private final UserRepository userRepository;

    public LichessSyncController(LichessSyncService syncService, PlayerProfileRepository playerProfileRepository, LocalProfileRepository localProfileRepository, LocalGameRepository gameRepo, UserRepository userRepository) {
        this.syncService = syncService;
        this.playerProfileRepository = playerProfileRepository;
        this.localProfileRepository = localProfileRepository;
        this.gameRepo = gameRepo;
        this.userRepository = userRepository;
    }

    @GetMapping("/sync/lastSynced/{username}")
    public ResponseEntity<SyncStatusDTO> isSynced(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @PathVariable String username) {
        String loggedInUsername = userDetails.getUsername();
        User loggedInUser = userRepository.findByUsername(loggedInUsername).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(this.syncService.previousSyncCheck(username, loggedInUser.getId()));
    }


    @PostMapping("/sync/{username}")
    public ResponseEntity<SyncStatusDTO> syncUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
                                                  @PathVariable String username, @RequestParam(name = "games") Optional<Integer> numberOfGames) {
        try {
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new SyncStatusDTO(null, false, null, null, username));
            }

            String loggedInUsername = userDetails.getUsername();
            User loggedInUser = userRepository.findByUsername(loggedInUsername).orElseThrow(() -> new RuntimeException("User not found"));

            LichessSyncStatus lichessSyncStatus = syncService.syncUser(username, numberOfGames, loggedInUser);
            Optional<LocalProfile> localProfile = localProfileRepository.findByUsernameAndSource(username, "lichess");
            if(localProfile.isEmpty()) {
                System.out.println("is profile null??");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new SyncStatusDTO(null, false, null, null, username));
            }
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
            var lastSyncDateTime = LocalDateTime.parse(lichessSyncStatus.getLastSync()); //, formatter
            SyncStatusDTO dto = new SyncStatusDTO(
                    lastSyncDateTime,
                    lichessSyncStatus.isUptoDate(),
                    lichessSyncStatus.getLastLocalGameDate(),
                    lichessSyncStatus.getNumberOfGamesSynced(),
                    lichessSyncStatus.getUsername()
            );
            return ResponseEntity.ok(dto);
        }catch (IllegalArgumentException e) {
            log.warn("Invalid sync request for user {}: {}", username, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new SyncStatusDTO(null, false, null, null, username));
        } catch (Exception e) {
            System.out.println("exception caught: " + e.toString());
            SyncStatusDTO errorDto = new SyncStatusDTO(
                    null,
                    false,
                    null,
                    null,
                    username
            );
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorDto);
        }
    }

    @GetMapping("/sync/games-count/{username}")
    public ResponseEntity<TotalGamesCount> getTotalGames(@PathVariable String username) {
        int totalGames = syncService.getTotalGames(username);
        TotalGamesCount response = new TotalGamesCount(username, totalGames);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/syncHistory/{username}")
    public ResponseEntity<List<LichessSyncStatusProjection>> syncHistory(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @PathVariable String username) {
        String loggedInUsername = userDetails.getUsername();
        User loggedInUser = userRepository.findByUsername(loggedInUsername).orElseThrow(() -> new RuntimeException("User not found"));

        List<LichessSyncStatusProjection> list = syncService.syncHistory(username, loggedInUser.getId());
        if(list.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }
}