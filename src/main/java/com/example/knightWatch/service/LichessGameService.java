package com.example.knightWatch.service;

import chariot.Client;
import chariot.api.GamesApi;
import chariot.model.Game;
import com.example.knightWatch.dto.OpeningInfo;
import com.example.knightWatch.model.LichessGame;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LichessGameService {

    public LichessGameService(Client lichessClient) {
        this.gamesApi = lichessClient.games();
    }

    private final GamesApi gamesApi;

    public List<LichessGame> fetchUserGamesWithOpenings(String username, int maxGames) {
        // Get PGN data with opening information
        List<String> pgnGames = gamesApi.pgnByUserId(username, params -> {
            params.opening(true);
            params.tags(true);
            params.max(maxGames);
        }).stream().map(Object::toString).toList();

        // Get regular Game objects
        List<Game> games = gamesApi.byUserId(username).stream().limit(maxGames).toList();

        // Create opening info map from PGN
        Map<String, OpeningInfo> openingMap = new HashMap<>();
        for (String pgn : pgnGames) {
            String gameId = extractFromPgn(pgn, "GameId");
            String eco = extractFromPgn(pgn, "ECO");
            String opening = extractFromPgn(pgn, "Opening");
            String resultNotation = extractFromPgn(pgn,"Result");
            String black = extractFromPgn(pgn,"Black");
            String white = extractFromPgn(pgn,"White");
            String timeControl = extractFromPgn(pgn,"TimeControl");
            if (gameId != null) {
                openingMap.put(gameId, new OpeningInfo(gameId, eco, opening, pgn, resultNotation, black, white ,timeControl));
            }
        }

        // Convert to LichessGame objects with opening data
        List<LichessGame> lichessGames = new ArrayList<>();
        for (Game game : games) {
            LichessGame lichessGame = new LichessGame(game, username);

            // Set opening name from PGN data
            OpeningInfo openingInfo = openingMap.get(game.id());
            if (openingInfo != null) {
                lichessGame.setOpeningName(openingInfo.getBestOpeningName());
                // Optionally store PGN in entity if you modified it
                lichessGame.setPgn(openingInfo.getPgn());
                lichessGame.setEco(openingInfo.getEco());
                lichessGame.setResult(openingInfo.getResultFromColor(username));
            }

            lichessGames.add(lichessGame);
        }

        return lichessGames;
    }

    private String extractFromPgn(String pgn, String tagName) {
        String pattern = "\\[" + tagName + " \"([^\"]+)\"\\]";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(pgn);
        return matcher.find() ? matcher.group(1) : null;
    }
}