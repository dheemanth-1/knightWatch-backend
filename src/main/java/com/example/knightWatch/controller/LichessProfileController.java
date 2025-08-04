package com.example.knightWatch.controller;

import com.example.knightWatch.model.LichessProfile;
import com.example.knightWatch.repository.LichessGameRepository;
import com.example.knightWatch.repository.LichessProfileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/db/lichess/profile")
@Tag(name = "Lichess Profile", description = "APIs for cached Lichess profile data")
public class LichessProfileController {

    private final LichessProfileRepository profileRepo;
    private final LichessGameRepository gameRepo;

    public LichessProfileController(LichessProfileRepository profileRepo, LichessGameRepository gameRepo) {
        this.profileRepo = profileRepo;
        this.gameRepo = gameRepo;
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
    public ResponseEntity<Void> deleteUserData(@PathVariable String username) {
        profileRepo.deleteByUsername(username);
        gameRepo.deleteAllByUsername(username);
        return ResponseEntity.noContent().build();
    }


}