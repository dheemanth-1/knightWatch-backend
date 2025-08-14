package com.example.knightWatch.controller;

import com.example.knightWatch.dto.SyncStatusDTO;
import com.example.knightWatch.model.PlayerProfile;
import com.example.knightWatch.repository.LichessGameRepository;
import com.example.knightWatch.repository.PlayerProfileRepository;
import com.example.knightWatch.service.LichessSyncService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/lichess")
public class LichessSyncController {

    private final LichessSyncService syncService;
    private final PlayerProfileRepository playerProfileRepository;
    private final LichessGameRepository gameRepo;

    public LichessSyncController(LichessSyncService syncService, PlayerProfileRepository playerProfileRepository, LichessGameRepository gameRepo) {
        this.syncService = syncService;
        this.playerProfileRepository = playerProfileRepository;
        this.gameRepo = gameRepo;
    }

    @PostMapping("/sync/{username}")
    public ResponseEntity<SyncStatusDTO> syncUser(@PathVariable String username) {
        try {
            syncService.syncUser(username);
            PlayerProfile profile =  playerProfileRepository.findByUsername(username);
            if(profile == null) {
               throw new RuntimeException("Profile not found");
            }

            SyncStatusDTO dto = new SyncStatusDTO(
                    profile.getLastSyncTime(),
                    false, // sync is finished
                    gameRepo.findLatestGameDateByUsername(username)
            );
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.out.println("exception caught: " + e.toString());
            SyncStatusDTO errorDto = new SyncStatusDTO(
                    null,
                    false,
                    null
            );
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorDto);
        }
    }
}