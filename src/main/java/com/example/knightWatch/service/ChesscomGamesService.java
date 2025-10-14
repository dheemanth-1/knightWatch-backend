package com.example.knightWatch.service;

import com.example.knightWatch.dto.OpeningInfo;
import com.example.knightWatch.model.LocalGame;
import com.example.knightWatch.repository.LocalGameRepository;
import io.github.sornerol.chess.pubapi.client.PlayerClient;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChesscomGamesService {
    private final  PlayerClient playerClient;
    private final LocalGameRepository localGameRepo;
    public ChesscomGamesService(PlayerClient playerClient, LocalGameRepository localGameRepo) {
        this.playerClient = playerClient;
        this.localGameRepo = localGameRepo;
    }

    public List<LocalGame> fetchUserGamesWithOpenings(String username, Integer year, Integer month) throws ChessComPubApiException, IOException {
        List<LocalGame> list = new ArrayList<>();
        try {
            String pgnGames = playerClient.getMonthlyArchiveForPlayerAsPgn(username, year, month);
            List<String> pgnList = splitPgns(pgnGames);
            for(String pgn : pgnList) {
                Map<String, String> tags = parseTags(pgn);

                OpeningInfo openingInfo = new OpeningInfo(
                        tags.get("Link"),
                        tags.get("ECO"),
                        tags.get("ECOUrl"),
                        pgn,
                        tags.get("Result"),
                        tags.get("Black"),
                        tags.get("White"),
                        tags.get("TimeControl"),
                        tags.get("Termination"),
                        tags.get("UTCDate") + "T" + tags.get("UTCTime"),
                        "chesscom"
                );
                list.add(new LocalGame(openingInfo, username));
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    private Map<String, String> parseTags(String pgn) {
        Map<String, String> tags = new HashMap<>();
        Matcher matcher = Pattern.compile("\\[(\\w+) \"([^\"]+)\"\\]").matcher(pgn);
        while (matcher.find()) {
            tags.put(matcher.group(1), matcher.group(2));
        }
        return tags;
    }

    public List<String> splitPgns(String multiplePgns) {
        List<String> pgnList = new ArrayList<>();


        String[] games = multiplePgns.split("(?=\\[Event)");

        for (String game : games) {
            String trimmed = game.trim();
            if (!trimmed.isEmpty()) {
                pgnList.add(trimmed);
            }
        }

        return pgnList;
    }
}
