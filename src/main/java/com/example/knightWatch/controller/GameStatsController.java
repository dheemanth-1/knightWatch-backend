package com.example.knightWatch.controller;

import com.example.knightWatch.dto.GameStatsByOpening;
import com.example.knightWatch.dto.OverallStats;
import com.example.knightWatch.model.User;
import com.example.knightWatch.repository.UserRepository;
import com.example.knightWatch.service.GameStatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class GameStatsController {
    private final GameStatsService gameStatsService;
    private final UserRepository userRepository;

    public GameStatsController(GameStatsService gameStatsService, UserRepository userRepository) {
        this.gameStatsService = gameStatsService;
        this.userRepository = userRepository;
    }

//    @GetMapping("/openings/{username}")
//    public List<GameStatsByOpening> getStatsByOpening(@PathVariable String username) {
//        return gameStatsService.calculateStatsByOpening(username);
//    }
//
//    @GetMapping("/overall/{username}")
//    public OverallStats getOverallStats(@PathVariable String username) {
//        return gameStatsService.calculateOverallStats(username);
//    }

    @GetMapping("/openings/{username}/{source}")
    public List<GameStatsByOpening> getStatsByOpeningFromSource(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @PathVariable String username, @PathVariable String source) {
        String loggedInUsername = userDetails.getUsername();
        User loggedInUser = userRepository.findByUsername(loggedInUsername).orElseThrow(() -> new RuntimeException("User not found"));
        return gameStatsService.calculateStatsByOpening(username, source, loggedInUser.getId());
    }

    @GetMapping("/overall/{username}/{source}")
    public OverallStats getOverallStatsFromSource(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @PathVariable String username, @PathVariable String source) {
        String loggedInUsername = userDetails.getUsername();
        User loggedInUser = userRepository.findByUsername(loggedInUsername).orElseThrow(() -> new RuntimeException("User not found"));
        return gameStatsService.calculateOverallStats(username, source, loggedInUser.getId());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        if(ex.getMessage().equals("Service error")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}

