package com.example.knightWatch.serviceTests;
import chariot.Client;
import chariot.api.UsersApi;
import chariot.model.*;
import com.example.knightWatch.model.LocalGame;
import com.example.knightWatch.repository.LocalGameRepository;
import com.example.knightWatch.service.LichessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LichessServiceTests {

    @Mock
    private Client clientMock;

    @Mock
    private LocalGameRepository gameRepoMock;

    @Mock
    private UsersApi usersApiMock;

    @Mock
    private java.util.function.Supplier<User> userSupplierMock;

    private LichessService lichessService;


    @BeforeEach
    void setup() {
        //when(clientMock.users()).thenReturn(usersApiMock);

        lichessService = new LichessService(clientMock, gameRepoMock);
    }

    @Test
    void getRecentGames_returnsTop10GamesByUsername() {

        String username = "testuser";
        List<LocalGame> expectedGames = createSampleGames(username, 5);

        when(gameRepoMock.findTop10ByUsernameOrderByPlayedAtDesc(username))
                .thenReturn(expectedGames);


        List<LocalGame> actualGames = lichessService.getRecentGames(username);


        assertEquals(expectedGames, actualGames);
        assertEquals(5, actualGames.size());
        verify(gameRepoMock).findTop10ByUsernameOrderByPlayedAtDesc(username);
        verifyNoMoreInteractions(gameRepoMock);
    }

    @Test
    void getRecentGames_returnsEmptyListWhenNoGamesFound() {

        String username = "newuser";
        when(gameRepoMock.findTop10ByUsernameOrderByPlayedAtDesc(username))
                .thenReturn(List.of());


        List<LocalGame> actualGames = lichessService.getRecentGames(username);


        assertTrue(actualGames.isEmpty());
        verify(gameRepoMock).findTop10ByUsernameOrderByPlayedAtDesc(username);
    }

    @Test
    void getAllGames_returnsAllGamesByUsername() {

        String username = "testuser";
        List<LocalGame> expectedGames = createSampleGames(username, 15);

        when(gameRepoMock.findByUsername(username)).thenReturn(expectedGames);


        List<LocalGame> actualGames = lichessService.getAllGames(username);


        assertEquals(expectedGames, actualGames);
        assertEquals(15, actualGames.size());
        verify(gameRepoMock).findByUsername(username);
        verifyNoMoreInteractions(gameRepoMock);
    }

    @Test
    void getAllGames_returnsEmptyListWhenNoGamesFound() {

        String username = "newuser";
        when(gameRepoMock.findByUsername(username)).thenReturn(List.of());


        List<LocalGame> actualGames = lichessService.getAllGames(username);


        assertTrue(actualGames.isEmpty());
        verify(gameRepoMock).findByUsername(username);
    }

    @Test
    void getUserProfile_returnsUserWhenFound() {

        String username = "testuser";

        User dummyUser = new UserProfileData(
                null,
                null,
                null,
                null,
                null,
                Opt.empty(),
                Opt.empty(),
                Opt.empty(),
                Opt.empty(),
                Opt.empty(),
                Opt.empty(),
                URI.create("https://lichess.org/@/" + username)
        );

        when(clientMock.users()).thenReturn(usersApiMock);

        when(usersApiMock.byId(username)).thenAnswer(invocation -> One.entry(dummyUser));

        Optional<User> result = lichessService.getUserProfile(username);


        assertTrue(result.isPresent());
        assertEquals("https://lichess.org/@/" + username, result.get().url().toString());
        verify(clientMock).users();
        verify(usersApiMock).byId(username);
    }

    @Test
    void getUserProfile_returnsEmptyWhenUserNotFound() {

        String username = "nonexistentuser";
        when(clientMock.users()).thenReturn(usersApiMock);
        when(usersApiMock.byId(username)).thenReturn(chariot.model.One.none());

        Optional<User> result = lichessService.getUserProfile(username);

        assertTrue(result.isEmpty());
        verify(clientMock).users();
        verify(usersApiMock).byId(username);
    }

    @Test
    void getUserProfile_handlesNullUsername() {

        String username = null;
        when(clientMock.users()).thenReturn(usersApiMock);
        when(usersApiMock.byId(username)).thenReturn(chariot.model.One.none());


        Optional<User> result = lichessService.getUserProfile(username);


        assertTrue(result.isEmpty());
        verify(clientMock).users();
        verify(usersApiMock).byId(username);
    }

    @Test
    void getUserProfile_handlesApiException() {

        String username = "testuser";
        when(clientMock.users()).thenReturn(usersApiMock);
        when(usersApiMock.byId(username)).thenThrow(new RuntimeException("API Error"));


        assertThrows(RuntimeException.class, () -> {
            lichessService.getUserProfile(username);
        });

        verify(clientMock).users();
        verify(usersApiMock).byId(username);
    }

    // Helper method to create sample games
    private List<LocalGame> createSampleGames(String username, int count) {
        List<LocalGame> games = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            LocalGame game = new LocalGame();
            game.setGameId("game" + i);
            game.setUsername(username);
            game.setResult(i % 2 == 0 ? "1-0" : "0-1");
            game.setOpeningName("Opening " + i);
            game.setPlayedAt(String.valueOf(LocalDateTime.now().minusDays(i)));
            games.add(game);
        }
        return games;
    }



    @Test
    void constructor_initializesFieldsCorrectly() {

        Client testClient = mock(Client.class);
        LocalGameRepository testRepo = mock(LocalGameRepository.class);

        LichessService service = new LichessService(testClient, testRepo);

        assertNotNull(service);
    }

    @Test
    void getRecentGames_verifyRepositoryMethodCalled() {

        String username = "verifyuser";
        List<LocalGame> emptyList = List.of();
        when(gameRepoMock.findTop10ByUsernameOrderByPlayedAtDesc(username))
                .thenReturn(emptyList);


        lichessService.getRecentGames(username);


        verify(gameRepoMock, times(1)).findTop10ByUsernameOrderByPlayedAtDesc(username);
        verify(gameRepoMock, never()).findByUsername(anyString());
    }

    @Test
    void getAllGames_verifyRepositoryMethodCalled() {

        String username = "verifyuser";
        List<LocalGame> emptyList = List.of();
        when(gameRepoMock.findByUsername(username)).thenReturn(emptyList);


        lichessService.getAllGames(username);


        verify(gameRepoMock, times(1)).findByUsername(username);
        verify(gameRepoMock, never()).findTop10ByUsernameOrderByPlayedAtDesc(anyString());
    }
}