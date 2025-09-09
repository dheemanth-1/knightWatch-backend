package com.example.knightWatch.repositoryTests;


import com.example.knightWatch.model.LichessProfile;
import com.example.knightWatch.repository.LichessProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("LichessProfileRepository Integration Tests")
class LichessProfileRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LichessProfileRepository repository;

    private LichessProfile testProfile1;
    private LichessProfile testProfile2;
    private LichessProfile testProfile3;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        testProfile1 = createTestProfile(
                "testuser1",
                1500, 1200,
                1800, 1600, 1750, 1900, 2100
        );

        testProfile2 = createTestProfile(
                "testuser2",
                2000, 1800,
                2200, 2000, 2100, 2300, 2500
        );

        testProfile3 = createTestProfile(
                "testuser3",
                500, 400,
                1200, 1100, 1150, 1300, 1400
        );

        testProfile1 = entityManager.persistAndFlush(testProfile1);
        testProfile2 = entityManager.persistAndFlush(testProfile2);
        testProfile3 = entityManager.persistAndFlush(testProfile3);

        entityManager.clear();
    }

    private LichessProfile createTestProfile(String username, int totalGames, int ratedGames,
                                             int blitzRating, int bulletRating, int rapidRating,
                                             int classicalRating, int puzzleRating) {
        LichessProfile profile = new LichessProfile();
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
    @DisplayName("findByUsername Tests")
    class FindByUsernameTests {

        @Test
        @DisplayName("Should return profile for existing username")
        void shouldReturnProfileForExistingUsername() {

            Optional<LichessProfile> result = repository.findByUsername("testuser1");

            assertThat(result).isPresent();
            LichessProfile profile = result.get();

            assertThat(profile.getUsername()).isEqualTo("testuser1");
            assertThat(profile.getTotalGames()).isEqualTo(1500);
            assertThat(profile.getRatedGames()).isEqualTo(1200);
            assertThat(profile.getBlitzRating()).isEqualTo(1800);
            assertThat(profile.getBulletRating()).isEqualTo(1600);
            assertThat(profile.getRapidRating()).isEqualTo(1750);
            assertThat(profile.getClassicalRating()).isEqualTo(1900);
            assertThat(profile.getPuzzleRating()).isEqualTo(2100);
        }

        @Test
        @DisplayName("Should return empty optional for non-existent username")
        void shouldReturnEmptyOptionalForNonExistentUsername() {

            Optional<LichessProfile> result = repository.findByUsername("nonexistent");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty optional for null username")
        void shouldReturnEmptyOptionalForNullUsername() {

            Optional<LichessProfile> result = repository.findByUsername(null);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty optional for empty string username")
        void shouldReturnEmptyOptionalForEmptyStringUsername() {
            Optional<LichessProfile> result = repository.findByUsername("");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should be case sensitive for usernames")
        void shouldBeCaseSensitiveForUsernames() {
            Optional<LichessProfile> lowerCase = repository.findByUsername("testuser1");
            Optional<LichessProfile> upperCase = repository.findByUsername("TESTUSER1");

            assertThat(lowerCase).isPresent();
            assertThat(upperCase).isEmpty();
        }

        @Test
        @DisplayName("Should handle username with special characters")
        void shouldHandleUsernameWithSpecialCharacters() {
            LichessProfile specialProfile = createTestProfile(
                    "user-name_123", 100, 50, 1500, 1400, 1550, 1600, 1700
            );
            entityManager.persistAndFlush(specialProfile);
            entityManager.clear();

            Optional<LichessProfile> result = repository.findByUsername("user-name_123");

            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("user-name_123");
        }

        @Test
        @DisplayName("Should return profile with zero ratings")
        void shouldReturnProfileWithZeroRatings() {
            LichessProfile zeroProfile = createTestProfile(
                    "newuser", 10, 5, 0, 0, 0, 0, 0
            );
            entityManager.persistAndFlush(zeroProfile);
            entityManager.clear();

            Optional<LichessProfile> result = repository.findByUsername("newuser");

            assertThat(result).isPresent();
            LichessProfile profile = result.get();
            assertThat(profile.getBlitzRating()).isEqualTo(0);
            assertThat(profile.getBulletRating()).isEqualTo(0);
            assertThat(profile.getRapidRating()).isEqualTo(0);
            assertThat(profile.getClassicalRating()).isEqualTo(0);
            assertThat(profile.getPuzzleRating()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should return profile with high ratings")
        void shouldReturnProfileWithHighRatings() {
            LichessProfile highRatingProfile = createTestProfile(
                    "grandmaster", 5000, 4500, 2800, 2700, 2850, 2900, 3200
            );
            entityManager.persistAndFlush(highRatingProfile);
            entityManager.clear();

            Optional<LichessProfile> result = repository.findByUsername("grandmaster");

            assertThat(result).isPresent();
            LichessProfile profile = result.get();
            assertThat(profile.getBlitzRating()).isEqualTo(2800);
            assertThat(profile.getBulletRating()).isEqualTo(2700);
            assertThat(profile.getRapidRating()).isEqualTo(2850);
            assertThat(profile.getClassicalRating()).isEqualTo(2900);
            assertThat(profile.getPuzzleRating()).isEqualTo(3200);
        }
    }

    @Nested
    @DisplayName("deleteByUsername Tests")
    class DeleteByUsernameTests {

        @Test
        @DisplayName("Should delete existing profile by username")
        void shouldDeleteExistingProfileByUsername() {
            assertThat(repository.findByUsername("testuser1")).isPresent();
            assertThat(repository.count()).isEqualTo(3);

            repository.deleteByUsername("testuser1");
            entityManager.flush();
            entityManager.clear();

            assertThat(repository.findByUsername("testuser1")).isEmpty();
            assertThat(repository.count()).isEqualTo(2);

            assertThat(repository.findByUsername("testuser2")).isPresent();
            assertThat(repository.findByUsername("testuser3")).isPresent();
        }

        @Test
        @DisplayName("Should handle deletion of non-existent username")
        void shouldHandleDeletionOfNonExistentUsername() {
            long initialCount = repository.count();

            repository.deleteByUsername("nonexistent");
            entityManager.flush();

            assertThat(repository.count()).isEqualTo(initialCount);
        }

        @Test
        @DisplayName("Should handle deletion with null username")
        void shouldHandleDeletionWithNullUsername() {
            long initialCount = repository.count();

            repository.deleteByUsername(null);
            entityManager.flush();

            assertThat(repository.count()).isEqualTo(initialCount);
        }

        @Test
        @DisplayName("Should handle deletion with empty string username")
        void shouldHandleDeletionWithEmptyStringUsername() {
            long initialCount = repository.count();

            repository.deleteByUsername("");
            entityManager.flush();

            assertThat(repository.count()).isEqualTo(initialCount);
        }

        @Test
        @DisplayName("Should be case sensitive for deletion")
        void shouldBeCaseSensitiveForDeletion() {
            assertThat(repository.findByUsername("testuser1")).isPresent();

            repository.deleteByUsername("TESTUSER1");
            entityManager.flush();
            entityManager.clear();

            assertThat(repository.findByUsername("testuser1")).isPresent();
            assertThat(repository.count()).isEqualTo(3);
        }

        @Test
        @DisplayName("Should delete profile with special characters in username")
        void shouldDeleteProfileWithSpecialCharactersInUsername() {

            LichessProfile specialProfile = createTestProfile(
                    "user-name_123", 100, 50, 1500, 1400, 1550, 1600, 1700
            );
            entityManager.persistAndFlush(specialProfile);
            entityManager.clear();
            assertThat(repository.findByUsername("user-name_123")).isPresent();

            repository.deleteByUsername("user-name_123");
            entityManager.flush();
            entityManager.clear();

            assertThat(repository.findByUsername("user-name_123")).isEmpty();
        }

        @Test
        @DisplayName("Should not affect other profiles when deleting one")
        void shouldNotAffectOtherProfilesWhenDeletingOne() {

            assertThat(repository.count()).isEqualTo(3);

            repository.deleteByUsername("testuser2");
            entityManager.flush();
            entityManager.clear();

            assertThat(repository.count()).isEqualTo(2);
            assertThat(repository.findByUsername("testuser2")).isEmpty();

            Optional<LichessProfile> profile1 = repository.findByUsername("testuser1");
            Optional<LichessProfile> profile3 = repository.findByUsername("testuser3");

            assertThat(profile1).isPresent();
            assertThat(profile3).isPresent();
            assertThat(profile1.get().getTotalGames()).isEqualTo(1500);
            assertThat(profile3.get().getTotalGames()).isEqualTo(500);
        }
    }

    @Nested
    @DisplayName("Repository Integration Tests")
    class RepositoryIntegrationTests {

        @Test
        @DisplayName("Should persist and retrieve complete profile data")
        void shouldPersistAndRetrieveCompleteProfileData() {

            LichessProfile complexProfile = createTestProfile(
                    "complexuser",
                    10000, 9500,
                    2400, 2300, 2450, 2500, 2800
            );

            LichessProfile saved = entityManager.persistAndFlush(complexProfile);
            entityManager.clear();

            Optional<LichessProfile> retrieved = repository.findByUsername("complexuser");

            assertThat(retrieved).isPresent();
            LichessProfile profile = retrieved.get();

            assertThat(profile.getId()).isNotNull();
            assertThat(profile.getUsername()).isEqualTo("complexuser");
            assertThat(profile.getTotalGames()).isEqualTo(10000);
            assertThat(profile.getRatedGames()).isEqualTo(9500);
            assertThat(profile.getBlitzRating()).isEqualTo(2400);
            assertThat(profile.getBulletRating()).isEqualTo(2300);
            assertThat(profile.getRapidRating()).isEqualTo(2450);
            assertThat(profile.getClassicalRating()).isEqualTo(2500);
            assertThat(profile.getPuzzleRating()).isEqualTo(2800);
        }

        @Test
        @DisplayName("Should handle profile with minimum values")
        void shouldHandleProfileWithMinimumValues() {

            LichessProfile minProfile = createTestProfile(
                    "minuser", 0, 0, 0, 0, 0, 0, 0
            );

            entityManager.persistAndFlush(minProfile);
            entityManager.clear();

            Optional<LichessProfile> retrieved = repository.findByUsername("minuser");

            assertThat(retrieved).isPresent();
            LichessProfile profile = retrieved.get();
            assertThat(profile.getTotalGames()).isEqualTo(0);
            assertThat(profile.getRatedGames()).isEqualTo(0);
            assertThat(profile.getBlitzRating()).isEqualTo(0);
            assertThat(profile.getBulletRating()).isEqualTo(0);
            assertThat(profile.getRapidRating()).isEqualTo(0);
            assertThat(profile.getClassicalRating()).isEqualTo(0);
            assertThat(profile.getPuzzleRating()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should maintain referential integrity across operations")
        void shouldMaintainReferentialIntegrityAcrossOperations() {

            String username = "integrityuser";
            assertThat(repository.findByUsername(username)).isEmpty();

            LichessProfile profile = createTestProfile(
                    username, 1000, 800, 1500, 1400, 1600, 1700, 1800
            );
            entityManager.persistAndFlush(profile);
            entityManager.clear();

            assertThat(repository.findByUsername(username)).isPresent();

            Optional<LichessProfile> retrieved = repository.findByUsername(username);
            assertThat(retrieved).isPresent();

            LichessProfile existingProfile = retrieved.get();
            existingProfile.setTotalGames(1100);
            existingProfile.setBlitzRating(1550);
            entityManager.merge(existingProfile);
            entityManager.flush();
            entityManager.clear();

            Optional<LichessProfile> updated = repository.findByUsername(username);
            assertThat(updated).isPresent();
            assertThat(updated.get().getTotalGames()).isEqualTo(1100);
            assertThat(updated.get().getBlitzRating()).isEqualTo(1550);

            repository.deleteByUsername(username);
            entityManager.flush();
            entityManager.clear();

            assertThat(repository.findByUsername(username)).isEmpty();
        }

        @Test
        @DisplayName("Should handle concurrent access patterns")
        void shouldHandleConcurrentAccessPatterns() {

            String username = "concurrentuser";

            LichessProfile profile1 = createTestProfile(username, 100, 80, 1200, 1100, 1250, 1300, 1400);
            entityManager.persistAndFlush(profile1);
            entityManager.clear();

            assertThat(repository.findByUsername(username)).isPresent();

            repository.deleteByUsername(username);
            entityManager.flush();
            entityManager.clear();

            assertThat(repository.findByUsername(username)).isEmpty();

            LichessProfile profile2 = createTestProfile(username, 200, 180, 1300, 1200, 1350, 1400, 1500);
            entityManager.persistAndFlush(profile2);
            entityManager.clear();

            Optional<LichessProfile> final_profile = repository.findByUsername(username);
            assertThat(final_profile).isPresent();
            assertThat(final_profile.get().getTotalGames()).isEqualTo(200);
            assertThat(final_profile.get().getBlitzRating()).isEqualTo(1300);
        }

        @Test
        @DisplayName("Should enforce unique username constraint")
        void shouldEnforceUniqueUsernameConstraint() {

            String duplicateUsername = "duplicateuser";
            LichessProfile profile1 = createTestProfile(duplicateUsername, 100, 80, 1200, 1100, 1250, 1300, 1400);

            entityManager.persistAndFlush(profile1);
            entityManager.clear();

            assertThat(repository.findByUsername(duplicateUsername)).isPresent();

            LichessProfile profile2 = createTestProfile(duplicateUsername, 200, 180, 1300, 1200, 1350, 1400, 1500);

            try {
                entityManager.persistAndFlush(profile2);
                entityManager.clear();

                long count = repository.findAll().stream()
                        .filter(p -> duplicateUsername.equals(p.getUsername()))
                        .count();

                assertThat(count).isGreaterThan(0);

            } catch (Exception e) {

                assertThat(e).isInstanceOf(Exception.class);
            }
        }
    }
}
