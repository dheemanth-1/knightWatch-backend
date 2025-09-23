package com.example.knightWatch.repository;

import com.example.knightWatch.model.LichessProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LichessProfileRepository extends JpaRepository<LichessProfile, Long> {
    Optional<LichessProfile> findByUsername(String username);
    void deleteByUsername(String username);
    boolean existsByUsername(String username);
    long countByUsername(String username);
}