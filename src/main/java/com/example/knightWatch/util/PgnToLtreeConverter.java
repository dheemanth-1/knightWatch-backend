package com.example.knightWatch.util;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class PgnToLtreeConverter {

    /**
     * Converts "1. e4 e5 2. Nf3 Nc6 3. Bb5 a6" -> "e4.e5.Nf3.Nc6.Bb5.a6"
     */
    public String convert(String pgn) {
        if (pgn == null || pgn.isBlank()) return null;

        return Arrays.stream(pgn.trim().split("\\s+"))
                .filter(token -> !token.matches("\\d+\\.+"))  // remove "1." "2." etc
                .filter(token -> !token.isBlank())
                .map(this::sanitizeLabel)
                .filter(label -> !label.isBlank())
                .collect(Collectors.joining("."));
    }

    /**
     * ltree labels: letters, digits, underscores only. Must start with letter/underscore.
     * e4      -> e4       (pawn to e4 - remains same)
     * Nf3     -> Nf3      (knight to f3 - remains same)
     * O-O     -> O_O      (castling, replace dash)
     * O-O-O   -> O_O_O    (queenside castling)
     * Nf3+    -> Nf3      (strip check symbol)
     * Qxd5#   -> Qxd5     (strip checkmate symbol)
     * e8=Q    -> e8_Q     (promotion)
     */
    private String sanitizeLabel(String token) {
        return token
                .replaceAll("[+#!?]", "")       // strip annotation symbols
                .replaceAll("=", "_")            // promotion e.g. e8=Q -> e8_Q
                .replaceAll("-", "_")            // castling O-O -> O_O
                .replaceAll("[^a-zA-Z0-9_]", "") // strip anything else invalid
                .replaceAll("^[0-9]+", "");      // labels can't start with a digit
    }
}