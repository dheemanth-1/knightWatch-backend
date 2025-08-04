package com.example.knightWatch.service;

import chariot.ClientAuth;
import chariot.api.GamesApiAuth;
import chariot.api.UsersApiAuth;
import chariot.model.*;
import com.example.knightWatch.model.LichessGame;
import com.example.knightWatch.model.LichessProfile;
import com.example.knightWatch.repository.LichessGameRepository;
import com.example.knightWatch.repository.LichessProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LichessSyncService {
    private final LichessGameService lichessGameService;
    private final UsersApiAuth userApi;
    private final GamesApiAuth gamesApi;
    private final LichessProfileRepository profileRepo;
    private final LichessGameRepository gameRepo;

    public LichessSyncService(ClientAuth client,
                              LichessProfileRepository profileRepo,
                              LichessGameRepository gameRepo,LichessGameService lichessGameService) {
        this.userApi = client.users();
        this.gamesApi = client.games();
        this.profileRepo = profileRepo;
        this.gameRepo = gameRepo;
        this.lichessGameService = lichessGameService;
    }

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

    public void logUserGamesInfo(String username) {
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

    @Transactional
    public void syncUser(String username) {
        //logUserGamesInfo(username);
        // Profile
        Optional<User> userOpt = Optional.ofNullable(userApi.byId(username).get());
        userOpt.ifPresent(user -> {
            LichessProfile profile = new LichessProfile(user);
            profileRepo.save(profile);
        });


//        // Recent games (e.g., last 10)
//        List<Game> games = gamesApi.byUserId(username).stream()//.export(username)
//                .limit(10).toList();
//
//
//
//        List<LichessGame> entities = games.stream()
//                .map(g -> new LichessGame(g, username)) // map to your entity
//                .collect(Collectors.toList());

        List<LichessGame> entities = lichessGameService.fetchUserGamesWithOpenings(username);
        gameRepo.saveAll(entities);
    }
}
