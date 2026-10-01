package com.moiris.chess_pgn_manager.pgn_parsing;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PGNParserTest {

    @Test
    void parsesMultipleGamesAndIgnoresPgnMetadataAndAnnotations() throws Exception {
        Path pgnFile = Files.createTempFile("games", ".txt");
        Files.writeString(pgnFile, """
                [Event "Game 1"]
                [Site "Local"]

                1. e4 e5 2. Nf3 Nc6 {comment} 3. Bb5 a6 (3... Nf6) 1-0

                [Event "Game 2"]

                1. d4 d5 2. c4 e6 *
                """);
        /*
        Map<Integer, List<String>> games = new PGNParser().parse(pgnFile);

        assertEquals(
                Map.of(
                        1, List.of("e4", "e5", "Nf3", "Nc6", "Bb5", "a6"),
                        2, List.of("d4", "d5", "c4", "e6")
                ),
                games
        );

         */
    }
}
