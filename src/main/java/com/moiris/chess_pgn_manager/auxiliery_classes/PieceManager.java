package com.moiris.chess_pgn_manager.auxiliery_classes;

import com.moiris.chess_pgn_manager.pieces.*;
import com.moiris.chess_pgn_manager.pojos.Piece;

import java.util.ArrayList;

import static java.lang.System.exit;

/**
 * Manages all pieces currently involved in a chess game.
 * <p>
 * The PieceManager stores the kings separately and maintains lists for
 * queens, bishops, knights, rooks, and pawns for both players. It also
 * keeps track of captured pieces.
 */
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

    /**
     * Creates a new PieceManager and initializes all pieces to their
     * standard starting positions in a chess game.
     */
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

    /**
     * Returns a list containing all pieces currently active on the board.
     * <p>
     * The returned list includes both kings and all queens, bishops,
     * knights, rooks, and pawns belonging to both players.
     *
     * @return a list containing all active pieces
     */
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

    /**
     * Returns a list containing all enemy pieces that are able to pin an enemy piece to the king at a straight line.
     * <p>
     * The returned list includes all queens and rooks belonging to the opponent.
     *
     * @return a list containing the pieces listed above
     */
    public ArrayList<Piece> getLinePinners(boolean whiteToPlay){
        ArrayList<Piece> pieces = new ArrayList<>();

        if (whiteToPlay) {
            pieces.addAll(bQueens);
            pieces.addAll(bRooks);
        }
        else{
            pieces.addAll(wQueens);
            pieces.addAll(wRooks);
        }

        return pieces;
    }

    /**
     * Returns a list containing all enemy pieces that are able to pin an enemy piece to the king at a diagonal.
     * <p>
     * The returned list includes all queens and bishops belonging to the opponent.
     *
     * @return a list containing the pieces listed above
     */
    public ArrayList<Piece> getDiagonalPinners(boolean whiteToPlay){
        ArrayList<Piece> pieces = new ArrayList<>();

        if (whiteToPlay) {
            pieces.addAll(bQueens);
            pieces.addAll(bBishops);
        }
        else{
            pieces.addAll(wQueens);
            pieces.addAll(wBishops);
        }

        return pieces;
    }

    /**
     * Returns a string representation of all currently active pieces.
     *
     * @return a string containing all active pieces
     */
    public String toString(){
        return getAllPieces().toString();
    }

    public void captureWhitePieceByPosition(String position) {
        for (Piece p : getWhitePieces()){
            if (p.getPosition().equals(position)){
                capturedPieces.add(p);
                if (p.getClass() == Pawn.class){
                    wPawns.remove(p);
                }
                else if (p.getClass() == Knight.class){
                    wKnights.remove(p);
                }
                else if (p.getClass() == Bishop.class){
                    wBishops.remove(p);
                }
                else if (p.getClass() == Rook.class){
                    wRooks.remove(p);
                }
                else if (p.getClass() == Queen.class){
                    wQueens.remove(p);
                }
                else{
                    //ERROR HANDLING
                    System.out.println("ERROR REMOVING WHITE PIECE");
                    exit(0);
                }
            }
        }
    }

    public void captureBlackPieceByPosition(String position) {
        for (Piece p : getBlackPieces()){
            if (p.getPosition().equals(position)){
                capturedPieces.add(p);
                if (p.getClass() == Pawn.class){
                    bPawns.remove(p);
                }
                else if (p.getClass() == Knight.class){
                    bKnights.remove(p);
                }
                else if (p.getClass() == Bishop.class){
                    bBishops.remove(p);
                }
                else if (p.getClass() == Rook.class){
                    bRooks.remove(p);
                }
                else if (p.getClass() == Queen.class){
                    bQueens.remove(p);
                }
                else{
                    //ERROR HANDLING
                    System.out.println("ERROR REMOVING BLACK PIECE");
                    exit(0);
                }
            }
        }
    }

    private ArrayList<Piece> getBlackPieces() {
        ArrayList<Piece> blackPieces = new ArrayList<>();

        blackPieces.addAll(bPawns);
        blackPieces.addAll(bBishops);
        blackPieces.addAll(bKnights);
        blackPieces.addAll(bQueens);
        blackPieces.addAll(bRooks);
        blackPieces.add(bKing);

        return blackPieces;
    }

    private ArrayList<Piece> getWhitePieces() {
        ArrayList<Piece> whitePieces = new ArrayList<>();

        whitePieces.addAll(wPawns);
        whitePieces.addAll(wBishops);
        whitePieces.addAll(wKnights);
        whitePieces.addAll(wQueens);
        whitePieces.addAll(wRooks);
        whitePieces.add(wKing);

        return whitePieces;
    }
}

