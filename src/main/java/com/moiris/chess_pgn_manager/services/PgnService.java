package com.moiris.chess_pgn_manager.services;

import com.moiris.chess_pgn_manager.logic.PGNParser;
import com.moiris.chess_pgn_manager.logic.GameData;
import com.moiris.chess_pgn_manager.services.JsonExportService.GameDTO;
import org.springframework.stereotype.Service;

@Service
public class PgnService {

    // dependency from JsonExportService (creates the DTO that will be turned into json)
    private final JsonExportService jsonExportService;

    // Constructor Injection:
    // auto-injection of JsonExportService Bean in the constructor
    public PgnService(JsonExportService exportService) {
        this.jsonExportService = exportService;
    }

    /** Method that uses the pgn as text (String) and returns the gameplay data as GameDTO */
    public GameDTO parsePgn(String pgn) {

        // PGNParser for parsing user PGN
        PGNParser parser = new PGNParser();

        // pgn is parsed and stored in the parser as GameData
        parser.parseText(pgn);
        GameData gd = parser.getGameData();

        // GameData turns to GameDTO through the JsonExportService.
        GameDTO export = jsonExportService.exportToDTO(gd);

        return export;
    }
}
