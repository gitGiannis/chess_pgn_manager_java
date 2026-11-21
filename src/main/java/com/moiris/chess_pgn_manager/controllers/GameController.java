package com.moiris.chess_pgn_manager.controllers;

import com.moiris.chess_pgn_manager.services.JsonExportService.GameDTO;
import com.moiris.chess_pgn_manager.logic.PGNParser;
import com.moiris.chess_pgn_manager.services.PgnService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.nio.file.Path;

/**
 * GET /api/pgn/{file}          -> list of games (headers + number of half-moves)
 * GET /api/pgn/{file}/{index}  -> one full game (0-based index) with a position after every half-move

 * Requires spring-boot-starter-web and SpringApplication.run(...) enabled in main().
 */
@RestController
@RequestMapping(path="/api/pgn", produces = "application/json")
@CrossOrigin // allows a frontend on another port (e.g. localhost:3000) while developing
public class GameController {

    private final PgnService pgnService;

    public GameController(PgnService pgnService) {
        this.pgnService = pgnService;
    }

    private static final Path PGN_DIR = Path.of("src/main/resources/pgn_files").toAbsolutePath().normalize();

    @GetMapping("/{file}/{index}")
    public GameDTO getGame(@PathVariable String file, @PathVariable int index) {
        PGNParser parser = load(file);
        if (index < 0 || index >= parser.getGameDataList().size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such game");
        }
        return null;
    }

    private PGNParser load(String file) {
        Path path = PGN_DIR.resolve(file + ".pgn").normalize();
        if (!path.startsWith(PGN_DIR) || !path.toFile().isFile()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such PGN file");
        }
        PGNParser parser = new PGNParser();
        try {
            parser.parseFile(path.toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return parser;
    }
}
