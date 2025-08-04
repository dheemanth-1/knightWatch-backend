package com.example.knightWatch.repository;


import com.example.knightWatch.model.PlayerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, Long> {
    PlayerProfile findByUsername(String username);
}