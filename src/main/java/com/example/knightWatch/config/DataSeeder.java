package com.example.knightWatch.config;

import com.example.knightWatch.model.PlayerProfile;
import com.example.knightWatch.repository.PlayerProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PlayerProfileRepository repository;

    public DataSeeder(PlayerProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            repository.save(new PlayerProfile("MagnusCarlsen", 1000, 72.5));
            repository.save(new PlayerProfile("HikaruNakamura", 850, 69.8));
        }
    }
}