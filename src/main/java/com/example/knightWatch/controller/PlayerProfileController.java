package com.example.knightWatch.controller;

import com.example.knightWatch.model.PlayerProfile;
import com.example.knightWatch.repository.PlayerProfileRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerProfileController {

    private final PlayerProfileRepository repository;

    public PlayerProfileController(PlayerProfileRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<PlayerProfile>> getAllPlayers() {
        List<PlayerProfile> players = repository.findAll();

        if (players.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(players);
    }

    @PostMapping
    public ResponseEntity<PlayerProfile> createPlayer(@RequestBody PlayerProfile profile) {
        try {
            PlayerProfile savedProfile = repository.save(profile);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProfile);
        } catch (DataIntegrityViolationException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<PlayerProfile> getPlayerByUsername(@PathVariable String username) {
        PlayerProfile player = repository.findByUsername(username);

        if (player == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(player);
    }

    @PutMapping("/{username}")
    public ResponseEntity<PlayerProfile> updatePlayer(
            @PathVariable String username,
            @RequestBody PlayerProfile updatedProfile) {
        PlayerProfile existingPlayer = repository.findByUsername(username);

        if (existingPlayer == null) {
            return ResponseEntity.notFound().build();
        }


        existingPlayer.setTotalGames(updatedProfile.getTotalGames());
        existingPlayer.setWinRate(updatedProfile.getWinRate());
        existingPlayer.setLastSyncTime(updatedProfile.getLastSyncTime());

        PlayerProfile saved = repository.save(existingPlayer);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deletePlayer(@PathVariable String username) {
        PlayerProfile player = repository.findByUsername(username);

        if (player == null) {
            return ResponseEntity.notFound().build();
        }

        repository.delete(player);
        return ResponseEntity.noContent().build();
    }
}
