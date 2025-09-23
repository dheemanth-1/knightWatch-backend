package com.example.knightWatch.controller;

import com.example.knightWatch.dto.SyncStatusDTO;
import com.example.knightWatch.dto.TotalGamesCount;
import com.example.knightWatch.model.PlayerProfile;
import com.example.knightWatch.model.SyncStatus;
import com.example.knightWatch.repository.LichessGameRepository;
import com.example.knightWatch.repository.PlayerProfileRepository;
import com.example.knightWatch.service.LichessSyncService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/lichess")
public class LichessSyncController {
    private static final Logger log = LoggerFactory.getLogger(LichessSyncController.class);
    private final LichessSyncService syncService;
    private final PlayerProfileRepository playerProfileRepository;
    private final LichessGameRepository gameRepo;

    public LichessSyncController(LichessSyncService syncService, PlayerProfileRepository playerProfileRepository, LichessGameRepository gameRepo) {
        this.syncService = syncService;
        this.playerProfileRepository = playerProfileRepository;
        this.gameRepo = gameRepo;
    }

    @GetMapping("/sync/lastSynced/{username}")
    public ResponseEntity<SyncStatusDTO> isSynced(@PathVariable String username) {
        return ResponseEntity.ok(this.syncService.previousSyncCheck(username));
    }


    @PostMapping("/sync/{username}")
    public ResponseEntity<SyncStatusDTO> syncUser(@PathVariable String username,  @RequestParam(name = "games") Optional<Integer> numberOfGames) {
        try {
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new SyncStatusDTO(null, false, null, null));
            }
            SyncStatus syncStatus = syncService.syncUser(username, numberOfGames);
            PlayerProfile profile =  playerProfileRepository.findByUsername(username);
            if(profile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new SyncStatusDTO(null, false, null, null));
            }
            SyncStatusDTO dto = new SyncStatusDTO(
                    profile.getLastSyncTime(),
                    syncStatus.isUptoDate(),
                    syncStatus.getLastLocalGameDate(),
                    syncStatus.getNumberOfGamesSynced()
            );
            return ResponseEntity.ok(dto);
        }catch (IllegalArgumentException e) {
            log.warn("Invalid sync request for user {}: {}", username, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new SyncStatusDTO(null, false, null, null));
        } catch (Exception e) {
            System.out.println("exception caught: " + e.toString());
            SyncStatusDTO errorDto = new SyncStatusDTO(
                    null,
                    false,
                    null,
                    null
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
    public ResponseEntity<List<SyncStatus>> syncHistory(@PathVariable String username) {
        List<SyncStatus> list = syncService.syncHistory(username);
        if(list.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }
}