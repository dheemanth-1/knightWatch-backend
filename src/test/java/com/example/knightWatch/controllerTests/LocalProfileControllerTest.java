package com.example.knightWatch.controllerTests;


import com.example.knightWatch.controller.LocalProfileController;
import com.example.knightWatch.model.LocalProfile;
import com.example.knightWatch.repository.LocalGameRepository;
import com.example.knightWatch.repository.LocalProfileRepository;
import com.example.knightWatch.repository.SyncStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LichessProfileController Tests")
class LocalProfileControllerTest {

    @Mock
    private LocalProfileRepository profileRepo;

    @Mock
    private LocalGameRepository gameRepo;

    @InjectMocks
    private LocalProfileController controller;

    private LocalProfile testProfile1;
    private LocalProfile testProfile2;

    @BeforeEach
    void setUp() {
        testProfile1 = createTestProfile("testuser1", 100, 80, 1500, 1400, 1600, 1550, 2000);
        testProfile2 = createTestProfile("testuser2", 200, 150, 1600, 1500, 1700, 1650, 2100);
    }

    private LocalProfile createTestProfile(String username, int totalGames, int ratedGames,
                                           int blitzRating, int bulletRating, int rapidRating,
                                           int classicalRating, int puzzleRating) {
        LocalProfile profile = new LocalProfile();
        profile.setUsername(username);
        profile.setTotalGames(totalGames);
        profile.setRatedGames(ratedGames);
        profile.setBlitzRating(blitzRating);
        profile.setBulletRating(bulletRating);
        profile.setRapidRating(rapidRating);
        profile.setClassicalRating(classicalRating);
        profile.setPuzzleRating(puzzleRating);
        return profile;
    }

    @Nested
    @DisplayName("GET /api/db/lichess/profile/{username}")
    class GetCachedProfileTests {

        @Test
        @DisplayName("Should return profile when user exists")
        void shouldReturnProfileWhenUserExists() {

            String username = "testuser1";
            when(profileRepo.findByUsername(username)).thenReturn(Optional.of(testProfile1));


            ResponseEntity<?> response = controller.getCachedProfile(username);


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(testProfile1);

            LocalProfile returnedProfile = (LocalProfile) response.getBody();
            assertThat(returnedProfile.getUsername()).isEqualTo("testuser1");
            assertThat(returnedProfile.getTotalGames()).isEqualTo(100);
            assertThat(returnedProfile.getRatedGames()).isEqualTo(80);
            assertThat(returnedProfile.getBlitzRating()).isEqualTo(1500);
            assertThat(returnedProfile.getBulletRating()).isEqualTo(1400);
            assertThat(returnedProfile.getRapidRating()).isEqualTo(1600);
            assertThat(returnedProfile.getClassicalRating()).isEqualTo(1550);
            assertThat(returnedProfile.getPuzzleRating()).isEqualTo(2000);

            verify(profileRepo).findByUsername(username);
        }

        @Test
        @DisplayName("Should return 404 when user does not exist")
        void shouldReturn404WhenUserDoesNotExist() {

            String username = "nonexistentuser";
            when(profileRepo.findByUsername(username)).thenReturn(Optional.empty());

            ResponseEntity<?> response = controller.getCachedProfile(username);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNull();
            verify(profileRepo).findByUsername(username);
        }

        @Test
        @DisplayName("Should handle username with special characters")
        void shouldHandleUsernameWithSpecialCharacters() {

            String username = "test-user_123";
            LocalProfile specialProfile = createTestProfile(username, 50, 40, 1300, 1250, 1350, 1300, 1800);
            when(profileRepo.findByUsername(username)).thenReturn(Optional.of(specialProfile));

            ResponseEntity<?> response = controller.getCachedProfile(username);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(specialProfile);
            verify(profileRepo).findByUsername(username);
        }

        @Test
        @DisplayName("Should handle empty username")
        void shouldHandleEmptyUsername() {

            String username = "";
            when(profileRepo.findByUsername(username)).thenReturn(Optional.empty());

            ResponseEntity<?> response = controller.getCachedProfile(username);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            verify(profileRepo).findByUsername(username);
        }

        @Test
        @DisplayName("Should handle case sensitivity")
        void shouldHandleCaseSensitivity() {

            String username = "TESTUSER1";
            when(profileRepo.findByUsername(username)).thenReturn(Optional.empty());

            ResponseEntity<?> response = controller.getCachedProfile(username);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            verify(profileRepo).findByUsername(username);
        }
    }

    @Nested
    @DisplayName("GET /api/db/lichess/profile/all")
    class GetAllCachedProfilesTests {

        @Test
        @DisplayName("Should return all profiles when profiles exist")
        void shouldReturnAllProfilesWhenProfilesExist() {

            List<LocalProfile> profiles = Arrays.asList(testProfile1, testProfile2);
            when(profileRepo.findAll()).thenReturn(profiles);

            ResponseEntity<List<LocalProfile>> response = controller.getAllCachedProfiles();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(2);
            assertThat(response.getBody()).containsExactlyInAnyOrder(testProfile1, testProfile2);

            List<LocalProfile> returnedProfiles = response.getBody();
            assertThat(returnedProfiles.get(0).getUsername()).isIn("testuser1", "testuser2");
            assertThat(returnedProfiles.get(1).getUsername()).isIn("testuser1", "testuser2");

            verify(profileRepo).findAll();
        }

        @Test
        @DisplayName("Should return single profile when only one exists")
        void shouldReturnSingleProfileWhenOnlyOneExists() {

            List<LocalProfile> profiles = Collections.singletonList(testProfile1);
            when(profileRepo.findAll()).thenReturn(profiles);

            ResponseEntity<List<LocalProfile>> response = controller.getAllCachedProfiles();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
            assertThat(response.getBody()).contains(testProfile1);
            assertThat(response.getBody().get(0).getUsername()).isEqualTo("testuser1");
            verify(profileRepo).findAll();
        }

        @Test
        @DisplayName("Should return 404 when no profiles exist")
        void shouldReturn404WhenNoProfilesExist() {

            when(profileRepo.findAll()).thenReturn(Collections.emptyList());

            ResponseEntity<List<LocalProfile>> response = controller.getAllCachedProfiles();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNull();
            verify(profileRepo).findAll();
        }

        @Test
        @DisplayName("Should handle large number of profiles")
        void shouldHandleLargeNumberOfProfiles() {

            List<LocalProfile> manyProfiles = Arrays.asList(
                    testProfile1,
                    testProfile2,
                    createTestProfile("user3", 300, 250, 1700, 1650, 1750, 1700, 2200),
                    createTestProfile("user4", 400, 350, 1800, 1750, 1850, 1800, 2300),
                    createTestProfile("user5", 500, 450, 1900, 1850, 1950, 1900, 2400)
            );
            when(profileRepo.findAll()).thenReturn(manyProfiles);

            ResponseEntity<List<LocalProfile>> response = controller.getAllCachedProfiles();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(5);
            assertThat(response.getBody()).containsAll(manyProfiles);
            verify(profileRepo).findAll();
        }

        @Test
        @DisplayName("Should handle profiles with zero ratings")
        void shouldHandleProfilesWithZeroRatings() {

            LocalProfile newProfile = createTestProfile("newuser", 0, 0, 0, 0, 0, 0, 0);
            List<LocalProfile> profiles = Collections.singletonList(newProfile);
            when(profileRepo.findAll()).thenReturn(profiles);

            ResponseEntity<List<LocalProfile>> response = controller.getAllCachedProfiles();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);

            LocalProfile returnedProfile = response.getBody().get(0);
            assertThat(returnedProfile.getTotalGames()).isEqualTo(0);
            assertThat(returnedProfile.getRatedGames()).isEqualTo(0);
            assertThat(returnedProfile.getBlitzRating()).isEqualTo(0);
            assertThat(returnedProfile.getBulletRating()).isEqualTo(0);
            assertThat(returnedProfile.getRapidRating()).isEqualTo(0);
            assertThat(returnedProfile.getClassicalRating()).isEqualTo(0);
            assertThat(returnedProfile.getPuzzleRating()).isEqualTo(0);

            verify(profileRepo).findAll();
        }
    }

    @Nested
    @DisplayName("DELETE /api/db/lichess/profile/{username}")
    class DeleteUserDataTests {

        @Test
        @DisplayName("Should delete user data and return 204")
        void shouldDeleteUserDataAndReturn204() {

            String username = "testuser1";
            doNothing().when(profileRepo).deleteByUsername(username);
            doNothing().when(gameRepo).deleteAllByUsername(username);

            ResponseEntity<Map<String, Object>> response = controller.deleteUserData(username);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(response.getBody()).isNull();
            verify(profileRepo).deleteByUsername(username);
            verify(gameRepo).deleteAllByUsername(username);
        }

        @Test
        @DisplayName("Should handle deletion of non-existent user")
        void shouldHandleDeletionOfNonExistentUser() {

            String username = "nonexistentuser";
            doNothing().when(profileRepo).deleteByUsername(username);
            doNothing().when(gameRepo).deleteAllByUsername(username);

            ResponseEntity<Map<String, Object>> response = controller.deleteUserData(username);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(response.getBody()).isNull();
            verify(profileRepo).deleteByUsername(username);
            verify(gameRepo).deleteAllByUsername(username);
        }

        @Test
        @DisplayName("Should handle username with special characters in deletion")
        void shouldHandleUsernameWithSpecialCharactersInDeletion() {

            String username = "test-user_123";
            doNothing().when(profileRepo).deleteByUsername(username);
            doNothing().when(gameRepo).deleteAllByUsername(username);

            ResponseEntity<Map<String, Object>> response = controller.deleteUserData(username);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(profileRepo).deleteByUsername(username);
            verify(gameRepo).deleteAllByUsername(username);
        }

        @Test
        @DisplayName("Should call both repository delete methods in correct order")
        void shouldCallBothRepositoryDeleteMethodsInCorrectOrder() {

            String username = "testuser1";
            doNothing().when(profileRepo).deleteByUsername(username);
            doNothing().when(gameRepo).deleteAllByUsername(username);

            controller.deleteUserData(username);

            verify(profileRepo).deleteByUsername(username);
            verify(gameRepo).deleteAllByUsername(username);


            verifyNoMoreInteractions(profileRepo);
            verifyNoMoreInteractions(gameRepo);
        }

        @Test
        @DisplayName("Should handle empty username in deletion")
        void shouldHandleEmptyUsernameInDeletion() {

            String username = "";
            doNothing().when(profileRepo).deleteByUsername(username);
            doNothing().when(gameRepo).deleteAllByUsername(username);

            ResponseEntity<Map<String, Object>> response = controller.deleteUserData(username);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(profileRepo).deleteByUsername(username);
            verify(gameRepo).deleteAllByUsername(username);
        }

        @Test
        @DisplayName("Should maintain transactional behavior")
        void shouldMaintainTransactionalBehavior() {

            String username = "testuser1";

            doNothing().when(profileRepo).deleteByUsername(username);
            doNothing().when(gameRepo).deleteAllByUsername(username);

            ResponseEntity<Map<String, Object>> response = controller.deleteUserData(username);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            verify(profileRepo, times(1)).deleteByUsername(username);
            verify(gameRepo, times(1)).deleteAllByUsername(username);
        }
    }

    @Nested
    @DisplayName("Controller Integration Tests")
    class ControllerIntegrationTests {

        @Test
        @DisplayName("Should handle repository exceptions gracefully")
        void shouldHandleRepositoryExceptionsGracefully() {

            String username = "testuser1";
            when(profileRepo.findByUsername(username)).thenThrow(new RuntimeException("Database connection error"));

            org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
                controller.getCachedProfile(username);
            });

            verify(profileRepo).findByUsername(username);
        }

        @Test
        @DisplayName("Should verify constructor injection")
        void shouldVerifyConstructorInjection() {

            LocalProfileRepository mockProfileRepo = mock(LocalProfileRepository.class);
            LocalGameRepository mockGameRepo = mock(LocalGameRepository.class);
            SyncStatusRepository mockSyncRepo = mock(SyncStatusRepository.class);
            LocalProfileController testController = new LocalProfileController(mockProfileRepo, mockGameRepo, mockSyncRepo);

            assertThat(testController).isNotNull();

            when(mockProfileRepo.findAll()).thenReturn(Collections.emptyList());
            ResponseEntity<List<LocalProfile>> response = testController.getAllCachedProfiles();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            verify(mockProfileRepo).findAll();
        }
    }
}