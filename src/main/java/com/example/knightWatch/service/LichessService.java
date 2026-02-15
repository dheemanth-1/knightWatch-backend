package com.example.knightWatch.service;

import chariot.Client;
import chariot.model.Game;
import chariot.model.User;
import com.example.knightWatch.model.LocalGame;
import com.example.knightWatch.repository.LocalGameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class LichessService {

    private final Client client;
    private final LocalGameRepository gameRepo;

    public LichessService(Client client, LocalGameRepository gameRepo) {
        this.client = client;
        this.gameRepo = gameRepo;
    }
    public List<LocalGame> getRecentGames(String username) {
        return gameRepo.findTop10ByUsernameOrderByPlayedAtDesc(username);
    }
    public List<LocalGame> getAllGamesByUsernameAndSource(String username, String source) {return gameRepo.findByUsernameAndSource(username, source);}
    public List<LocalGame> getAllGames(String username) {
        return gameRepo.findByUsername(username);
    }
    public Optional<User> getUserProfile(String username) {
        return (Optional<User>) client.users().byId(username).maybe(); // always returns Optional<User> so unchecked cast acceptable for now.
    }

    public List<Game> getRecentGamesFromExternalApi(String username) {
        return this.client.games().byUserId(username).stream().limit(10).toList();
    }
}
