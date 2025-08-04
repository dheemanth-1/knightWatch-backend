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


    public OpeningInfo(String gameId, String eco, String openingName, String pgn, String resultNotation, String black, String white, String timeControl) {
        this.gameId = gameId;
        this.eco = eco;
        this.openingName = openingName;
        this.pgn = pgn;
        this.resultNotation = resultNotation;
        this.black = black;
        this.white = white;
        this.timeControl = timeControl;
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

    // Getters and setters
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
}