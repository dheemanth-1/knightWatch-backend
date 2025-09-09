package com.example.knightWatch.controllerTests;


import com.example.knightWatch.controller.GameStatsController;
import com.example.knightWatch.dto.GameStatsByOpening;
import com.example.knightWatch.dto.OverallStats;
import com.example.knightWatch.service.GameStatsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameStatsController.class)
class GameStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameStatsService gameStatsService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<GameStatsByOpening> sampleStatsByOpening;
    private OverallStats sampleOverallStats;

    @BeforeEach
    void setup() {
        sampleStatsByOpening = createSampleStatsByOpening();
        sampleOverallStats = createSampleOverallStats();
    }



    @Test
    void getStatsByOpening_returnsEmptyListWhenNoStats() throws Exception {

        String userId = "newuser";
        when(gameStatsService.calculateStatsByOpening(userId)).thenReturn(List.of());


        mockMvc.perform(get("/api/stats/openings/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(jsonPath("$", is(empty())));

        verify(gameStatsService).calculateStatsByOpening(userId);
    }


    @Test
    void getStatsByOpening_validatesPathVariable() throws Exception {

        String userId = "user-with-special-chars";
        when(gameStatsService.calculateStatsByOpening(userId)).thenReturn(List.of());


        mockMvc.perform(get("/api/stats/openings/{userId}", userId))
                .andExpect(status().isOk());

        verify(gameStatsService).calculateStatsByOpening(userId);
    }

    @Test
    void getOverallStats_returnsOverallStats() throws Exception {


        String userId = "testuser";
        when(gameStatsService.calculateOverallStats(userId)).thenReturn(sampleOverallStats);



        mockMvc.perform(get("/api/stats/overall/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lossRate", is(30.0)))
                .andExpect(jsonPath("$.winRate", is(60.0)))
                .andExpect(jsonPath("$.drawRate", is(10.0)));


        verify(gameStatsService).calculateOverallStats(userId);
        verifyNoMoreInteractions(gameStatsService);
    }

    @Test
    void getOverallStats_handlesServiceException() throws Exception {

        String userId = "erroruser";
        when(gameStatsService.calculateOverallStats(userId))
                .thenThrow(new RuntimeException("Service error"));


        mockMvc.perform(get("/api/stats/overall/{userId}", userId))
                .andExpect(status().isInternalServerError());

        verify(gameStatsService).calculateOverallStats(userId);
    }

    @Test
    void getOverallStats_validatesPathVariable() throws Exception {

        String userId = "user_with_underscores";
        when(gameStatsService.calculateOverallStats(userId)).thenReturn(sampleOverallStats);


        mockMvc.perform(get("/api/stats/overall/{userId}", userId))
                .andExpect(status().isOk());

        verify(gameStatsService).calculateOverallStats(userId);
    }

    @Test
    void getStatsByOpening_verifyCorrectEndpoint() throws Exception {

        String userId = "testuser";
        when(gameStatsService.calculateStatsByOpening(userId)).thenReturn(sampleStatsByOpening);

        mockMvc.perform(get("/api/stats/openings/{userId}", userId))
                .andExpect(status().isOk());


        verify(gameStatsService).calculateStatsByOpening(userId);
    }

    @Test
    void getOverallStats_verifyCorrectEndpoint() throws Exception {
        String userId = "testuser";
        when(gameStatsService.calculateOverallStats(userId)).thenReturn(sampleOverallStats);

        mockMvc.perform(get("/api/stats/overall/{userId}", userId))
                .andExpect(status().isOk());


        verify(gameStatsService).calculateOverallStats(userId);
    }

    @Test
    void getStatsByOpening_handlesEmptyUserId() throws Exception {

        mockMvc.perform(get("/api/stats/openings/"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOverallStats_handlesEmptyUserId() throws Exception {
        // Test with empty userId
        mockMvc.perform(get("/api/stats/overall/"))
                .andExpect(status().isNotFound());
    }

    // Test Content-Type and Accept headers
    @Test
    void getStatsByOpening_returnsJsonContentType() throws Exception {
        String userId = "testuser";
        when(gameStatsService.calculateStatsByOpening(userId)).thenReturn(sampleStatsByOpening);

        mockMvc.perform(get("/api/stats/openings/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getOverallStats_returnsJsonContentType() throws Exception {
        String userId = "testuser";
        when(gameStatsService.calculateOverallStats(userId)).thenReturn(sampleOverallStats);

        mockMvc.perform(get("/api/stats/overall/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // methods to create sample data
    private List<GameStatsByOpening> createSampleStatsByOpening() {
        return Arrays.asList(
                createGameStatsByOpening("Sicilian Defense", 10, 6, 3, 1, 65.0),
                createGameStatsByOpening("French Defense", 5, 2, 2, 1, 50.0),
                createGameStatsByOpening("King's Pawn Game", 8, 5, 3, 0, 62.5)
        );
    }

    private GameStatsByOpening createGameStatsByOpening(String openingName, int gamesPlayed,
                                                        int wins, int losses, int draws, double winRate) {
        GameStatsByOpening stats = new GameStatsByOpening();
        stats.setOpeningName(openingName);
        stats.setNumWins(wins);
        stats.setNumLosses(losses);
        stats.setNumDraws(draws);
        stats.setWinRate(winRate);
        return stats;
    }

    private OverallStats createSampleOverallStats() {
        OverallStats stats = new OverallStats();
        stats.setLossRate(30.0);
        stats.setDrawRate(10.0);
        stats.setWinRate(60.0);
        return stats;
    }
}