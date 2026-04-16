package com.example.knightWatch.dto;

import com.example.knightWatch.util.PgnToLtreeConverter;

import java.util.Map;

public class OpeningInfo {
    public static class Builder {
        public Builder(String gameId, String username, String source, String pgn, PgnToLtreeConverter pgnConverter, Long userId) {
            this.userId = userId;
            this.gameId = gameId;
            this.username = username;
            this.source = source;
            this.pgn = pgn;
            this.pgnConverter = pgnConverter;
        }

        public static String getGameLastParamFromChesscomLink(String link) {
            return link.substring(link.lastIndexOf('/')+1);
        }

        public static OpeningInfo fromPgnTags(Map<String, String> tags, String username, String source, String pgn, PgnToLtreeConverter pgnConverter, Long userId) {
            String gameId = (source.equals("chesscom"))? getGameLastParamFromChesscomLink(tags.get("Link")) : tags.get(("GameId"));
            String opening = (source.equals("chesscom"))? tags.get("ECOUrl") : tags.get("Opening");
            System.out.println("opening: " +  opening);
            System.out.println("gameId: " + gameId);
             return new Builder(gameId, username, source, pgn, pgnConverter, userId)
                    .eco(tags.get("ECO"))
                    .openingName(opening)
                    .resultNotation(tags.get("Result"))
                    .black(tags.get("Black"))
                    .white(tags.get("White"))
                    .timeControl(tags.get("TimeControl"))
                    .status(tags.get("Termination"))
                    .playedAt(formatDateTime(tags.get("UTCDate"), tags.get("UTCTime")))
                    .whiteElo(parseInteger(tags.get("WhiteElo")))
                    .blackElo(parseInteger(tags.get("BlackElo")))
                    .whiteEloDiff(parseInteger(tags.get("WhiteRatingDiff")))
                    .blackEloDiff(parseInteger(tags.get("BlackRatingDiff")))
                    .event(tags.get("Event"))
                    .variant(tags.get("Variant"))
                    .termination(tags.get("Termination"))
                    .build();
        }

        public static String formatDateTime(String date, String time) {
            return  date + "T" + time;
        }

        public static Integer parseInteger(String numberStr) {
            if(numberStr == null) return null;
            return Integer.valueOf(numberStr);
        }

        private final Long userId;
        private final String gameId;
        private String eco;
        private String openingName;
        private String pgn;
        private String resultNotation;
        private String black;
        private String white;
        private String timeControl;
        private String status;
        private String playedAt;
        private String source;
        private String username;
        private PgnToLtreeConverter pgnConverter;
        private String event;
        private String variant;
        private String termination;

        // Below are Optional, only appear in lichess pgn
        private Integer whiteElo;
        private Integer blackElo;
        private Integer whiteEloDiff;
        private Integer blackEloDiff;


        public Builder eco(String eco) {
            this.eco = eco;
            return this;
        }

        public Builder openingName(String openingName) {
            this.openingName = openingName;
            return this;
        }

        public Builder resultNotation(String resultNotation) {
            this.resultNotation = resultNotation;
            return this;
        }

        public Builder black(String black) {
            this.black = black;
            return this;
        }

        public Builder timeControl(String timeControl) {
            this.timeControl = timeControl;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder playedAt(String playedAt) {
            this.playedAt = playedAt;
            return this;
        }

        public Builder white(String white) {
            this.white = white;
            return this;
        }

        // Optional setters return this for chaining
        public Builder whiteElo(Integer whiteElo) {
            this.whiteElo = whiteElo;
            return this;
        }

        public Builder blackElo(Integer blackElo) {
            this.blackElo = blackElo;
            return this;
        }

        public Builder whiteEloDiff(Integer whiteEloDiff) {
            this.whiteEloDiff = whiteEloDiff;
            return this;
        }

        public Builder blackEloDiff(Integer blackEloDiff) {
            this.blackEloDiff = blackEloDiff;
            return this;
        }

        public Builder event(String event) {
            this.event = event;
            return this;
        }

        public Builder variant(String variant) {
            this.variant = variant;
            return this;
        }

        public Builder termination(String termination) {
            this.termination = termination;
            return this;
        }

        public OpeningInfo build() {
            return new OpeningInfo(this);
        }
    }

    public OpeningInfo(Builder builder) {
        this.userId = builder.userId;
        if(builder.source.equals("chesscom")) {
            this.gameId = builder.gameId.substring(builder.gameId.lastIndexOf('/') + 1);
            this.openingName = builder.openingName.substring(builder.openingName.lastIndexOf("/") + 1);
            this.status = builder.status.substring(builder.status.lastIndexOf(' ') + 1);
        } else {
            this.gameId = builder.gameId;
            this.openingName = builder.openingName;
            this.status = builder.status;
        }
        this.eco = builder.eco;
        this.pgn = builder.pgn;
        this.resultNotation = builder.resultNotation;
        this.black = builder.black;
        this.white = builder.white;
        this.timeControl = builder.timeControl;
        this.playedAt = builder.playedAt;
        this.source = builder.source;
        this.playerColor = extractPlayerColor(builder.username);
        this.username = builder.username;
        this.pgnConverter = builder.pgnConverter;
        this.event = builder.event;
        this.pgnPath = pgnConverter.convert(extractMovesFromPgn(builder.pgn));
        this.whiteElo = builder.whiteElo;
        this.blackElo = builder.blackElo;
        this.whiteEloDiff = builder.whiteEloDiff;
        this.blackEloDiff = builder.blackEloDiff;
        this.variant = builder.variant;
        this.termination = builder.termination;
    }

    private Long userId;
    private String gameId;
    private String eco;
    private String openingName;
    private String pgn;
    private String resultNotation;
    private String black;
    private String white;
    private String timeControl;
    private String status;
    private String playedAt;
    private String source;
    private String playerColor;
    private String username;
    private String event;
    private String pgnPath;
    private PgnToLtreeConverter pgnConverter;
    private Integer whiteElo;
    private Integer blackElo;
    private Integer whiteEloDiff;
    private Integer blackEloDiff;
    private String variant;
    private String termination;


    public String extractPlayerColor(String username) {
        return this.black.equals(username)? "black" : "white";
    }

    public String getResultFromColor(String userId) {
        if (userId == null || resultNotation == null) {
            return null;
        }
        return switch (resultNotation) {
            case "1-0" -> userId.equals(white) ? "won" : userId.equals(black) ? "lost" : null;
            case "0-1" -> userId.equals(black) ? "won" : userId.equals(white) ? "lost" : null;
            case "1/2-1/2" -> "draw";
            default -> null;
        };
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public String getEco() { return eco; }
    public void setEco(String eco) { this.eco = eco; }

    public String getOpeningName() { return openingName; }
    public void setOpeningName(String openingName) { this.openingName = openingName; }

    public String getPgn() { return pgn; }
    public void setPgn(String pgn) { this.pgn = pgn; }

    public String getBestOpeningName() {
        return openingName != null ? openingName : eco;
    }

    public String getResultNotation() {
        return resultNotation;
    }

    public void setResultNotation(String resultNotation) {
        this.resultNotation = resultNotation;
    }

    public String getBlack() {
        return black;
    }

    public void setBlack(String black) {
        this.black = black;
    }

    public String getWhite() {
        return white;
    }

    public void setWhite(String white) {
        this.white = white;
    }

    public String getTimeControl() {
        return timeControl;
    }

    public void setTimeControl(String timeControl) {
        this.timeControl = timeControl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(String playedAt) {
        this.playedAt = playedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPlayerColor() {return playerColor; }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getEvent() {return event;}

    public void setEvent(String event) {this.event = event;}

    public String getPgnPath() {return pgnPath;}

    public void setPgnPath(String pgnPath) {this.pgnPath = pgnPath;}

    public Integer getWhiteElo() {
        return whiteElo;
    }

    public void setWhiteElo(Integer whiteElo) {
        this.whiteElo = whiteElo;
    }

    public Integer getBlackElo() {
        return blackElo;
    }

    public void setBlackElo(Integer blackElo) {
        this.blackElo = blackElo;
    }

    public Integer getWhiteEloDiff() {
        return whiteEloDiff;
    }

    public void setWhiteEloDiff(Integer whiteEloDiff) {
        this.whiteEloDiff = whiteEloDiff;
    }

    public Integer getBlackEloDiff() {
        return blackEloDiff;
    }

    public void setBlackEloDiff(Integer blackEloDiff) {
        this.blackEloDiff = blackEloDiff;
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

    public static String extractMovesFromPgn(String fullPgn) {
        if (fullPgn == null || fullPgn.isBlank()) {
            return null;
        }
        String[] parts = fullPgn.split("\\n\\n", 2);

        if (parts.length < 2) {
            return null;
        }
        return parts[1].trim();
    }

    public Integer getPlayerElo() {
        return this.username.equals(this.black)? this.blackElo : this.whiteElo;
    }

    public Integer getOpponentElo() {
        return !this.username.equals(this.black)? this.blackElo : this.whiteElo;
    }

    public Integer getPlayerRatingDiff() {
        return this.username.equals(this.black)? this.blackEloDiff : this.whiteEloDiff;
    }

    public Integer getOpponentRatingDiff() {
        return !this.username.equals(this.black)? this.blackEloDiff : this.whiteEloDiff;
    }


    @Override
    public String toString() {
        return "OpeningInfo{" +
                "userId=" + userId +
                ", gameId='" + gameId + '\'' +
                ", eco='" + eco + '\'' +
                ", openingName='" + openingName + '\'' +
                ", pgn='" + pgn + '\'' +
                ", resultNotation='" + resultNotation + '\'' +
                ", black='" + black + '\'' +
                ", white='" + white + '\'' +
                ", timeControl='" + timeControl + '\'' +
                ", status='" + status + '\'' +
                ", playedAt='" + playedAt + '\'' +
                ", source='" + source + '\'' +
                ", playerColor='" + playerColor + '\'' +
                ", username='" + username + '\'' +
                ", event='" + event + '\'' +
                ", pgnPath='" + pgnPath + '\'' +
                ", pgnConverter=" + pgnConverter +
                ", whiteElo=" + whiteElo +
                ", blackElo=" + blackElo +
                ", whiteEloDiff=" + whiteEloDiff +
                ", blackEloDiff=" + blackEloDiff +
                ", variant='" + variant + '\'' +
                ", termination='" + termination + '\'' +
                '}';
    }
}