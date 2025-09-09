package com.example.knightWatch.service;

import chariot.Client;
import chariot.ClientAuth;
import chariot.model.User;
import com.example.knightWatch.model.LichessGame;
import com.example.knightWatch.repository.LichessGameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class LichessService {

    private final Client client;
    private final LichessGameRepository gameRepo;

    public LichessService(Client client,LichessGameRepository gameRepo) {
        this.client = client;
        this.gameRepo = gameRepo;
    }
    public List<LichessGame> getRecentGames(String username) {
        return gameRepo.findTop10ByUsernameOrderByPlayedAtDesc(username);
    }
    public List<LichessGame> getAllGames(String username) {
        return gameRepo.findByUsername(username);
    }
    public Optional<User> getUserProfile(String username) {
        return (Optional<User>) client.users().byId(username).maybe(); // always returns Optional<User> so unchecked cast acceptable for now.
    }
}
