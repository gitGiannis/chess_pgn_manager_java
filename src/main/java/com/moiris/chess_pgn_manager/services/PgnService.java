package com.moiris.chess_pgn_manager.services;

import com.moiris.chess_pgn_manager.logic.PGNParser;
import com.moiris.chess_pgn_manager.logic.GameData;
import com.moiris.chess_pgn_manager.services.JsonExportService.GameDTO;
import org.springframework.stereotype.Service;

@Service
public class PgnService {

    // εξάρτηση από το JsonExportService (παράγει το DTO που θα γίνει json)
    private final JsonExportService jsonExportService;

    // Constructor Injection:
    // αυτόματο injection του JsonExportService Bean στον constructor.
    public PgnService(JsonExportService exportService) {
        this.jsonExportService = exportService;
    }

    /** Μέθοδος που δέχεται το pgn σαν text (String) και επιστρέφει τα δεδομένα της παρτίδας ως GameDTO */
    public GameDTO parsePgn(String pgn) {

        // PGNParser για την επεξεργασία του PGN που στέλνει ο user
        PGNParser parser = new PGNParser();
        // ανάλυση του pgn και αποθήκευση εσωτερικά στον parser
        parser.parseText(pgn);

        // ανάκτηση του GameData που δημιουργήθηκε
        GameData gd = parser.getGameData();

        // μετατροπή του GameData σε GameDTO μέσω του JsonExportService.
        GameDTO export = jsonExportService.exportToDTO(gd);

        return export;
    }
}
