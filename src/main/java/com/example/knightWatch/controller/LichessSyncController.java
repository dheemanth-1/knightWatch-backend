package com.example.knightWatch.controller;

import com.example.knightWatch.service.LichessSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lichess")
public class LichessSyncController {

    private final LichessSyncService syncService;

    public LichessSyncController(LichessSyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping("/sync/{username}")
    public ResponseEntity<String> syncUser(@PathVariable String username) {
        try {
            syncService.syncUser(username);
            return ResponseEntity.ok("Synced data for user: " + username);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to sync data: " + e.getMessage());
        }
    }
}