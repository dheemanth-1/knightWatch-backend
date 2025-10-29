package com.example.knightWatch.repository;

import com.example.knightWatch.model.LocalProfile;
import com.example.knightWatch.projection.LocalProfileProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
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
    Optional<LocalProfile> findEntityByUserIdAndUsername(long userId, String username);


    Optional<LocalProfileProjection> findProjectedByUserIdAndUsername(long userId, String username);
    Optional<LocalProfileProjection> findProjectedByUserIdAndUsernameAndSource(long userId, String username, String source);
    void deleteByUsernameAndSourceAndUserId(String username, String source, long userId);
    boolean existsByUsernameAndUserId(String username, long userId);
    boolean existsByUsernameAndSourceAndUserId(String username, String source, long userId);
    long countByUsernameAndSourceAndUserId(String username, String source, long userId);
    List<LocalProfileProjection> findProjectedByUserId(long userId);
}