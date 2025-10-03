package com.example.knightWatch.serviceTests;

import chariot.Client;
import chariot.api.GamesApi;
import chariot.model.Many;
import chariot.model.Pgn;
import com.example.knightWatch.model.LocalGame;
import com.example.knightWatch.service.LichessGameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocalGameServiceTests {
    private Client clientMock;
    private LichessGameService service;
    private GamesApi gamesApiMock;

    @BeforeEach
    void setup() {
        gamesApiMock = mock(GamesApi.class);
        clientMock = mock(Client.class);
        when(clientMock.games()).thenReturn(gamesApiMock);
        service = new LichessGameService(clientMock);
    }

    @Test
    void fetchUserGamesWithOpenings_returnsExpectedLichessGames() {
        String username = "testuser";
        int maxGames = 2;
        String pgn1 = "[GameId \"id1\"]\n" +
                "[ECO \"B20\"]\n[Opening \"Sicilian Defense\"]\n[Result \"1-0\"]\n[White \"testuser\"]\n[Black \"Beta\"]\n\n1. e4 c5 1-0";
        String pgn2 = "[GameId \"id2\"]\n" +
                "[ECO \"C00\"]\n[Opening \"French Defense\"]\n[Result \"0-1\"]\n[White \"testuser\"]\n[Black \"Delta\"]\n\n1. e4 e6 0-1";


        List<Pgn> pgnObjects = List.of(
                Pgn.readFromString(pgn1).getFirst(),
                Pgn.readFromString(pgn2).getFirst()
        );


        when(gamesApiMock.pgnByUserId(anyString(), any())).thenReturn(Many.entries(pgnObjects.stream()));

        List<LocalGame> games = service.fetchUserGamesWithOpenings(username, maxGames);


        Assertions.assertEquals(2, games.size());
        LocalGame g1 = games.get(0);
        LocalGame g2 = games.get(1);


        Assertions.assertEquals("id1", g1.getGameId());
        Assertions.assertEquals("Sicilian Defense", g1.getOpeningName());
        Assertions.assertEquals("won", g1.getResult());


        Assertions.assertEquals("id2", g2.getGameId());
        Assertions.assertEquals("French Defense", g2.getOpeningName());
        Assertions.assertEquals("lost", g2.getResult());
    }
}
