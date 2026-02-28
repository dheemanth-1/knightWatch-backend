package com.example.knightWatch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DatabaseIndexInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseIndexInitializer.class);

    @Autowired
    private DataSource dataSource;

    @Value("${spring.jpa.hibernate.ddl-auto:none}")
    private String ddlAuto;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!ddlAuto.equals("create") && !ddlAuto.equals("create-drop")) {
            log.info("Skipping index creation — ddl-auto is '{}'", ddlAuto);
            return;
        }
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            log.info("Creating database indexes...");

            stmt.execute("CREATE EXTENSION IF NOT EXISTS ltree");


            stmt.execute("""
                CREATE INDEX IF NOT EXISTS idx_opening_pgn_path_gist
                ON opening USING GIST (pgn_path)
                """);


            stmt.execute("""
                CREATE INDEX IF NOT EXISTS idx_opening_path_depth
                ON opening (nlevel(pgn_path) DESC)
                """);


            stmt.execute("""
                CREATE INDEX IF NOT EXISTS idx_game_pgn_path_gist
                ON local_game USING GIST (pgn_path)
                """);


            stmt.execute("""
                CREATE INDEX IF NOT EXISTS idx_game_user_id
                ON local_game (local_profile_id)
                """);

            log.info("Database indexes created successfully");

        } catch (Exception e) {
            log.error("Failed to create indexes: {}", e.getMessage(), e);
            throw e;
        }
    }
}