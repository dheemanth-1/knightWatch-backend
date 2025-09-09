package com.example.knightWatch.config;

import chariot.Client;
import chariot.ClientAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class LichessConfig {

    @Bean
    @Primary
    public Client lichessClient() {
        return Client.basic(); // for public, unauthenticated endpoints
    }


    @Bean
    public ClientAuth lichessClientAuth(@Value("${lichess.token}") String lichessToken) {
        return Client.auth(lichessToken);
    }
//    @Bean
//    @Primary
//    public Client lichessClient() {
//        return Client.basic();
//    }
//    @Value("${lichess.token}")
//    private String lichessToken;
//
//
//    @Bean
//    public ClientAuth lichessClientAuth() {
//        return Client.auth(lichessToken);
//    }

}