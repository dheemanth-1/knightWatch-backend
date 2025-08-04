package com.example.knightWatch.repository;

import com.example.knightWatch.model.LichessGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LichessGameRepository extends JpaRepository<LichessGame, Long> {
    List<LichessGame> findByUsername(String username);
    void deleteAllByUsername(String username);
    List<LichessGame> findTop10ByUsernameOrderByPlayedAtDesc(String username);
}