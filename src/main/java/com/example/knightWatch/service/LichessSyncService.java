package com.example.knightWatch.service;

import chariot.ClientAuth;
import chariot.api.GamesApiAuth;
import chariot.api.UsersApiAuth;
import chariot.model.*;
import com.example.knightWatch.model.LichessGame;
import com.example.knightWatch.model.LichessProfile;
import com.example.knightWatch.model.PlayerProfile;
import com.example.knightWatch.repository.LichessGameRepository;
import com.example.knightWatch.repository.LichessProfileRepository;
import com.example.knightWatch.repository.PlayerProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.sql.SQLOutput;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class LichessSyncService {
    private final LichessGameService lichessGameService;
    private final UsersApiAuth userApi;
    private final GamesApiAuth gamesApi;
    private final LichessProfileRepository profileRepo;
    private final LichessGameRepository gameRepo;
    private final GameStatsService gameStatsService;
    private final PlayerProfileRepository playerProfileRepo;

    public LichessSyncService(ClientAuth client,
                              LichessProfileRepository profileRepo,
                              LichessGameRepository gameRepo,LichessGameService lichessGameService, GameStatsService gameStatsService, PlayerProfileRepository playerProfileRepo) {
        this.userApi = client.users();
        this.gamesApi = client.games();
        this.profileRepo = profileRepo;
        this.gameRepo = gameRepo;
        this.lichessGameService = lichessGameService;
        this.gameStatsService = gameStatsService;
        this.playerProfileRepo = playerProfileRepo;
    }

    private static final Logger log = LoggerFactory.getLogger(LichessSyncService.class);

    private String tryGetOpening(Game game) {
        try {
            if (game.opening().isPresent()) {
                return game.opening().get().toString();
            } else {
                return "No opening data";
            }
        } catch (Exception e) {
            return "Error accessing opening: " + e.getMessage();
        }
    }

    public void logUserGamesInfo(String username) { // was only used for logging/testing purposes
        List<Game> gamesWithoutParams = gamesApi.byUserId(username).stream().toList();
        System.out.println("Games without params: " + gamesWithoutParams.size());
        for (int i = 0; i < Math.min(3, gamesWithoutParams.size()); i++) {
            Game game = gamesWithoutParams.get(i);
            System.out.println("\n=== Game " + (i+1) + " ===");
            System.out.println("Game ID: " + game.id());
            System.out.println("Game object class: " + game.getClass().getName());
            System.out.println("Available methods: ");

            // Print all available methods
            Arrays.stream(game.getClass().getMethods())
                    .filter(method -> method.getName().contains("open") ||
                            method.getName().contains("Open"))
                    .forEach(method -> System.out.println("  - " + method.getName()));

            // Try different ways to access opening data
            System.out.println("Trying game.opening(): " + tryGetOpening(game));
            System.out.println("Game toString(): " + game.toString());
        }

        System.out.println("Trying PGN method for user: " + username);

        // Try the PGN method which might include opening data
        List<String> pgnGames = gamesApi.pgnByUserId(username, params -> {
            System.out.println("PGN Parameter class: " + params.getClass().getName());
            // Log available PGN parameters
            Method[] methods = params.getClass().getMethods();
            for (Method method : methods) {
                if (!method.getName().startsWith("get") &&
                        !method.getName().equals("hashCode") &&
                        !method.getName().equals("toString") &&
                        !method.getName().equals("equals") &&
                        method.getParameterCount() <= 1) {
                    System.out.println("Available PGN parameter: " + method.getName());
                }
            }
        }).stream().map(Object::toString).toList();

        System.out.println("First PGN game sample:");
        if (!pgnGames.isEmpty()) {
            System.out.println(pgnGames.get(0).substring(0, Math.min(500, pgnGames.get(0).length())));
        }
        System.out.println("Available methods on gamesApi:");
        System.out.println(Arrays.toString(gamesApi.getClass().getMethods()));
//        gamesApi.byUserId(username,
//                Opt.of(params -> {
//                    System.out.println("Available parameters:");
//                    System.out.println(params.getClass().getMethods());
//                    return params;
//                })
//        ).stream().toList();
    }

    private boolean isRecentSync(LocalDateTime lastSync) {
        if (lastSync == null) return false;
        return lastSync.isAfter(LocalDateTime.now().minusHours(1)); // Adjust threshold as needed
    }

    @Transactional(readOnly = true)
    public boolean isUserAlreadySynced(String username) {
        try {
            // Get last game from database
            LichessGame lastDbGame = gameRepo.findLatestGameByUsername(username);

            if (lastDbGame == null) {
                log.info("No games in database for {}, sync needed", username);
                return false;
            }

            List<String> lastPgnGame = gamesApi.pgnByUserId(username, params -> {
                params.opening(true);
                params.tags(true);
                params.max(1);
            }).stream().map(Object::toString).toList();
            if (lastPgnGame.isEmpty()) {
                log.warn("Could not fetch latest game from API for {}", username);
                return false;
            }

            String date = extractFromPgn(lastPgnGame.getFirst(), "UTCDate");
            String time = extractFromPgn(lastPgnGame.getFirst(),"UTCTime");
            String lastApiGameId = extractFromPgn(lastPgnGame.getFirst(),"GameId");
            assert date != null;
            String lastDateTimeOfApiGame = date.replace(".", "-") + "T" + time;
            String lastDateTimeOfDbGame = lastDbGame.getPlayedAt();

            System.out.println("API datetime: " + lastDateTimeOfApiGame);
            System.out.println("DB datetime: " + lastDateTimeOfDbGame);
            System.out.println("API gameId: " + lastApiGameId);
            System.out.println("DB gameId: " + lastDbGame.getGameId());


            boolean isSame = (lastDateTimeOfApiGame.equals(lastDateTimeOfDbGame) && (lastApiGameId.equals(lastDbGame.getGameId())));

            if (isSame) {
                log.info("User {} is already synced (last game: {})", username, lastDbGame.getId());
            } else {
                log.info("User {} needs sync. DB last game: {}, API last game: {}",
                        username, lastDbGame.getGameId(), lastApiGameId);
            }

            return isSame;

        } catch (Exception e) {
            log.error("Error checking sync status for {}: {}", username, e.getMessage());
            return false;
        }
    }

    private String extractFromPgn(String pgn, String tagName) {
        String pattern = "\\[" + tagName + " \"([^\"]+)\"\\]";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(pgn);
        return matcher.find() ? matcher.group(1) : null;
    }

    private void updateLastSyncTime(String username) {
        PlayerProfile playerProfile = playerProfileRepo.findByUsername(username);
        if (playerProfile != null) {
            playerProfile.setLastSyncTime(LocalDateTime.now());
            playerProfileRepo.save(playerProfile);
        }
    }
    @Transactional
    public void syncUser(String username, Optional<Integer> numberOfGames) {
        PlayerProfile existingProfile = playerProfileRepo.findByUsername(username);
        if (existingProfile != null && isUserAlreadySynced(username)) {
            log.info("Skipping sync for {} - already up to date", username);
            updateLastSyncTime(username); // Update sync timestamp even if no games were fetched
            return;
        }
        User user;
        try {
            user = userApi.byId(username).get();
            if (user == null || user.id() == null) {
                throw new RuntimeException("Invalid user data received from Lichess API");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user from Lichess API: " + e.getMessage(), e);
        }
        LichessProfile profile = new LichessProfile(user);
        profileRepo.save(profile);
        int maxGames = profileRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("Profile not found")).getRatedGames();
        int numberOfGamesToBeQueried;
        if (numberOfGames.isPresent() && numberOfGames.get() <= maxGames) {
            numberOfGamesToBeQueried = numberOfGames.get();
        } else {
            numberOfGamesToBeQueried = maxGames;
        }
        log.info("Starting game fetch...");
        long start = System.currentTimeMillis();
        List<LichessGame> entities = lichessGameService.fetchUserGamesWithOpenings(username, numberOfGamesToBeQueried);


        if (entities == null || entities.isEmpty()) {
            log.warn("No games found for user: {}", username);
            entities = new ArrayList<>();
            return;
        }

        double winRate = gameStatsService.calculateOverallStats(username).getWinRate();
        long elapsedMs = System.currentTimeMillis() - start;
        log.info("Game fetch completed, time taken: {} ms", elapsedMs);
        log.info("Starting game saving...");
        start = System.currentTimeMillis();
        gameRepo.saveAll(entities);
        elapsedMs = System.currentTimeMillis() - start;
        log.info("Game save completed, time taken: {} ms", elapsedMs);
        PlayerProfile playerProfile = playerProfileRepo.findByUsername(username);
        if(playerProfile != null) {
            playerProfile.setTotalGames(numberOfGamesToBeQueried);
            playerProfile.setWinRate(winRate);
            playerProfile.setLastSyncTime(LocalDateTime.now());
        } else {
            playerProfile = new PlayerProfile(username, numberOfGamesToBeQueried, winRate, LocalDateTime.now());
        }
        playerProfileRepo.save(playerProfile);

    }

    public Integer getTotalGames(String username) {
        return userApi.byId(username).get().accountStats().rated();
    }
}
