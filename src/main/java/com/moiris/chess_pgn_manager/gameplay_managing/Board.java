package com.moiris.chess_pgn_manager.gameplay_managing;

import com.moiris.chess_pgn_manager.pojos.Files;
import com.moiris.chess_pgn_manager.pojos.Piece;
import com.moiris.chess_pgn_manager.pojos.Ranks;

import java.util.HashMap;


public class Board extends HashMap<String, Piece> {
    private final PieceManager pm;

    public Board(PieceManager pm){
        this.pm = pm;
        updateBoard();
    }


    public void updateBoard(){
        this.clear();
        for (Piece p : pm.getAllPieces()){
            this.put(p.getPosition(), p);
        }
    }

    public void printSelf(){
        for (String r : Ranks.ranksReversed){
            for (String f : Files.files){
                Piece p = this.get(f + r);
                if (p != null){ System.out.print(p); }
                else { System.out.print("."); }
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println("-----------------");
    }

    public void updateAndPrintSelf() {
        this.updateBoard();
        this.printSelf();
    }
}
