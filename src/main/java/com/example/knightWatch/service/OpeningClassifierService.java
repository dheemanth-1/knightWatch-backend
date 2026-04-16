package com.example.knightWatch.service;

import com.example.knightWatch.model.Opening;
import com.example.knightWatch.util.PgnToLtreeConverter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OpeningClassifierService {

//    @Autowired
//    private PgnToLtreeConverter converter;

    @PersistenceContext
    private EntityManager em;

    public Optional<Opening> classifyGame(String pgnPath) {
        if (pgnPath == null || pgnPath.isBlank()) {
            return Optional.empty();
        }

        // String path = converter.convert(pgn);

//        if (path == null || path.isBlank()) {
//            return Optional.empty();
//        }

        try {
            Opening opening = (Opening) em.createNativeQuery("""
                SELECT * FROM opening
                WHERE pgn_path @> CAST(:path AS ltree)
                ORDER BY nlevel(pgn_path) DESC
                LIMIT 1
                """, Opening.class)
                    .setParameter("path", pgnPath)
                    .getSingleResult();

            return Optional.of(opening);

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}