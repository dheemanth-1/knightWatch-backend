package com.example.knightWatch.dto.nodetrav;

import java.util.List;

public class OpeningNodeDTO {
    private Long openingId;
    private String name;
    private String eco;
    private String pgnPath;
    private Integer depth;
    private Integer gameCount;
    private OpeningStatsDTO stats;
    private List<OpeningNodeDTO> children;

    public OpeningNodeDTO() {
    }

    public OpeningNodeDTO(Long openingId, String name, String eco, String pgnPath, Integer depth, Integer gameCount, OpeningStatsDTO stats, List<OpeningNodeDTO> children) {
        this.openingId = openingId;
        this.name = name;
        this.eco = eco;
        this.pgnPath = pgnPath;
        this.depth = depth;
        this.gameCount = gameCount;
        this.stats = stats;
        this.children = children;
    }

    public Long getOpeningId() {
        return openingId;
    }

    public void setOpeningId(Long openingId) {
        this.openingId = openingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEco() {
        return eco;
    }

    public void setEco(String eco) {
        this.eco = eco;
    }

    public String getPgnPath() {
        return pgnPath;
    }

    public void setPgnPath(String pgnPath) {
        this.pgnPath = pgnPath;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public Integer getGameCount() {
        return gameCount;
    }

    public void setGameCount(Integer gameCount) {
        this.gameCount = gameCount;
    }

    public OpeningStatsDTO getStats() {
        return stats;
    }

    public void setStats(OpeningStatsDTO stats) {
        this.stats = stats;
    }

    public List<OpeningNodeDTO> getChildren() {
        return children;
    }

    public void setChildren(List<OpeningNodeDTO> children) {
        this.children = children;
    }
}
