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

        List<LichessGame> lichessGames = new ArrayList<>();
        for (String pgn : pgnGames) {
            Map<String, String> tags = parseTags(pgn);
            String gameId = tags.get("GameId");
            String eco = tags.get("ECO");
            String opening = tags.get("Opening");
            String resultNotation = tags.get("Result");
            String black = tags.get("Black");
            String white = tags.get("White");
            String timeControl = tags.get("TimeControl");
            String status = tags.get("Termination");
            String date = tags.get("UTCDate");
            String time = tags.get("UTCTime");
            String formattedDateTime = date.replace(".", "-") + "T" + time;

            OpeningInfo openingInfo = new OpeningInfo(gameId, eco, opening, pgn, resultNotation, black, white ,timeControl,status, formattedDateTime);
            LichessGame lichessGame = new LichessGame(openingInfo, username);
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

    private Map<String, String> parseTags(String pgn) {
        Map<String, String> tags = new HashMap<>();
        Matcher matcher = Pattern.compile("\\[(\\w+) \"([^\"]+)\"\\]").matcher(pgn);
        while (matcher.find()) {
            tags.put(matcher.group(1), matcher.group(2));
        }
        return tags;
    }
}