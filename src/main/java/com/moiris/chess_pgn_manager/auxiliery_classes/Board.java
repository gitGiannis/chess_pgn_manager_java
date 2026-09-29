package com.moiris.chess_pgn_manager.auxiliery_classes;

import com.moiris.chess_pgn_manager.pojos.Files;
import com.moiris.chess_pgn_manager.pojos.Piece;
import com.moiris.chess_pgn_manager.pojos.Ranks;

import java.util.HashMap;

/** Represents the chess board as a mapping between board positions and pieces.
 *  The board extends {@link HashMap}, where the key is a position such as
 *  "e4" and the value is the {@link Piece} occupying that position.
 *  The board is populated and updated using the pieces managed by a
 *  {@link PieceManager}.
 */
public class Board extends HashMap<String, Piece> {
    private final PieceManager pm;
    /** Creates a new Board associated with the specified PieceManager.
     *  @param pm the PieceManager used to retrieve and manage the pieces on the board */
    public Board(PieceManager pm){
        this.pm = pm;
        updateBoard();
    }

    /** Updates the board so that it reflects the current positions of all
     * pieces managed by the PieceManager.
     * Any existing board contents are cleared before the pieces are added
     * using their current positions as keys. */
    public void updateBoard(){
        this.clear();
        for (Piece p : pm.getAllPieces()){
            this.put(p.getPosition(), p);
        }
    }
    /** Prints the current state of the board to the standard output.
     * Empty squares are represented by ".", while occupied squares are
     * represented by their corresponding Piece. Ranks are printed from
     * highest to lowest, followed by the file labels. */
    public void printSelf(){
        for (String r : Ranks.ranksReversed){
            for (String f : Files.files){
                System.out.print(" ");
                Piece p = this.get(f + r);
                if (p != null){ System.out.print(p); }
                else { System.out.print("."); }
                System.out.print("  ");
            }
            System.out.println("[" + r + "]");
        }
        for (String f : Files.files){
            System.out.print("[" + f + "] ");
        }
        System.out.println("\n-----------------");
    }
    /** Updates the board and then prints its current state to the standard output. */
    public void updateAndPrintSelf() {
        this.updateBoard();
        this.printSelf();
    }
}
