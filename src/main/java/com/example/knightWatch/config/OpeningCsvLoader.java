package com.example.knightWatch.config;

import com.example.knightWatch.model.Opening;
import com.example.knightWatch.repository.OpeningRepository;
import com.example.knightWatch.util.PgnToLtreeConverter;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


@Component
public class OpeningCsvLoader implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(OpeningCsvLoader.class);

    @Autowired
    private OpeningRepository openingRepository;

    @Autowired
    private PgnToLtreeConverter pgnConverter;

    @Value("${spring.jpa.hibernate.ddl-auto:none}")
    private String ddlAuto;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!ddlAuto.equals("create") && !ddlAuto.equals("create-drop")) {
            log.info("Skipping CSV load — ddl-auto is '{}'", ddlAuto);
            return;
        }

        loadOpenings();
    }

    private void loadOpenings() {
        try (
                InputStream is = getClass().getResourceAsStream("/data/openings.csv")
        ) {
            assert is != null;
            try (CSVReader csv = new CSVReader(new InputStreamReader(is))
            ) {
                csv.skip(1);

                List<Opening> openings = csv.readAll().stream()
                        .map(this::toOpening)
                        .toList();

                openingRepository.saveAll(openings);
                log.info("Loaded {} openings", openings.size());

            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load openings CSV", e);
        }
    }

    private Opening toOpening(String[] cols) {
        // CSV columns: eco_volume, eco, name, pgn, uci, epd
        String pgn = cols[3];

        Opening o = new Opening();
        o.setEcoVolume(cols[0]);
        o.setEco(cols[1]);
        o.setName(cols[2]);
        o.setPgn(pgn);
        o.setUci(cols[4]);
        o.setEpd(cols[5]);
        o.setPgnPath(pgnConverter.convert(pgn));
        return o;
    }
}