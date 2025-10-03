package com.example.knightWatch.config;
import io.github.sornerol.chess.pubapi.client.PlayerClient;
import io.github.sornerol.chess.pubapi.domain.player.Player;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChesscomConfig {
    @Bean
    public PlayerClient chessComClient(@Value("${CHESSCOM_USER}") String user, @Value("${CHESSCOM_MAIL}") String mail) {
        PlayerClient client = new PlayerClient();
        client.setUserAgent("Chess App; username: "+ user + "; contact: " + mail + ";");
        return client;
    }
}
