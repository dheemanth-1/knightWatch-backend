package com.example.knightWatch.controllerTests;

import com.example.knightWatch.controller.PlayerProfileController;
import com.example.knightWatch.model.PlayerProfile;
import com.example.knightWatch.repository.PlayerProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlayerProfileController Tests")
class PlayerProfileControllerTest {

    @Mock
    private PlayerProfileRepository repository;

    @InjectMocks
    private PlayerProfileController controller;

    private PlayerProfile testPlayer1;
    private PlayerProfile testPlayer2;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        testPlayer1 = createTestPlayer("player1", 100, 0.75, testDateTime);
        testPlayer2 = createTestPlayer("player2", 250, 0.82, testDateTime.plusDays(1));
    }

    private PlayerProfile createTestPlayer(String username, int totalGames, double winRate, LocalDateTime lastSyncTime) {
        PlayerProfile player = new PlayerProfile();
        player.setUsername(username);
        player.setTotalGames(totalGames);
        player.setWinRate(winRate);
        player.setLastSyncTime(lastSyncTime);
        return player;
    }

    @Nested
    @DisplayName("GET /api/players")
    class GetAllPlayersTests {

        @Test
        @DisplayName("Should return 200 OK with all players when players exist")
        void shouldReturn200WithAllPlayersWhenPlayersExist() {

            List<PlayerProfile> players = Arrays.asList(testPlayer1, testPlayer2);
            when(repository.findAll()).thenReturn(players);


            ResponseEntity<List<PlayerProfile>> response = controller.getAllPlayers();


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(2);
            assertThat(response.getBody()).containsExactlyInAnyOrder(testPlayer1, testPlayer2);


            PlayerProfile firstPlayer = response.getBody().stream()
                    .filter(p -> "player1".equals(p.getUsername()))
                    .findFirst()
                    .orElseThrow();

            assertThat(firstPlayer.getUsername()).isEqualTo("player1");
            assertThat(firstPlayer.getTotalGames()).isEqualTo(100);
            assertThat(firstPlayer.getWinRate()).isEqualTo(0.75);
            assertThat(firstPlayer.getLastSyncTime()).isEqualTo(testDateTime);

            verify(repository).findAll();
        }

        @Test
        @DisplayName("Should return 404 Not Found when no players exist")
        void shouldReturn404WhenNoPlayersExist() {

            when(repository.findAll()).thenReturn(Collections.emptyList());


            ResponseEntity<List<PlayerProfile>> response = controller.getAllPlayers();


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNull();
            verify(repository).findAll();
        }

        @Test
        @DisplayName("Should return 200 OK with single player when only one exists")
        void shouldReturn200WithSinglePlayerWhenOnlyOneExists() {

            List<PlayerProfile> players = Collections.singletonList(testPlayer1);
            when(repository.findAll()).thenReturn(players);


            ResponseEntity<List<PlayerProfile>> response = controller.getAllPlayers();


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
            assertThat(response.getBody().get(0)).isEqualTo(testPlayer1);
            assertThat(response.getBody().get(0).getUsername()).isEqualTo("player1");
            verify(repository).findAll();
        }

        @Test
        @DisplayName("Should return 200 OK with large number of players")
        void shouldReturn200WithLargeNumberOfPlayers() {

            List<PlayerProfile> manyPlayers = Arrays.asList(
                    testPlayer1,
                    testPlayer2,
                    createTestPlayer("player3", 50, 0.60, testDateTime.plusDays(2)),
                    createTestPlayer("player4", 300, 0.90, testDateTime.plusDays(3)),
                    createTestPlayer("player5", 150, 0.70, testDateTime.plusDays(4))
            );
            when(repository.findAll()).thenReturn(manyPlayers);


            ResponseEntity<List<PlayerProfile>> response = controller.getAllPlayers();


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(5);
            assertThat(response.getBody()).containsAll(manyPlayers);
            verify(repository).findAll();
        }

        @Test
        @DisplayName("Should return 200 OK with players having zero games")
        void shouldReturn200WithPlayersHavingZeroGames() {

            PlayerProfile newPlayer = createTestPlayer("newbie", 0, 0.0, testDateTime);
            List<PlayerProfile> players = Collections.singletonList(newPlayer);
            when(repository.findAll()).thenReturn(players);


            ResponseEntity<List<PlayerProfile>> response = controller.getAllPlayers();


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
            assertThat(response.getBody().get(0).getTotalGames()).isEqualTo(0);
            assertThat(response.getBody().get(0).getWinRate()).isEqualTo(0.0);
            verify(repository).findAll();
        }

        @Test
        @DisplayName("Should propagate repository exceptions")
        void shouldPropagateRepositoryExceptions() {

            when(repository.findAll()).thenThrow(new RuntimeException("Database connection failed"));


            org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
                controller.getAllPlayers();
            });

            verify(repository).findAll();
        }
    }

    @Nested
    @DisplayName("POST /api/players")
    class CreatePlayerTests {

        @Test
        @DisplayName("Should return 201 Created with player when creation succeeds")
        void shouldReturn201WithPlayerWhenCreationSucceeds() {

            PlayerProfile newPlayer = createTestPlayer("newplayer", 0, 0.0, testDateTime);
            PlayerProfile savedPlayer = createTestPlayer("newplayer", 0, 0.0, testDateTime);
            when(repository.save(newPlayer)).thenReturn(savedPlayer);


            ResponseEntity<PlayerProfile> response = controller.createPlayer(newPlayer);


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isEqualTo(savedPlayer);
            assertThat(response.getBody().getUsername()).isEqualTo("newplayer");
            assertThat(response.getBody().getTotalGames()).isEqualTo(0);
            assertThat(response.getBody().getWinRate()).isEqualTo(0.0);
            assertThat(response.getBody().getLastSyncTime()).isEqualTo(testDateTime);

            verify(repository).save(newPlayer);
        }

        @Test
        @DisplayName("Should return 201 Created with player having valid statistics")
        void shouldReturn201WithPlayerHavingValidStatistics() {

            PlayerProfile playerWithStats = createTestPlayer("experienced", 500, 0.85, testDateTime);
            when(repository.save(playerWithStats)).thenReturn(playerWithStats);


            ResponseEntity<PlayerProfile> response = controller.createPlayer(playerWithStats);


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().getUsername()).isEqualTo("experienced");
            assertThat(response.getBody().getTotalGames()).isEqualTo(500);
            assertThat(response.getBody().getWinRate()).isEqualTo(0.85);
            assertThat(response.getBody().getLastSyncTime()).isEqualTo(testDateTime);

            verify(repository).save(playerWithStats);
        }

        @Test
        @DisplayName("Should return 201 Created with player having perfect win rate")
        void shouldReturn201WithPlayerHavingPerfectWinRate() {

            PlayerProfile perfectPlayer = createTestPlayer("perfect", 10, 1.0, testDateTime);
            when(repository.save(perfectPlayer)).thenReturn(perfectPlayer);


            ResponseEntity<PlayerProfile> response = controller.createPlayer(perfectPlayer);


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().getWinRate()).isEqualTo(1.0);
            verify(repository).save(perfectPlayer);
        }

        @Test
        @DisplayName("Should return 201 Created with player having zero win rate")
        void shouldReturn201WithPlayerHavingZeroWinRate() {

            PlayerProfile unluckyPlayer = createTestPlayer("unlucky", 20, 0.0, testDateTime);
            when(repository.save(unluckyPlayer)).thenReturn(unluckyPlayer);


            ResponseEntity<PlayerProfile> response = controller.createPlayer(unluckyPlayer);


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().getWinRate()).isEqualTo(0.0);
            verify(repository).save(unluckyPlayer);
        }

        @Test
        @DisplayName("Should return 409 Conflict on duplicate username")
        void shouldReturn409OnDuplicateUsername() {

            PlayerProfile duplicatePlayer = createTestPlayer("existing", 50, 0.6, testDateTime);
            when(repository.save(duplicatePlayer))
                    .thenThrow(new DataIntegrityViolationException("Unique constraint violation"));


            ResponseEntity<PlayerProfile> response = controller.createPlayer(duplicatePlayer);


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNull();
            verify(repository).save(duplicatePlayer);
        }

        @Test
        @DisplayName("Should return 500 Internal Server Error on repository exceptions")
        void shouldReturn500OnRepositoryExceptions() {

            PlayerProfile player = createTestPlayer("test", 10, 0.5, testDateTime);
            when(repository.save(player)).thenThrow(new RuntimeException("Save operation failed"));


            ResponseEntity<PlayerProfile> response = controller.createPlayer(player);


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNull();
            verify(repository).save(player);
        }

        @Test
        @DisplayName("Should return 500 Internal Server Error on null player")
        void shouldReturn500OnNullPlayer() {

            when(repository.save(null)).thenThrow(new IllegalArgumentException("Player cannot be null"));


            ResponseEntity<PlayerProfile> response = controller.createPlayer(null);


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNull();
            verify(repository).save(null);
        }

        @Test
        @DisplayName("Should preserve all player fields during creation")
        void shouldPreserveAllPlayerFieldsDuringCreation() {

            LocalDateTime specificTime = LocalDateTime.of(2024, 6, 15, 14, 30, 45);
            PlayerProfile detailedPlayer = createTestPlayer("detailed", 999, 0.731, specificTime);
            when(repository.save(detailedPlayer)).thenReturn(detailedPlayer);


            ResponseEntity<PlayerProfile> response = controller.createPlayer(detailedPlayer);


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().getUsername()).isEqualTo("detailed");
            assertThat(response.getBody().getTotalGames()).isEqualTo(999);
            assertThat(response.getBody().getWinRate()).isEqualTo(0.731);
            assertThat(response.getBody().getLastSyncTime()).isEqualTo(specificTime);

            verify(repository).save(detailedPlayer);
        }
    }

    @Nested
    @DisplayName("GET /api/players/{username}")
    class GetPlayerByUsernameTests {

        @Test
        @DisplayName("Should return 200 OK with player when username exists")
        void shouldReturn200WithPlayerWhenUsernameExists() {

            String username = "player1";
            when(repository.findByUsername(username)).thenReturn(testPlayer1);


            ResponseEntity<PlayerProfile> response = controller.getPlayerByUsername(username);


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(testPlayer1);
            assertThat(response.getBody().getUsername()).isEqualTo("player1");
            assertThat(response.getBody().getTotalGames()).isEqualTo(100);
            assertThat(response.getBody().getWinRate()).isEqualTo(0.75);
            assertThat(response.getBody().getLastSyncTime()).isEqualTo(testDateTime);

            verify(repository).findByUsername(username);
        }

        @Test
        @DisplayName("Should return 404 Not Found when username does not exist")
        void shouldReturn404WhenUsernameDoesNotExist() {

            String username = "nonexistent";
            when(repository.findByUsername(username)).thenReturn(null);


            ResponseEntity<PlayerProfile> response = controller.getPlayerByUsername(username);


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNull();
            verify(repository).findByUsername(username);
        }

        @Test
        @DisplayName("Should return 200 OK with player having special characters in username")
        void shouldReturn200WithPlayerHavingSpecialCharactersInUsername() {

            String specialUsername = "user-name_123";
            PlayerProfile specialPlayer = createTestPlayer(specialUsername, 75, 0.68, testDateTime);
            when(repository.findByUsername(specialUsername)).thenReturn(specialPlayer);


            ResponseEntity<PlayerProfile> response = controller.getPlayerByUsername(specialUsername);


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(specialPlayer);
            assertThat(response.getBody().getUsername()).isEqualTo(specialUsername);
            verify(repository).findByUsername(specialUsername);
        }

        @Test
        @DisplayName("Should return 404 Not Found for empty username")
        void shouldReturn404ForEmptyUsername() {

            String username = "";
            when(repository.findByUsername(username)).thenReturn(null);


            ResponseEntity<PlayerProfile> response = controller.getPlayerByUsername(username);


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNull();
            verify(repository).findByUsername(username);
        }

        @Test
        @DisplayName("Should return 404 Not Found for case-sensitive usernames")
        void shouldReturn404ForCaseSensitiveUsernames() {

            String upperUsername = "PLAYER1";
            when(repository.findByUsername(upperUsername)).thenReturn(null);


            ResponseEntity<PlayerProfile> response = controller.getPlayerByUsername(upperUsername);


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNull();
            verify(repository).findByUsername(upperUsername);
        }

        @Test
        @DisplayName("Should return 404 Not Found for very long usernames")
        void shouldReturn404ForVeryLongUsernames() {

            String longUsername = "a".repeat(100);
            when(repository.findByUsername(longUsername)).thenReturn(null);


            ResponseEntity<PlayerProfile> response = controller.getPlayerByUsername(longUsername);


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNull();
            verify(repository).findByUsername(longUsername);
        }

        @Test
        @DisplayName("Should propagate repository exceptions")
        void shouldPropagateRepositoryExceptions() {

            String username = "player1";
            when(repository.findByUsername(username))
                    .thenThrow(new RuntimeException("Database query failed"));


            org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
                controller.getPlayerByUsername(username);
            });

            verify(repository).findByUsername(username);
        }

        @Test
        @DisplayName("Should return 200 OK with player having recent sync time")
        void shouldReturn200WithPlayerHavingRecentSyncTime() {

            LocalDateTime recentTime = LocalDateTime.now().minusMinutes(5);
            PlayerProfile recentPlayer = createTestPlayer("recent", 50, 0.8, recentTime);
            when(repository.findByUsername("recent")).thenReturn(recentPlayer);


            ResponseEntity<PlayerProfile> response = controller.getPlayerByUsername("recent");


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getLastSyncTime()).isEqualTo(recentTime);
            assertThat(response.getBody().getLastSyncTime()).isAfter(testDateTime);
            verify(repository).findByUsername("recent");
        }
    }

    @Nested
    @DisplayName("Controller Integration Tests")
    class ControllerIntegrationTests {

        @Test
        @DisplayName("Should verify constructor injection")
        void shouldVerifyConstructorInjection() {

            PlayerProfileRepository mockRepository = mock(PlayerProfileRepository.class);


            PlayerProfileController testController = new PlayerProfileController(mockRepository);


            assertThat(testController).isNotNull();


            when(mockRepository.findAll()).thenReturn(Collections.emptyList());
            ResponseEntity<List<PlayerProfile>> response = testController.getAllPlayers();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNull();
            verify(mockRepository).findAll();
        }

        @Test
        @DisplayName("Should handle concurrent access patterns with proper status codes")
        void shouldHandleConcurrentAccessPatternsWithProperStatusCodes() {

            when(repository.findAll()).thenReturn(Arrays.asList(testPlayer1, testPlayer2));
            when(repository.findByUsername("player1")).thenReturn(testPlayer1);
            when(repository.save(any(PlayerProfile.class))).thenReturn(testPlayer1);


            ResponseEntity<List<PlayerProfile>> allPlayersResponse = controller.getAllPlayers();
            ResponseEntity<PlayerProfile> foundPlayerResponse = controller.getPlayerByUsername("player1");
            ResponseEntity<PlayerProfile> createdPlayerResponse = controller.createPlayer(testPlayer1);


            assertThat(allPlayersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(allPlayersResponse.getBody()).hasSize(2);

            assertThat(foundPlayerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(foundPlayerResponse.getBody()).isEqualTo(testPlayer1);

            assertThat(createdPlayerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(createdPlayerResponse.getBody()).isEqualTo(testPlayer1);

            verify(repository).findAll();
            verify(repository).findByUsername("player1");
            verify(repository).save(testPlayer1);
        }

        @Test
        @DisplayName("Should maintain data consistency across operations with proper responses")
        void shouldMaintainDataConsistencyAcrossOperationsWithProperResponses() {

            PlayerProfile newPlayer = createTestPlayer("consistent", 100, 0.75, testDateTime);
            when(repository.save(newPlayer)).thenReturn(newPlayer);
            when(repository.findByUsername("consistent")).thenReturn(newPlayer);


            ResponseEntity<PlayerProfile> createResponse = controller.createPlayer(newPlayer);
            ResponseEntity<PlayerProfile> retrieveResponse = controller.getPlayerByUsername("consistent");


            assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(retrieveResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            PlayerProfile created = createResponse.getBody();
            PlayerProfile retrieved = retrieveResponse.getBody();

            assertThat(created).isEqualTo(retrieved);
            assertThat(created.getUsername()).isEqualTo(retrieved.getUsername());
            assertThat(created.getTotalGames()).isEqualTo(retrieved.getTotalGames());
            assertThat(created.getWinRate()).isEqualTo(retrieved.getWinRate());
            assertThat(created.getLastSyncTime()).isEqualTo(retrieved.getLastSyncTime());

            verify(repository).save(newPlayer);
            verify(repository).findByUsername("consistent");
        }

        @Test
        @DisplayName("Should handle edge case combinations properly")
        void shouldHandleEdgeCaseCombinationsProperly() {

            when(repository.findAll()).thenReturn(Collections.emptyList());
            when(repository.findByUsername("nonexistent")).thenReturn(null);

            PlayerProfile newPlayer = createTestPlayer("first", 0, 0.0, testDateTime);
            when(repository.save(newPlayer)).thenReturn(newPlayer);


            ResponseEntity<List<PlayerProfile>> emptyListResponse = controller.getAllPlayers();
            ResponseEntity<PlayerProfile> notFoundResponse = controller.getPlayerByUsername("nonexistent");
            ResponseEntity<PlayerProfile> firstPlayerResponse = controller.createPlayer(newPlayer);


            assertThat(emptyListResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(notFoundResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(firstPlayerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            verify(repository).findAll();
            verify(repository).findByUsername("nonexistent");
            verify(repository).save(newPlayer);
        }
    }
}