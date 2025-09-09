package com.example.knightWatch.controllerTests;
import com.example.knightWatch.controller.LichessGameController;
import com.example.knightWatch.model.LichessGame;
import com.example.knightWatch.repository.LichessGameRepository;
import com.example.knightWatch.service.LichessService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("LichessGameController Tests")
class LichessGameControllerTest {

    @Mock
    private LichessGameRepository gameRepo;

    @Mock
    private LichessService lichessService;

    @InjectMocks
    private LichessGameController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should return valid games when user exists")
    void getRecentCachedGames_WithValidUsername_ReturnsGames() throws Exception {

        String username = "testuser";
        List<LichessGame> mockGames = createMockGames(username);
        when(lichessService.getRecentGames(username)).thenReturn(mockGames);


        mockMvc.perform(get("/api/db/lichess/games/recent/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value(username))
                .andExpect(jsonPath("$[0].gameId").value("game1"))
                .andExpect(jsonPath("$[0].openingName").value("Italian Game"))
                .andExpect(jsonPath("$[1].username").value(username))
                .andExpect(jsonPath("$[1].gameId").value("game2"))
                .andExpect(jsonPath("$[1].openingName").value("Sicilian Defense"));
    }

    @Test
    @DisplayName("Should return 404 when games don't not exist")
    void getRecentCachedGames_WithNoGamesFound_ReturnsNotFound() throws Exception {

        String username = "nonexistentuser";
        when(lichessService.getRecentGames(username)).thenReturn(Collections.emptyList());


        mockMvc.perform(get("/api/db/lichess/games/recent/{username}", username))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when user is null")
    void getRecentCachedGames_WithEmptyUsername_ReturnsBadRequest() throws Exception {

        mockMvc.perform(get("/api/db/lichess/games/recent/"))
                .andExpect(status().isNotFound()); // Spring returns 404 for missing path variable
    }

    @Test
    void getCachedGames_WithValidUsername_ReturnsGames() throws Exception {

        String username = "testuser";
        List<LichessGame> mockGames = createMockGames(username);
        when(lichessService.getAllGames(username)).thenReturn(mockGames);


        mockMvc.perform(get("/api/db/lichess/games/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value(username))
                .andExpect(jsonPath("$[0].gameId").value("game1"))
                .andExpect(jsonPath("$[1].username").value(username))
                .andExpect(jsonPath("$[1].gameId").value("game2"));
    }

    @Test
    void getCachedGames_WithNoGamesFound_ReturnsNotFound() throws Exception {

        String username = "nonexistentuser";
        when(lichessService.getAllGames(username)).thenReturn(Collections.emptyList());


        mockMvc.perform(get("/api/db/lichess/games/{username}", username))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCachedGames_WithSpecialCharactersInUsername_ReturnsGames() throws Exception {

        String username = "test_user-123";
        List<LichessGame> mockGames = createMockGames(username);
        when(lichessService.getAllGames(username)).thenReturn(mockGames);


        mockMvc.perform(get("/api/db/lichess/games/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value(username));
    }

    @Test
    void getRecentCachedGames_ServiceThrowsException_ReturnsInternalServerError() throws Exception {

        String username = "testuser";
        when(lichessService.getRecentGames(anyString())).thenThrow(new RuntimeException("Service error"));


        mockMvc.perform(get("/api/db/lichess/games/recent/{username}", username))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getCachedGames_ServiceThrowsException_ReturnsInternalServerError() throws Exception {

        String username = "testuser";
        when(lichessService.getAllGames(anyString())).thenThrow(new RuntimeException("Service error"));


        mockMvc.perform(get("/api/db/lichess/games/{username}", username))
                .andExpect(status().isInternalServerError());
    }

    private List<LichessGame> createMockGames(String username) {
        LichessGame game1 = new LichessGame();
        game1.setUsername(username);
        game1.setGameId("game1");
        game1.setOpeningName("Italian Game");
        game1.setResult("1-0");
        game1.setPlayedAt("2024-01-15T10:30:00Z");
        game1.setPgn("1. e4 e5 2. Nf3 Nc6 3. Bc4");
        game1.setEco("C50");
        game1.setStatus("mate");

        LichessGame game2 = new LichessGame();
        game2.setUsername(username);
        game2.setGameId("game2");
        game2.setOpeningName("Sicilian Defense");
        game2.setResult("0-1");
        game2.setPlayedAt("2024-01-15T11:00:00Z");
        game2.setPgn("1. e4 c5 2. Nf3 d6");
        game2.setEco("B50");
        game2.setStatus("resign");

        return Arrays.asList(game1, game2);
    }
}