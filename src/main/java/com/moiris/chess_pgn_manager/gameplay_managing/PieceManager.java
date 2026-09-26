package com.moiris.chess_pgn_manager.gameplay_managing;

import com.moiris.chess_pgn_manager.pieces.*;
import com.moiris.chess_pgn_manager.pojos.Piece;

import java.util.ArrayList;

public class PieceManager {
    public King wKing;
    public King bKing;

    public ArrayList<Piece> wQueens;
    public ArrayList<Piece> bQueens;

    public ArrayList<Piece> wBishops;
    public ArrayList<Piece> bBishops;

    public ArrayList<Piece> wKnights;
    public ArrayList<Piece> bKnights;

    public ArrayList<Piece> wRooks;
    public ArrayList<Piece> bRooks;

    public ArrayList<Piece> wPawns;
    public ArrayList<Piece> bPawns;

    public ArrayList<Piece> capturedPieces;

    public PieceManager(){
        wKing = new King("e1", "K");
        bKing = new King("e8", "k");

        wQueens = new ArrayList<>();
        wQueens.add(new Queen("d1", "Q"));
        bQueens = new ArrayList<>();
        bQueens.add(new Queen("d8", "q"));

        wBishops = new ArrayList<>();
        wBishops.add(new Bishop("c1", "B"));
        wBishops.add(new Bishop("f1", "B"));

        bBishops = new ArrayList<>();
        bBishops.add(new Bishop("c8", "b"));
        bBishops.add(new Bishop("f8", "b"));

        wKnights = new ArrayList<>();
        wKnights.add(new Knight("b1", "N"));
        wKnights.add(new Knight("g1", "N"));

        bKnights = new ArrayList<>();
        bKnights.add(new Knight("b8", "n"));
        bKnights.add(new Knight("g8", "n"));

        wRooks = new ArrayList<>();
        wRooks.add(new Rook("a1", "R"));
        wRooks.add(new Rook("h1", "R"));

        bRooks = new ArrayList<>();
        bRooks.add(new Rook("a8", "r"));
        bRooks.add(new Rook("h8", "r"));

        wPawns = new ArrayList<>();
        wPawns.add(new Pawn("a2", "P"));
        wPawns.add(new Pawn("b2", "P"));
        wPawns.add(new Pawn("c2", "P"));
        wPawns.add(new Pawn("d2", "P"));
        wPawns.add(new Pawn("e2", "P"));
        wPawns.add(new Pawn("f2", "P"));
        wPawns.add(new Pawn("g2", "P"));
        wPawns.add(new Pawn("h2", "P"));

        bPawns = new ArrayList<>();
        bPawns.add(new Pawn("a7", "p"));
        bPawns.add(new Pawn("b7", "p"));
        bPawns.add(new Pawn("c7", "p"));
        bPawns.add(new Pawn("d7", "p"));
        bPawns.add(new Pawn("e7", "p"));
        bPawns.add(new Pawn("f7", "p"));
        bPawns.add(new Pawn("g7", "p"));
        bPawns.add(new Pawn("h7", "p"));

        capturedPieces = new ArrayList<>();
    }

    public ArrayList<Piece> getAllPieces(){
        ArrayList<Piece> pieces = new ArrayList<>();
        pieces.add(wKing);
        pieces.add(bKing);
        pieces.addAll(wQueens);
        pieces.addAll(bQueens);
        pieces.addAll(wBishops);
        pieces.addAll(bBishops);
        pieces.addAll(wKnights);
        pieces.addAll(bKnights);
        pieces.addAll(wRooks);
        pieces.addAll(bRooks);
        pieces.addAll(wPawns);
        pieces.addAll(bPawns);

        return pieces;
    }

    public void capturePieceByPosition(String position){
        for (Piece p : getAllPieces()){
            if (p.getPosition().equals(position)){
                capturedPieces.add(p);
                wPawns.remove(p);
                bPawns.remove(p);
                wKnights.remove(p);
                bKnights.remove(p);
                wBishops.remove(p);
                bBishops.remove(p);
                wRooks.remove(p);
                bRooks.remove(p);
                wQueens.remove(p);
                bQueens.remove(p);
            }
        }
    }

    public String toString(){
        return getAllPieces().toString();
    }
}
