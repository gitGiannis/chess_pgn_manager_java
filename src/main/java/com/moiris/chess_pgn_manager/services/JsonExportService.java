package com.moiris.chess_pgn_manager.services;

import com.moiris.chess_pgn_manager.logic.Board;
import com.moiris.chess_pgn_manager.logic.PieceManager;
import com.moiris.chess_pgn_manager.logic.exceptions.IllegalPlyException;
import com.moiris.chess_pgn_manager.logic.MoveDecoder;
import com.moiris.chess_pgn_manager.logic.pojos.Files;
import com.moiris.chess_pgn_manager.logic.GameData;
import com.moiris.chess_pgn_manager.logic.pojos.Piece;
import com.moiris.chess_pgn_manager.logic.pojos.Ranks;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Replays a {@link GameData} with the existing MoveParser and converts it into
 * plain DTOs that Jackson can serialize straight to JSON.
 */
@Service
public class JsonExportService {

    /** record that stores the Header info of a game */
    public record Headers(String event,
                          String site,
                          String date,
                          String round,
                          String white,
                          String black,
                          String result) {}

    /** One half-move with some extra info for the front end. */
    public record Ply(int plyNo,
                      String color,
                      String move,
                      String fen,
                      Footer footer) {}

    /** Footer with auxiliary information about the game. */
    public record Footer(boolean capture,
                         boolean check,
                         boolean brilliant,
                         boolean blunder,
                         boolean error,
                         String capturedPieces) {}

    /** Data Transfer Object to be sent through the API */
    public record GameDTO(Headers headers, String initialFen, List<Ply> plies) {}

    /** Static method that creates the Header info of a game */
    public static Headers headersOf(GameData gd) {
        return new Headers(
                gd.getEvent(),
                gd.getSite(),
                gd.getDate(),
                gd.getRound(),
                gd.getWhite(),
                gd.getBlack(),
                gd.getResult());}

    /** Static method that creates the DTO for the API */
    public GameDTO exportToDTO(GameData gd) {
        // MoveParser consumes the game through the *Index fields, so reset them
        // to allow exporting the same GameData more than once.

        PieceManager pm = new PieceManager();
        Board board = new Board(pm);
        MoveDecoder mp = new MoveDecoder(gd, pm, board);

        String initialFen = createFEN(board);

        List<Ply> plies = new ArrayList<>();

        int total = gd.totalPliesCount();

        for (int i = 0; i < total; i++) {
            boolean error = false;

            try{
                mp.parseNextPly();
            }
            catch (IllegalPlyException e) {
                // ply could not be parsed due to faulty mistake and parsing stops
                error = true;
            }

            boolean white = (i % 2 == 0);
            String san = white ? gd.getWhitePlies().get(i / 2) : gd.getBlackPlies().get(i / 2);

            plies.add(new Ply(i/2 + 1,
                    white ? "white" : "black",
                    san,
                    createFEN(board),
                    new Footer(mp.capture, mp.check, mp.brilliant, mp.blunder, error, capturedPieces(pm))));

            if (error) break;
        }

        return new GameDTO(headersOf(gd), initialFen, plies);
    }

    /** screenshot of the chess board for every ply as FEN, e.g. "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR". */
    static String createFEN(Board board) {
        StringBuilder sb = new StringBuilder();
        for (String r : Ranks.ranksReversed) {
            int empty = 0;
            for (String f : Files.files) {
                Piece p = board.get(f + r);
                if (p == null) {
                    empty++;
                } else {
                    if (empty > 0) { sb.append(empty); empty = 0; }
                    sb.append(p.getName());
                }
            }
            if (empty > 0) sb.append(empty);
            if (!r.equals("1")) sb.append('/');
        }
        return sb.toString();
    }

    /** screenshot of the captured pieces, e.g. "PpbN". */
    static String capturedPieces(PieceManager pieceManager){
        StringBuilder sb = new StringBuilder();
        for (Piece p : pieceManager.capturedPieces){
            sb.append(p.getName());
        }
        return sb.toString();
    }
}
