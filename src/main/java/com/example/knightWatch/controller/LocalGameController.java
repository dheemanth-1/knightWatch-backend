package com.example.knightWatch.controller;

import com.example.knightWatch.model.User;
import com.example.knightWatch.projection.LocalGameProjection;
import com.example.knightWatch.repository.LocalGameRepository;
import com.example.knightWatch.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/db/local/games")
public class LocalGameController {

    private final LocalGameRepository localGameRepository;
    private final UserRepository userRepository;

    public LocalGameController(LocalGameRepository localGameRepository, UserRepository userRepository) {
        this.localGameRepository = localGameRepository;
        this.userRepository = userRepository;

    }

    @GetMapping("/recent/{username}")
    public ResponseEntity<?> getRecentCachedGames(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @PathVariable String username) {
        String loggedInUsername = userDetails.getUsername();
        User loggedInUser = userRepository.findByUsername(loggedInUsername).orElseThrow(() -> new RuntimeException("User not found"));
        List<LocalGameProjection> games = localGameRepository.findTop10ProjectedByUsernameAndLocalProfile_User_IdOrderByPlayedAtDesc(username, loggedInUser.getId());
        if (games.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getCachedGames(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @PathVariable String username) {
        String loggedInUsername = userDetails.getUsername();
        User loggedInUser = userRepository.findByUsername(loggedInUsername).orElseThrow(() -> new RuntimeException("User not found"));
        List<LocalGameProjection> games = localGameRepository.findProjectedByUsernameAndLocalProfile_User_Id(username, loggedInUser.getId());
        if (games.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(games);

    }

    @GetMapping("/{source}/{username}")
    public ResponseEntity<?> getCachedGamesFromUsernameAndSource(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @PathVariable String username, @PathVariable String source) {
        String loggedInUsername = userDetails.getUsername();
        User loggedInUser = userRepository.findByUsername(loggedInUsername).orElseThrow(() -> new RuntimeException("User not found"));
        List<LocalGameProjection> games = localGameRepository.findProjectedByUsernameAndSourceAndLocalProfile_User_Id(username, source, loggedInUser.getId());
        if (games.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(games);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        if(ex.getMessage().equals("Service error")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}