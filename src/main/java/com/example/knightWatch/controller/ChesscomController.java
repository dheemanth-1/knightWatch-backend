package com.example.knightWatch.controller;


import com.example.knightWatch.dto.ChesscomPlayerDTO;
import com.example.knightWatch.model.LocalGame;
import com.example.knightWatch.service.ChesscomGamesService;

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

import java.util.List;

@RestController
@RequestMapping("/api/chesscom")
public class ChesscomController {
    private final PlayerClient playerClient;
    private final ChesscomGamesService chesscomGamesService;


    public ChesscomController(PlayerClient playerClient, ChesscomGamesService chesscomGamesService) {
        this.playerClient = playerClient;
        this.chesscomGamesService = chesscomGamesService;
    }

    @GetMapping("/{username}/profile")
    public ResponseEntity<?> getProfile(@PathVariable String username) throws ChessComPubApiException, IOException {
        try {
            Player player = playerClient.getPlayerByUsername(username);
            PlayerStats stats = playerClient.getStatsForPlayer(username);

            if (player != null && stats != null) {
                ChesscomPlayerDTO chesscomPlayerDTO = new ChesscomPlayerDTO(player.getId().substring(player.getId().lastIndexOf("/") + 1),
                        player.getName(),
                        player.getCountryApiUrl().substring(player.getCountryApiUrl().lastIndexOf("/") + 1),
                        player.getMembershipStatus().getValue(),
                        player.getJoined(),
                        player.getLastOnline(),
                        stats.getChessBlitz() != null? (stats.getChessBlitz().getRecord().getWin() + stats.getChessBlitz().getRecord().getLoss() +  stats.getChessBlitz().getRecord().getDraw()) : 0,
                        stats.getChessRapid() != null? (stats.getChessRapid().getRecord().getWin() + stats.getChessRapid().getRecord().getLoss() + stats.getChessRapid().getRecord().getDraw()) : 0,
                        stats.getChessBullet() != null? (stats.getChessBullet().getRecord().getWin() + stats.getChessBullet().getRecord().getLoss() +  stats.getChessBullet().getRecord().getDraw()) : 0,
                        stats.getChessDaily() != null? (stats.getChessDaily().getRecord().getWin() + stats.getChessDaily().getRecord().getLoss() + stats.getChessDaily().getRecord().getDraw()) : 0,
                        stats.getChessBlitz() != null? stats.getChessBlitz().getLast().getRating(): 0,
                        stats.getChessRapid() != null? stats.getChessRapid().getLast().getRating() : 0,
                        stats.getChessBullet() != null? stats.getChessBullet().getLast().getRating() : 0,
                        stats.getChessDaily() != null? stats.getChessDaily().getLast().getRating() : 0,
                        stats.getTactics().getHighest().getRating(),
                        player.getUrl(),
                        player.getFollowers(),
                        stats.getPuzzleRush().getBest().getScore()
                );

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
            List<LocalGame> gameList = this.chesscomGamesService.fetchUserGamesWithOpenings(username, year, month);

            if (list != null) {
                return ResponseEntity.ok(gameList);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
