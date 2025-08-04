package com.example.knightWatch.config;

import chariot.Client;
import chariot.ClientAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${lichess.token}")
    private String lichessToken;

    // Optional: For authenticated access with a personal token
    @Bean
    public ClientAuth lichessClientAuth() {
        return Client.auth(lichessToken); // You can store this in application.properties securely
    }

}