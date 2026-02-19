package com.example.knightWatch.model;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLLTreeType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "opening")
public class Opening {

    @Id
    @Column(name="opening_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long openingId;

    @Column(name="eco_volume")
    private String ecoVolume;

    private String eco;

    private String name;

    private String pgn;

    private String uci;

    private String epd;

    @Type(PostgreSQLLTreeType.class)
    @Column(name = "pgn_path", columnDefinition = "ltree")
    private String pgnPath;


    public Opening() {

    }

    public Opening(String ecoVolume, String eco, String name, String pgn, String uci, String epd, String pgnPath) {
        this.ecoVolume = ecoVolume;
        this.eco = eco;
        this.name = name;
        this.pgn = pgn;
        this.uci = uci;
        this.epd = epd;
        this.pgnPath = pgnPath;
    }

    public Long getOpeningId() {
        return openingId;
    }

    public void setOpeningId(Long openingId) {
        this.openingId = openingId;
    }

    public String getEcoVolume() {
        return ecoVolume;
    }

    public void setEcoVolume(String ecoVolume) {
        this.ecoVolume = ecoVolume;
    }

    public String getEco() {
        return eco;
    }

    public void setEco(String eco) {
        this.eco = eco;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPgn() {
        return pgn;
    }

    public void setPgn(String pgn) {
        this.pgn = pgn;
    }

    public String getUci() {
        return uci;
    }

    public void setUci(String uci) {
        this.uci = uci;
    }

    public String getEpd() {
        return epd;
    }

    public void setEpd(String epd) {
        this.epd = epd;
    }

    public String getPgnPath() {
        return pgnPath;
    }

    public void setPgnPath(String pgnPath) {
        this.pgnPath = pgnPath;
    }
}
