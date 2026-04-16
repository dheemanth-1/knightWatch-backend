package com.example.knightWatch.model;

import chariot.model.Game;
import com.example.knightWatch.dto.OpeningInfo;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLLTreeType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "local_game",
indexes = {
@Index(name = "idx_game_user_id", columnList = "user_id"),
@Index(name = "idx_game_opening_id", columnList = "opening_id"),
@Index(name = "idx_game_user_id_result", columnList = "user_id, result")
    }
)
public class LocalGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String username;
    private String gameId;
    private String openingName;
    private String result;
    private LocalDateTime playedAt;
    @Column(name = "pgn", columnDefinition = "text")
    private String pgn;
    private String eco;
    private String status;
    private String source;
    @Column(name="player_color")
    private String playerColor;

    @Type(PostgreSQLLTreeType.class)
    @Column(name = "pgn_path", columnDefinition = "ltree")
    private String pgnPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opening_id", nullable = true)
    private Opening opening;

    @ManyToOne
    @JoinColumn(name = "local_profile_id")
    private LocalProfile localProfile;

    @Column(name = "event")
    private String event;

    @Column(name = "time_control", length = 50)
    private String timeControl;  // "180+0", "600+5", "60+1", etc.

    @Column(name = "variant")
    private String variant;

    @Column(name = "termination")
    private String termination;

    @Column(name = "player_elo")
    private Integer playerElo;

    @Column(name = "opponent_elo")
    private Integer opponentElo;

    @Column(name = "player_rating_diff")
    private Integer playerRatingDiff;

    @Column(name = "opponent_rating_diff")
    private Integer opponentRatingDiff;



    public LocalGame() {}

    public LocalGame(chariot.model.Game game, String username) {
        if (username == null || game == null) {
            throw new IllegalArgumentException("Invalid game data");
        }
        this.username = username;
        this.gameId = game.id();
        this.openingName = game.opening()
                .map(Game.Opening::name)
                .orElse(null);
        this.status = game.status().name();
        this.playedAt = LocalDateTime.parse(game.createdAt().toString()); // ISO date
    }


    public LocalGame(OpeningInfo openingInfo) {
        this.userId = openingInfo.getUserId();
        this.username = openingInfo.getUsername();
        this.gameId = openingInfo.getGameId();
        this.openingName = openingInfo.getOpeningName();
        this.result = openingInfo.getResultFromColor(this.username);
        this.playedAt = LocalDateTime.parse(openingInfo.getPlayedAt(), DateTimeFormatter.ofPattern("yyyy.MM.dd'T'HH:mm:ss"));
        this.pgn = openingInfo.getPgn();
        this.eco = openingInfo.getEco();
        this.status = openingInfo.getStatus();
        this.source = openingInfo.getSource();
        this.playerColor = openingInfo.getPlayerColor();
        this.pgnPath = openingInfo.getPgnPath();
        this.event = openingInfo.getEvent();
        this.timeControl = openingInfo.getTimeControl();
        this.variant = openingInfo.getVariant();
        this.termination = openingInfo.getTermination();
        this.playerElo = openingInfo.getPlayerElo();
        this.opponentElo = openingInfo.getOpponentElo();
        this.playerRatingDiff = openingInfo.getPlayerRatingDiff();
        this.opponentRatingDiff = openingInfo.getOpponentRatingDiff();
    }


    @Override
    public String toString() {
        return "LichessGame{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", gameId='" + gameId + '\'' +
                ", openingName='" + openingName + '\'' +
                ", result='" + result + '\'' +
                ", playedAt='" + playedAt + '\'' +
                ", pgn='" + pgn + '\'' +
                ", eco='" + eco + '\'' +
                ", status='" + status + '\'' +
                ", source='" + source + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {return userId;}

    public void setUserId(Long userId) {this.userId = userId;}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getOpeningName() {
        return openingName;
    }

    public void setOpeningName(String openingName) {
        this.openingName = openingName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }

    public String getPgn() {
        return pgn;
    }

    public void setPgn(String pgn) {
        this.pgn = pgn;
    }

    public String getEco() {
        return eco;
    }

    public void setEco(String eco) {
        this.eco = eco;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    public LocalProfile getLocalProfile() {
        return localProfile;
    }

    public void setLocalProfile(LocalProfile localProfile) {
        this.localProfile = localProfile;
    }

    public String getPgnPath() {
        return pgnPath;
    }

    public void setPgnPath(String pgnPath) {
        this.pgnPath = pgnPath;
    }

    public Opening getOpening() {
        return opening;
    }

    public void setOpening(Opening opening) {
        this.opening = opening;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTimeControl() {
        return timeControl;
    }

    public void setTimeControl(String timeControl) {
        this.timeControl = timeControl;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getTermination() {
        return termination;
    }

    public void setTermination(String termination) {
        this.termination = termination;
    }

    public Integer getPlayerElo() {
        return playerElo;
    }

    public void setPlayerElo(Integer playerElo) {
        this.playerElo = playerElo;
    }

    public Integer getOpponentElo() {
        return opponentElo;
    }

    public void setOpponentElo(Integer opponentElo) {
        this.opponentElo = opponentElo;
    }

    public Integer getPlayerRatingDiff() {
        return playerRatingDiff;
    }

    public void setPlayerRatingDiff(Integer playerRatingDiff) {
        this.playerRatingDiff = playerRatingDiff;
    }

    public Integer getOpponentRatingDiff() {
        return opponentRatingDiff;
    }

    public void setOpponentRatingDiff(Integer opponentRatingDiff) {
        this.opponentRatingDiff = opponentRatingDiff;
    }
}