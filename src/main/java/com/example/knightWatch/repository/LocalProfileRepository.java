package com.example.knightWatch.repository;

import com.example.knightWatch.model.LocalProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocalProfileRepository extends JpaRepository<LocalProfile, Long> {
    Optional<LocalProfile> findByUsername(String username);
    Optional<LocalProfile> findByUsernameAndSource(String username, String source);
    void deleteByUsername(String username);
    void deleteByUsernameAndSource(String username, String source);
    boolean existsByUsername(String username);
    boolean existsByUsernameAndSource(String username, String source);
    long countByUsername(String username);
    long countByUsernameAndSource(String username, String source);
}