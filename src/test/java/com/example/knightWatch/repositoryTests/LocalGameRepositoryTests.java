package com.example.knightWatch.repositoryTests;



import com.example.knightWatch.model.LocalGame;
import com.example.knightWatch.repository.LocalGameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("LichessGameRepository Integration Tests")
class LocalGameRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LocalGameRepository repository;

    private LocalGame game1;
    private LocalGame game2;
    private LocalGame game3;
    private LocalGame gameOtherUser;

    @BeforeEach
    void setUp() {

        repository.deleteAll();
        entityManager.flush();
        entityManager.clear();


        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 15, 10, 0, 0);

        game1 = createTestGame(
                "testuser",
                "game001",
                "Sicilian Defense",
                "win",
                baseTime.toString(),
                "1. e4 c5",
                "B20",
                "mate"
        );

        game2 = createTestGame(
                "testuser",
                "game002",
                "Queen's Gambit",
                "loss",
                baseTime.plusHours(1).toString(),
                "1. d4 d5",
                "D06",
                "resign"
        );

        game3 = createTestGame(
                "testuser",
                "game003",
                "King's Indian Defense",
                "draw",
                baseTime.plusHours(2).toString(),
                "1. d4 Nf6",
                "E90",
                "stalemate"
        );

        gameOtherUser = createTestGame(
                "otheruser",
                "game004",
                "Ruy Lopez",
                "win",
                baseTime.plusHours(3).toString(),
                "1. e4 e5",
                "C84",
                "timeout"
        );


        game1 = entityManager.persistAndFlush(game1);
        game2 = entityManager.persistAndFlush(game2);
        game3 = entityManager.persistAndFlush(game3);
        gameOtherUser = entityManager.persistAndFlush(gameOtherUser);

        entityManager.clear();
    }

    private LocalGame createTestGame(String username, String gameId, String openingName,
                                     String result, String playedAt, String pgn,
                                     String eco, String status) {
        LocalGame game = new LocalGame();
        game.setUsername(username);
        game.setGameId(gameId);
        game.setOpeningName(openingName);
        game.setResult(result);
        game.setPlayedAt(playedAt);
        game.setPgn(pgn);
        game.setEco(eco);
        game.setStatus(status);
        return game;
    }

    @Nested
    @DisplayName("findByUsername Tests")
    class FindByUsernameTests {

        @Test
        @DisplayName("Should return all games for existing user")
        void shouldReturnAllGamesForExistingUser() {

            List<LocalGame> games = repository.findByUsername("testuser");


            assertThat(games).hasSize(3);
            assertThat(games).extracting(LocalGame::getGameId)
                    .containsExactlyInAnyOrder("game001", "game002", "game003");
            assertThat(games).allMatch(game -> "testuser".equals(game.getUsername()));
        }

        @Test
        @DisplayName("Should return empty list for non-existent user")
        void shouldReturnEmptyListForNonExistentUser() {

            List<LocalGame> games = repository.findByUsername("nonexistent");


            assertThat(games).isEmpty();
        }

        @Test
        @DisplayName("Should return games only for specified user")
        void shouldReturnGamesOnlyForSpecifiedUser() {

            List<LocalGame> testUserGames = repository.findByUsername("testuser");
            List<LocalGame> otherUserGames = repository.findByUsername("otheruser");


            assertThat(testUserGames).hasSize(3);
            assertThat(otherUserGames).hasSize(1);
            assertThat(testUserGames).noneMatch(game -> "otheruser".equals(game.getUsername()));
            assertThat(otherUserGames).noneMatch(game -> "testuser".equals(game.getUsername()));
        }

        @Test
        @DisplayName("Should handle empty string username")
        void shouldHandleEmptyStringUsername() {

            List<LocalGame> games = repository.findByUsername("");


            assertThat(games).isEmpty();
        }

        @Test
        @DisplayName("Should handle null username")
        void shouldHandleNullUsername() {

            List<LocalGame> games = repository.findByUsername(null);


            assertThat(games).isEmpty();
        }

        @Test
        @DisplayName("Should handle username with special characters")
        void shouldHandleUsernameWithSpecialCharacters() {

            LocalGame specialGame = createTestGame(
                    "user-name_123", "special001", "Italian Game", "win",
                    LocalDateTime.now().toString(), "1. e4 e5", "C50", "mate"
            );
            entityManager.persistAndFlush(specialGame);
            entityManager.clear();


            List<LocalGame> games = repository.findByUsername("user-name_123");


            assertThat(games).hasSize(1);
            assertThat(games.get(0).getGameId()).isEqualTo("special001");
        }

        @Test
        @DisplayName("Should be case sensitive for usernames")
        void shouldBeCaseSensitiveForUsernames() {

            List<LocalGame> lowerCaseGames = repository.findByUsername("testuser");
            List<LocalGame> upperCaseGames = repository.findByUsername("TESTUSER");


            assertThat(lowerCaseGames).hasSize(3);
            assertThat(upperCaseGames).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteAllByUsername Tests")
    class DeleteAllByUsernameTests {

        @Test
        @DisplayName("Should delete all games for specified user")
        void shouldDeleteAllGamesForSpecifiedUser() {

            assertThat(repository.findByUsername("testuser")).hasSize(3);
            assertThat(repository.findByUsername("otheruser")).hasSize(1);

            repository.deleteAllByUsername("testuser");
            entityManager.flush();
            entityManager.clear();

            assertThat(repository.findByUsername("testuser")).isEmpty();
            assertThat(repository.findByUsername("otheruser")).hasSize(1);
            assertThat(repository.count()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should handle deletion of non-existent user")
        void shouldHandleDeletionOfNonExistentUser() {

            long initialCount = repository.count();

            repository.deleteAllByUsername("nonexistent");
            entityManager.flush();

            assertThat(repository.count()).isEqualTo(initialCount);
        }

        @Test
        @DisplayName("Should handle empty string username for deletion")
        void shouldHandleEmptyStringUsernameForDeletion() {

            long initialCount = repository.count();

            repository.deleteAllByUsername("");
            entityManager.flush();

            assertThat(repository.count()).isEqualTo(initialCount);
        }

        @Test
        @DisplayName("Should handle null username for deletion")
        void shouldHandleNullUsernameForDeletion() {

            long initialCount = repository.count();

            repository.deleteAllByUsername(null);
            entityManager.flush();

            assertThat(repository.count()).isEqualTo(initialCount);
        }

        @Test
        @DisplayName("Should be case sensitive for deletion")
        void shouldBeCaseSensitiveForDeletion() {

            assertThat(repository.findByUsername("testuser")).hasSize(3);

            repository.deleteAllByUsername("TESTUSER");
            entityManager.flush();
            entityManager.clear();

            assertThat(repository.findByUsername("testuser")).hasSize(3);
        }
    }

    @Nested
    @DisplayName("findTop10ByUsernameOrderByPlayedAtDesc Tests")
    class FindTop10ByUsernameOrderByPlayedAtDescTests {

        @Test
        @DisplayName("Should return games in descending order by playedAt")
        void shouldReturnGamesInDescendingOrderByPlayedAt() {

            List<LocalGame> games = repository.findTop10ByUsernameOrderByPlayedAtDesc("testuser");

            assertThat(games).hasSize(3);

            assertThat(games.get(0).getGameId()).isEqualTo("game003"); // Latest
            assertThat(games.get(1).getGameId()).isEqualTo("game002");
            assertThat(games.get(2).getGameId()).isEqualTo("game001"); // Earliest

            for (int i = 0; i < games.size() - 1; i++) {
                String currentDate = games.get(i).getPlayedAt();
                String nextDate = games.get(i + 1).getPlayedAt();
                assertThat(currentDate.compareTo(nextDate)).isGreaterThanOrEqualTo(0);
            }
        }

        @Test
        @DisplayName("Should limit results to 10 games maximum")
        void shouldLimitResultsTo10GamesMaximum() {

            LocalDateTime baseTime = LocalDateTime.of(2024, 2, 1, 10, 0, 0);
            for (int i = 4; i <= 15; i++) {
                LocalGame extraGame = createTestGame(
                        "testuser",
                        "game" + String.format("%03d", i),
                        "Test Opening " + i,
                        "win",
                        baseTime.plusHours(i).toString(),
                        "1. e4 e5",
                        "C20",
                        "mate"
                );
                entityManager.persistAndFlush(extraGame);
            }
            entityManager.clear();

            List<LocalGame> games = repository.findTop10ByUsernameOrderByPlayedAtDesc("testuser");

            assertThat(games).hasSize(10);

            assertThat(games.get(0).getGameId()).isEqualTo("game015"); // Most recent
            assertThat(games.get(9).getGameId()).isEqualTo("game006"); // 10th most recent
        }

        @Test
        @DisplayName("Should return empty list for non-existent user")
        void shouldReturnEmptyListForNonExistentUser() {

            List<LocalGame> games = repository.findTop10ByUsernameOrderByPlayedAtDesc("nonexistent");

            assertThat(games).isEmpty();
        }

        @Test
        @DisplayName("Should return fewer than 10 games if user has fewer games")
        void shouldReturnFewerThan10GamesIfUserHasFewerGames() {

            List<LocalGame> games = repository.findTop10ByUsernameOrderByPlayedAtDesc("otheruser");

            assertThat(games).hasSize(1);
            assertThat(games.get(0).getGameId()).isEqualTo("game004");
        }

        @Test
        @DisplayName("Should handle games with identical timestamps")
        void shouldHandleGamesWithIdenticalTimestamps() {

            String sameTimestamp = LocalDateTime.now().toString();
            LocalGame sameTime1 = createTestGame("sameuser", "same001", "Opening1", "win", sameTimestamp, "pgn1", "E01", "mate");
            LocalGame sameTime2 = createTestGame("sameuser", "same002", "Opening2", "loss", sameTimestamp, "pgn2", "E02", "resign");

            entityManager.persistAndFlush(sameTime1);
            entityManager.persistAndFlush(sameTime2);
            entityManager.clear();

            List<LocalGame> games = repository.findTop10ByUsernameOrderByPlayedAtDesc("sameuser");

            assertThat(games).hasSize(2);
            assertThat(games).extracting(LocalGame::getGameId)
                    .containsExactlyInAnyOrder("same001", "same002");
        }

        @Test
        @DisplayName("Should only return games for specified user")
        void shouldOnlyReturnGamesForSpecifiedUser() {

            List<LocalGame> testUserGames = repository.findTop10ByUsernameOrderByPlayedAtDesc("testuser");
            List<LocalGame> otherUserGames = repository.findTop10ByUsernameOrderByPlayedAtDesc("otheruser");

            assertThat(testUserGames).hasSize(3);
            assertThat(otherUserGames).hasSize(1);
            assertThat(testUserGames).allMatch(game -> "testuser".equals(game.getUsername()));
            assertThat(otherUserGames).allMatch(game -> "otheruser".equals(game.getUsername()));
        }
    }

    @Nested
    @DisplayName("findLatestGameDateByUsername Tests")
    class FindLatestGameDateByUsernameTests {

        @Test
        @DisplayName("Should return latest playedAt date for existing user")
        void shouldReturnLatestPlayedAtDateForExistingUser() {

            String latestDate = repository.findLatestGameDateByUsername("testuser");

            assertThat(latestDate).isNotNull();

            assertThat(latestDate).isEqualTo(game3.getPlayedAt());

            List<LocalGame> allGames = repository.findByUsername("testuser");
            String expectedLatest = allGames.stream()
                    .map(LocalGame::getPlayedAt)
                    .max(String::compareTo)
                    .orElse(null);
            assertThat(latestDate).isEqualTo(expectedLatest);
        }

        @Test
        @DisplayName("Should return null for non-existent user")
        void shouldReturnNullForNonExistentUser() {

            String latestDate = repository.findLatestGameDateByUsername("nonexistent");

            assertThat(latestDate).isNull();
        }

        @Test
        @DisplayName("Should return null for empty string username")
        void shouldReturnNullForEmptyStringUsername() {

            String latestDate = repository.findLatestGameDateByUsername("");

            assertThat(latestDate).isNull();
        }

        @Test
        @DisplayName("Should return null for null username")
        void shouldReturnNullForNullUsername() {

            String latestDate = repository.findLatestGameDateByUsername(null);

            assertThat(latestDate).isNull();
        }

        @Test
        @DisplayName("Should return correct date when user has single game")
        void shouldReturnCorrectDateWhenUserHasSingleGame() {

            String latestDate = repository.findLatestGameDateByUsername("otheruser");

            assertThat(latestDate).isNotNull();
            assertThat(latestDate).isEqualTo(gameOtherUser.getPlayedAt());
        }

        @Test
        @DisplayName("Should handle user with games having identical timestamps")
        void shouldHandleUserWithGamesHavingIdenticalTimestamps() {

            String sameTimestamp = LocalDateTime.now().toString();
            LocalGame sameTime1 = createTestGame("sameuser", "same001", "Opening1", "win", sameTimestamp, "pgn1", "E01", "mate");
            LocalGame sameTime2 = createTestGame("sameuser", "same002", "Opening2", "loss", sameTimestamp, "pgn2", "E02", "resign");

            entityManager.persistAndFlush(sameTime1);
            entityManager.persistAndFlush(sameTime2);
            entityManager.clear();

            String latestDate = repository.findLatestGameDateByUsername("sameuser");

            assertThat(latestDate).isEqualTo(sameTimestamp);
        }

        @Test
        @DisplayName("Should be case sensitive for username in query")
        void shouldBeCaseSensitiveForUsernameInQuery() {

            String lowerCaseResult = repository.findLatestGameDateByUsername("testuser");
            String upperCaseResult = repository.findLatestGameDateByUsername("TESTUSER");

            assertThat(lowerCaseResult).isNotNull();
            assertThat(upperCaseResult).isNull();
        }

        @Test
        @DisplayName("Should return latest date after adding new game")
        void shouldReturnLatestDateAfterAddingNewGame() {

            String initialLatestDate = repository.findLatestGameDateByUsername("testuser");

            LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
            LocalGame newGame = createTestGame(
                    "testuser", "newest001", "Future Game", "win",
                    futureTime.toString(), "1. e4 e5", "C20", "mate"
            );
            entityManager.persistAndFlush(newGame);
            entityManager.clear();

            String newLatestDate = repository.findLatestGameDateByUsername("testuser");

            assertThat(newLatestDate).isNotEqualTo(initialLatestDate);
            assertThat(newLatestDate).isEqualTo(futureTime.toString());
        }
    }

    @Nested
    @DisplayName("Repository Integration Tests")
    class RepositoryIntegrationTests {

        @Test
        @DisplayName("Should persist and retrieve complete game data")
        void shouldPersistAndRetrieveCompleteGameData() {

            LocalGame complexGame = createTestGame(
                    "complexuser",
                    "complex001",
                    "Caro-Kann Defense: Main Line",
                    "win",
                    LocalDateTime.now().toString(),
                    "1. e4 c6 2. d4 d5 3. Nc3 dxe4 4. Nxe4 Nd7",
                    "B18",
                    "mate"
            );

            LocalGame saved = entityManager.persistAndFlush(complexGame);
            entityManager.clear();

            List<LocalGame> retrieved = repository.findByUsername("complexuser");

            assertThat(retrieved).hasSize(1);
            LocalGame game = retrieved.get(0);

            assertThat(game.getId()).isNotNull();
            assertThat(game.getUsername()).isEqualTo("complexuser");
            assertThat(game.getGameId()).isEqualTo("complex001");
            assertThat(game.getOpeningName()).isEqualTo("Caro-Kann Defense: Main Line");
            assertThat(game.getResult()).isEqualTo("win");
            assertThat(game.getPlayedAt()).isNotNull();
            assertThat(game.getPgn()).isEqualTo("1. e4 c6 2. d4 d5 3. Nc3 dxe4 4. Nxe4 Nd7");
            assertThat(game.getEco()).isEqualTo("B18");
            assertThat(game.getStatus()).isEqualTo("mate");
        }

        @Test
        @DisplayName("Should handle null values in optional fields")
        void shouldHandleNullValuesInOptionalFields() {

            LocalGame gameWithNulls = new LocalGame();
            gameWithNulls.setUsername("nulluser");
            gameWithNulls.setGameId("null001");
            gameWithNulls.setOpeningName(null);
            gameWithNulls.setResult(null);
            gameWithNulls.setPlayedAt(LocalDateTime.now().toString());
            gameWithNulls.setPgn(null);
            gameWithNulls.setEco(null);
            gameWithNulls.setStatus(null);

            entityManager.persistAndFlush(gameWithNulls);
            entityManager.clear();

            List<LocalGame> retrieved = repository.findByUsername("nulluser");

            assertThat(retrieved).hasSize(1);
            LocalGame game = retrieved.get(0);

            assertThat(game.getUsername()).isEqualTo("nulluser");
            assertThat(game.getGameId()).isEqualTo("null001");
            assertThat(game.getOpeningName()).isNull();
            assertThat(game.getResult()).isNull();
            assertThat(game.getPgn()).isNull();
            assertThat(game.getEco()).isNull();
            assertThat(game.getStatus()).isNull();
        }

        @Test
        @DisplayName("Should maintain referential integrity across operations")
        void shouldMaintainReferentialIntegrityAcrossOperations() {

            String username = "integrityuser";
            assertThat(repository.findByUsername(username)).isEmpty();

            LocalGame game1 = createTestGame(username, "int001", "Opening1", "win", LocalDateTime.now().toString(), "pgn1", "E01", "mate");
            LocalGame game2 = createTestGame(username, "int002", "Opening2", "loss", LocalDateTime.now().plusHours(1).toString(), "pgn2", "E02", "resign");

            entityManager.persistAndFlush(game1);
            entityManager.persistAndFlush(game2);
            entityManager.clear();

            assertThat(repository.findByUsername(username)).hasSize(2);
            assertThat(repository.findLatestGameDateByUsername(username)).isNotNull();
            assertThat(repository.findTop10ByUsernameOrderByPlayedAtDesc(username)).hasSize(2);

            repository.deleteAllByUsername(username);
            entityManager.flush();
            entityManager.clear();

            assertThat(repository.findByUsername(username)).isEmpty();
            assertThat(repository.findLatestGameDateByUsername(username)).isNull();
            assertThat(repository.findTop10ByUsernameOrderByPlayedAtDesc(username)).isEmpty();
        }
    }
}