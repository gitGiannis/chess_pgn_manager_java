package com.moiris.chess_pgn_manager.controllers;

import com.moiris.chess_pgn_manager.services.JsonExportService.GameDTO;
import com.moiris.chess_pgn_manager.services.PgnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/pgn")
public class PgnController {

    private final PgnService pgnService;

    // autowired dependency
    public PgnController(PgnService pgnService) {
        this.pgnService = pgnService;
    }

    @PostMapping("/parse")
    public ResponseEntity<?> parsePgn(@RequestBody String pgn) {

        // service used to create the DTO that is then sent to the user
        GameDTO dto = pgnService.parsePgn(pgn);

        return ResponseEntity.ok(dto);
    }
}
