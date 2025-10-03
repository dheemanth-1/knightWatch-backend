package com.example.knightWatch.model;

import chariot.model.StatsPerf;
import chariot.model.StatsPerfType;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Entity
@Table(name = "local_profile")
public class LocalProfile {

    private static final Logger logger = LoggerFactory.getLogger(LocalProfile.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
    }

    public int getRatedGames() {
        return ratedGames;
    }

    public void setRatedGames(int ratedGames) {
        this.ratedGames = ratedGames;
    }

    public int getBlitzRating() {
        return blitzRating;
    }

    public void setBlitzRating(int blitzRating) {
        this.blitzRating = blitzRating;
    }

    public int getBulletRating() {
        return bulletRating;
    }

    public void setBulletRating(int bulletRating) {
        this.bulletRating = bulletRating;
    }

    public int getRapidRating() {
        return rapidRating;
    }

    public void setRapidRating(int rapidRating) {
        this.rapidRating = rapidRating;
    }

    public int getClassicalRating() {
        return classicalRating;
    }

    public void setClassicalRating(int classicalRating) {
        this.classicalRating = classicalRating;
    }

    public int getPuzzleRating() {
        return puzzleRating;
    }

    public void setPuzzleRating(int puzzleRating) {
        this.puzzleRating = puzzleRating;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    private String username;

    private int totalGames;
    private int ratedGames;
    private int blitzRating;
    private int bulletRating;
    private int rapidRating;
    private int classicalRating;
    private int puzzleRating;
    private String source;

    public LocalProfile() {
    }

    private static int getRating(Map<StatsPerfType, StatsPerf> ratings, StatsPerfType type) {
        StatsPerf perf = ratings.get(type);
        if (perf instanceof StatsPerf.StatsPerfGame stats) {
            return stats.rating();
        }
        return 0; // default if not found or wrong type
    }

    public LocalProfile(chariot.model.User user, String source) {
        if (user == null || user.id() == null) {
            throw new IllegalArgumentException("Invalid user data");
        }
        this.username = user.id();
        this.totalGames = user.accountStats() != null ? user.accountStats().all() : 0;
        this.ratedGames = user.accountStats()  != null ? user.accountStats().rated() : 0;
        this.blitzRating = getRating(user.ratings(), StatsPerfType.blitz);
        this.bulletRating = getRating(user.ratings(), StatsPerfType.bullet);
        this.rapidRating = getRating(user.ratings(), StatsPerfType.rapid);
        this.classicalRating = getRating(user.ratings(), StatsPerfType.classical);
        this.puzzleRating = getRating(user.ratings(), StatsPerfType.puzzle);
        this.source = source;
    }

}