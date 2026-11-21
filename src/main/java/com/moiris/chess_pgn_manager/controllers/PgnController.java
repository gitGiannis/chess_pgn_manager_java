package com.moiris.chess_pgn_manager.controllers;

import com.moiris.chess_pgn_manager.export.jsonExporter;
import com.moiris.chess_pgn_manager.parsing.PGNParser;
import com.moiris.chess_pgn_manager.pojos.GameData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/pgn")
public class PgnController {

    @PostMapping("/parse")
    public ResponseEntity<?> parsePgn(@RequestBody String pgn) {

        PGNParser parser = new PGNParser();
        parser.parseText(pgn);
        GameData gd = parser.getGameData();

        return ResponseEntity.ok(jsonExporter.toDTO(gd));
    }
}
