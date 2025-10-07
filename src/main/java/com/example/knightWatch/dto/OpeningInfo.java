package com.example.knightWatch.dto;

public class OpeningInfo {
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


    public OpeningInfo(String gameId, String eco, String openingName, String pgn, String resultNotation, String black, String white, String timeControl, String status, String playedAt, String source) {
        if(source.equals("chesscom")) {
            this.gameId = gameId.substring(gameId.lastIndexOf('/') + 1);
            this.openingName = trimOpeningName(openingName.substring(openingName.lastIndexOf("/") + 1));
            this.status = status.substring(status.lastIndexOf(' ') + 1);
        } else {
            this.gameId = gameId;
            this.openingName = openingName;
            this.status = status;
        }
        this.eco = eco;
        this.pgn = pgn;
        this.resultNotation = resultNotation;
        this.black = black;
        this.white = white;
        this.timeControl = timeControl;
        this.playedAt = playedAt;
        this.source = source;
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

    public String trimOpeningName(String openingName) {
        if (openingName == null || openingName.equals("Undefined")) {
            return openingName;
        }

        int gameIndex = openingName.indexOf("-Game");
        int defenseIndex = openingName.indexOf("-Defense");
        int openingIndex = openingName.indexOf("-Opening");

        // Find the earliest occurrence
        int cutoffIndex = -1;

        if (gameIndex != -1) {
            cutoffIndex = gameIndex + 5; // Length of "-Game"
        }
        if (defenseIndex != -1 && (cutoffIndex == -1 || defenseIndex < cutoffIndex - 5)) {
            cutoffIndex = defenseIndex + 8; // Length of "-Defense"
        }
        if (openingIndex != -1 && (cutoffIndex == -1 || openingIndex < cutoffIndex - 8)) {
            cutoffIndex = openingIndex + 8; // Length of "-Opening"
        }

        String result;
        if (cutoffIndex != -1) {
            result = openingName.substring(0, cutoffIndex);
        } else {
            result = openingName;
        }

        return result.replace("-", " ");
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
}