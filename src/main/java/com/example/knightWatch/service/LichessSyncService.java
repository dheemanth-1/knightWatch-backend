package com.example.knightWatch.service;

import chariot.ClientAuth;
import chariot.api.GamesApiAuth;
import chariot.api.UsersApiAuth;
import chariot.model.*;
import com.example.knightWatch.dto.SyncCheckDTO;
import com.example.knightWatch.dto.SyncStatusDTO;
import com.example.knightWatch.model.LocalGame;
import com.example.knightWatch.model.LocalProfile;
import com.example.knightWatch.model.LichessSyncStatus;
import com.example.knightWatch.projection.LichessSyncStatusProjection;
import com.example.knightWatch.repository.LocalGameRepository;
import com.example.knightWatch.repository.LocalProfileRepository;
import com.example.knightWatch.repository.SyncStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LichessSyncService {
    private final LichessGameService lichessGameService;
    private final UsersApiAuth userApi;
    private final GamesApiAuth gamesApi;
    private final LocalProfileRepository profileRepo;
    private final LocalGameRepository gameRepo;
    private final GameStatsService gameStatsService;
    //private final PlayerProfileRepository playerProfileRepo;
    private final SyncStatusRepository syncStatusRepo;

    public LichessSyncService(ClientAuth client,
                              LocalProfileRepository profileRepo,
                              LocalGameRepository gameRepo,
                              LichessGameService lichessGameService,
                              GameStatsService gameStatsService,
                              //PlayerProfileRepository playerProfileRepo,
                              SyncStatusRepository syncStatusRepo) {

        this.userApi = client.users();
        this.gamesApi = client.games();
        this.profileRepo = profileRepo;
        this.gameRepo = gameRepo;
        this.lichessGameService = lichessGameService;
        this.gameStatsService = gameStatsService;
        //this.playerProfileRepo = playerProfileRepo;
        this.syncStatusRepo = syncStatusRepo;
    }

    private static final Logger log = LoggerFactory.getLogger(LichessSyncService.class);

    @Transactional(readOnly = true)
    public SyncCheckDTO isUserAlreadySynced(String username, int numberOfGamesQueried, long userId) {
        try {

            LocalGame lastDbGame = gameRepo.findLatestGameByUsernameAndUserId(username, userId);

            if (lastDbGame == null) {
                log.info("No games in database for {}, sync needed", username);
                return new SyncCheckDTO(true, null, numberOfGamesQueried, null);
            }
            System.out.println("lastDBGame" + lastDbGame);
            List<String> lastPgnGame = gamesApi.pgnByUserId(username, params -> {
                params.opening(true);
                params.tags(true);
                params.max(1);
            }).stream().map(Object::toString).toList();
            if (lastPgnGame.isEmpty()) {
                log.warn("Could not fetch latest game from API for {}", username);
                throw new RuntimeException("Could not fetch latest game from API for " + username);
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
            Integer requestedGamesPending = numberOfGamesQueried - (int) gameRepo.countByUsernameAndLocalProfile_User_Id(username, userId);
            System.out.println("requested pending games: " + requestedGamesPending);
            return new SyncCheckDTO(false, isSame, requestedGamesPending, getOldestSyncedDateGamesDateTime(username, userId));

        } catch (Exception e) {
            log.error("Error checking sync status for {}: {}", username, e.getMessage());
            return new SyncCheckDTO(null, null, null, null);
        }
    }

    private String extractFromPgn(String pgn, String tagName) {
        String pattern = "\\[" + tagName + " \"([^\"]+)\"\\]";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(pgn);
        return matcher.find() ? matcher.group(1) : null;
    }

    @Transactional
    public LichessSyncStatus syncUser(String username, Optional<Integer> numberOfGames, com.example.knightWatch.model.User loggedInUser) {
        Optional<LocalProfile> existingProfile = profileRepo.findByUsername(username);
        User user;
        try {
            user = userApi.byId(username).get();
            if (user == null || user.id() == null) {
                throw new RuntimeException("Invalid user data received from Lichess API");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user from Lichess API: " + e.getMessage(), e);
        }
        if(existingProfile.isEmpty()) {
            LocalProfile profile = new LocalProfile(user, "lichess");
            profile.setUser(loggedInUser);
            profileRepo.save(profile);
            existingProfile = profileRepo.findByUsername(username);
        }
        int maxGames = profileRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("Profile not found")).getRatedGames();
        int numberOfGamesToBeQueried;
        if (numberOfGames.isPresent() && numberOfGames.get() <= maxGames) {
            numberOfGamesToBeQueried = numberOfGames.get();
        } else {
            numberOfGamesToBeQueried = maxGames;
        }
        SyncCheckDTO syncCheck = isUserAlreadySynced(username, numberOfGamesToBeQueried, loggedInUser.getId());
        if (existingProfile.isPresent() && syncCheck.latestGameMatch() != null  && syncCheck.requestedGamesPending() <= 0) {
            log.info("Skipping sync for {} - already up to date", username);
            return syncStatusRepo.findFirstByUsernameAndUserIdOrderBySyncIdDesc(username, loggedInUser.getId());
        }
        log.info("Starting game fetch...");
        long start = System.currentTimeMillis();

        List<LocalGame> entities;
        if(syncCheck.freshSync()) {
            System.out.println("is this a fresh sync???");
            entities = lichessGameService.fetchUserGamesWithOpenings(username, numberOfGamesToBeQueried);
        } else {
            System.out.println("not a fresh sync???");
            System.out.println("pending games :" + syncCheck.requestedGamesPending());
            System.out.printf("earliest game date time :" + syncCheck.earliestGameDateTime().toString());
            entities = lichessGameService.fetchUserGamesWithOpeningsUntilTimeDate(username, syncCheck.requestedGamesPending(), syncCheck.earliestGameDateTime());
        }

        if (entities == null || entities.isEmpty()) {
            log.warn("No games found for user: {}", username);
            return null;
        }

        //double winRate = gameStatsService.calculateOverallStats(username).getWinRate();
        long elapsedMs = System.currentTimeMillis() - start;
        log.info("Game fetch completed, time taken: {} ms", elapsedMs);
        log.info("Starting game saving...");
        start = System.currentTimeMillis();
        for(LocalGame entity : entities) {
            entity.setLocalProfile(existingProfile.get());
        }
        gameRepo.saveAll(entities);
        elapsedMs = System.currentTimeMillis() - start;
        log.info("Game save completed, time taken: {} ms", elapsedMs);
//        PlayerProfile playerProfile = playerProfileRepo.findByUsername(username);
//        if(playerProfile != null) {
//            playerProfile.setTotalGames(numberOfGamesToBeQueried);
//            playerProfile.setWinRate(winRate);
//            playerProfile.setLastSyncTime(LocalDateTime.now());
//        } else {
//            playerProfile = new PlayerProfile(username, numberOfGamesToBeQueried, winRate, LocalDateTime.now());
//        }
//        playerProfileRepo.save(playerProfile);

        LichessSyncStatus lichessSyncStatus = new LichessSyncStatus(LocalDateTime.now().toString(),
                username,
                getOldestSyncedDateGamesDateTime(username, loggedInUser.getId()).toString(),
                numberOfGamesToBeQueried,
                true);
        lichessSyncStatus.setUser(loggedInUser);
        this.syncStatusRepo.save(lichessSyncStatus);
        return lichessSyncStatus;
    }

    public Integer getTotalGames(String username) {
        return userApi.byId(username).get().accountStats().rated();
    }

    public SyncStatusDTO previousSyncCheck(String username, long userId) {
        LichessSyncStatus lichessSyncStatus = this.syncStatusRepo.findFirstByUsernameAndUserIdOrderBySyncIdDesc(username, userId);
        SyncStatusDTO syncStatusDTO;
        if(lichessSyncStatus == null) {
            syncStatusDTO = new SyncStatusDTO(null, true, null, 0, username);
        } else {
            syncStatusDTO = new SyncStatusDTO(LocalDateTime.parse(lichessSyncStatus.getLastSync()),
                    lichessSyncStatus.isUptoDate(),
                    lichessSyncStatus.getLastLocalGameDate(),
                    lichessSyncStatus.getNumberOfGamesSynced(),
                    lichessSyncStatus.getUsername());
        }
        return syncStatusDTO;
    }

    public ZonedDateTime getOldestSyncedDateGamesDateTime(String username, long userId) {
        LocalGame oldestGame = this.gameRepo.findOldestGameByUsernameAndUserId(username, userId);
        if(oldestGame == null) {
            return null;
        }
        LocalDateTime ldt = LocalDateTime.parse(oldestGame.getPlayedAt());
        return ldt.atZone(ZoneId.of("UTC"));
    }

    public List<LichessSyncStatusProjection> syncHistory(String username, long userId) {
        return this.syncStatusRepo.findAllProjectedByUsernameAndUserId(username, userId);
    }
}
