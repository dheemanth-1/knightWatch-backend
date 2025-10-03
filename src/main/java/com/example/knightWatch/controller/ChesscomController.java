package com.example.knightWatch.controller;


import com.example.knightWatch.dto.ChesscomPlayerDTO;
import io.github.sornerol.chess.pubapi.client.PlayerClient;
import io.github.sornerol.chess.pubapi.domain.game.ArchiveGameList;
import io.github.sornerol.chess.pubapi.domain.player.Player;
import io.github.sornerol.chess.pubapi.domain.player.stats.PlayerStats;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/chesscom")
public class ChesscomController {
    private final PlayerClient playerClient;


    public ChesscomController(PlayerClient playerClient) {
        this.playerClient = playerClient;
    }

    @GetMapping("/{username}/profile")
    public ResponseEntity<?> getProfile(@PathVariable String username) throws ChessComPubApiException, IOException {
        try {
            Player player = playerClient.getPlayerByUsername(username);
            PlayerStats stats = playerClient.getStatsForPlayer(username);
            System.out.println(playerClient.getCurrentDailyChessGamesForPlayer(username));
            ChesscomPlayerDTO chesscomPlayerDTO = new ChesscomPlayerDTO(player.getId().substring(player.getId().lastIndexOf("/") + 1),
                                                                        player.getName(),
                    player.getCountryApiUrl().substring(player.getCountryApiUrl().lastIndexOf("/") + 1),
                                                                        player.getMembershipStatus().getValue(),
                                                                        player.getJoined(),
                                                                        player.getLastOnline(),
                    (stats.getChessBlitz().getRecord().getWin() + stats.getChessBlitz().getRecord().getLoss() +  stats.getChessBlitz().getRecord().getDraw()),
                    (stats.getChessRapid().getRecord().getWin() + stats.getChessRapid().getRecord().getLoss() + stats.getChessRapid().getRecord().getDraw()),
                    stats.getChessBlitz().getLast().getRating(),
                    stats.getChessRapid().getLast().getRating(),
                    player.getUrl()
                    );

            if (player != null) {
                return ResponseEntity.ok(chesscomPlayerDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ChessComPubApiException | IOException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{username}/games/{year}/{month}")
    public ResponseEntity<?> getGamesByMonth(@PathVariable String username, @PathVariable Integer year, @PathVariable Integer month) {
        try {
            ArchiveGameList list = playerClient.getMonthlyArchiveForPlayer(username, year, month);
            if (list != null) {
                return ResponseEntity.ok(list);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
