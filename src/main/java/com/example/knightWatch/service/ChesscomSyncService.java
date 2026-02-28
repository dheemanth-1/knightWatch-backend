package com.example.knightWatch.service;

import com.example.knightWatch.dto.ChesscomPlayerDTO;
import com.example.knightWatch.model.ChesscomSyncStatus;
import com.example.knightWatch.model.LocalGame;
import com.example.knightWatch.model.LocalProfile;
import com.example.knightWatch.model.User;
import com.example.knightWatch.repository.ChesscomSyncStatusRepository;
import com.example.knightWatch.repository.LocalGameRepository;
import com.example.knightWatch.repository.LocalProfileRepository;
import io.github.sornerol.chess.pubapi.client.PlayerClient;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChesscomSyncService {
    private final ChesscomGamesService chesscomGamesService;
    private final PlayerClient playerClient;
    private final LocalProfileRepository profileRepo;
    private final LocalGameRepository localGameRepo;
    private final ChesscomSyncStatusRepository chesscomSyncStatusRepo;

    public ChesscomSyncService(ChesscomGamesService chesscomGamesService, PlayerClient playerClient, LocalProfileRepository profileRepo, LocalGameRepository localGameRepo, ChesscomSyncStatusRepository chesscomSyncStatusRepo) {
        this.chesscomGamesService = chesscomGamesService;
        this.playerClient = playerClient;
        this.localGameRepo = localGameRepo;
        this.profileRepo = profileRepo;
        this.chesscomSyncStatusRepo = chesscomSyncStatusRepo;
    }

    private static final Logger log = LoggerFactory.getLogger(ChesscomSyncService.class);

    @Transactional
    public ChesscomSyncStatus syncUser(String username, Integer year, Integer month, User loggedInUser) throws ChessComPubApiException, IOException {

        if(this.chesscomSyncStatusRepo.existsByUsernameAndYearAndMonth(username, year, month)) {
            System.out.println("already synced.");
            return chesscomSyncStatusRepo.findByUsernameAndYearAndMonth(username, year, month);
        }

        LocalProfile localPlayer;
        if(this.profileRepo.findByUsernameAndSource(username, "chesscom").isEmpty()) {
            var player = this.playerClient.getPlayerByUsername(username);
            var stats = this.playerClient.getStatsForPlayer(username);

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
            System.out.println("player dto :" + chesscomPlayerDTO);
            localPlayer = new LocalProfile(chesscomPlayerDTO, "chesscom");
            localPlayer.setUser(loggedInUser);
            this.profileRepo.save(localPlayer);
        }

        List<LocalGame> gameList = this.chesscomGamesService.fetchUserGamesWithOpenings(username, year, month, loggedInUser);
        this.localGameRepo.saveAll(gameList);

        var status = new ChesscomSyncStatus(username, LocalDateTime.now(), year, month, gameList.size(), this.localGameRepo.countByUsernameAndSource(username, "chesscom"));
        status.setUser(loggedInUser);
        this.chesscomSyncStatusRepo.save(status);
        return status;
    }



}
