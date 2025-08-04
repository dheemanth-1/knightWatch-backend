package com.example.knightWatch.controller;

import com.example.knightWatch.model.PlayerProfile;
import com.example.knightWatch.repository.PlayerProfileRepository;
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
    public List<PlayerProfile> getAllPlayers() {
        return repository.findAll();
    }

    @PostMapping
    public PlayerProfile createPlayer(@RequestBody PlayerProfile profile) {
        return repository.save(profile);
    }

    @GetMapping("/{username}")
    public PlayerProfile getPlayerByUsername(@PathVariable String username) {
        return repository.findByUsername(username);
    }
}
