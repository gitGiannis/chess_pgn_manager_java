package com.moiris.chess_pgn_manager.parsing;

import com.moiris.chess_pgn_manager.gameplay_managing.Board;
import com.moiris.chess_pgn_manager.gameplay_managing.ValidMoveChecker;
import com.moiris.chess_pgn_manager.pieces.*;
import com.moiris.chess_pgn_manager.pojos.Files;
import com.moiris.chess_pgn_manager.pojos.GameData;
import com.moiris.chess_pgn_manager.gameplay_managing.PieceManager;
import com.moiris.chess_pgn_manager.pojos.Piece;
import com.moiris.chess_pgn_manager.pojos.Ranks;

import java.util.ArrayList;

public class MoveParser {
    private final GameData gd;
    private final PieceManager pm;
    private final ValidMoveChecker vmc;
    private Board board;

    private boolean capture = false;
    private String pawnPromotion = "";
    private boolean check = false;
    private boolean brilliant = false;
    private boolean blunder = false;

    private boolean whiteToPlay = false;
    private String move;

    public MoveParser(GameData gameData, PieceManager pieceManager, ValidMoveChecker validMoveChecker) {
        this.gd = gameData;
        this.pm = pieceManager;
        this.vmc = validMoveChecker;
    }

    public void parseNextMove(){
        whiteToPlay = !whiteToPlay;

        if (whiteToPlay) { move = gd.getNextWhiteHalfMove(); }
        else{ move = gd.getNextBlackHalfMove(); }


        if (whiteToPlay) System.out.println("White -> move=" + move);
        else System.out.println("Black -> move=" + move);


        if (move==null){
            // END OF GAME
            return;
        }

        capture = move.contains("x");
        move = move.replace("x", "");

        check = move.contains("+") || move.contains("#");
        move = move.replace("+", "");
        move = move.replace("#", "");

        brilliant = move.contains("!");
        move = move.replace("!", "");
        blunder = move.contains("?");
        move = move.replace("?", "");


        if (move.matches("O-O|0-0")){ smallCastle(); return; }
        if (move.matches("O-O-O|0-0-0")){ bigCastle(); return; }

        if (move.matches("K.+")){ handleKing(); return; }
        if (move.matches("Q.+")){ handleQueen(); return; }
        if (move.matches("B.+")){ handleBishop(); return; }
        if (move.matches("N.+")){ handleKnight(); return; }
        if (move.matches("R.+")){ handleRook(); return; }

        handlePawn();
        //String s = "[a-h]\\d|[a-h]x[a-h]\\d|[a-h]\\dx[a-h]\\d";
        //if (move.matches(s)){ pawnMove(); return; }


        // error handling ?

    }

    private void smallCastle(){
        if (whiteToPlay) {
            pm.wKing.move("g1");
            movePieceByPosition(pm.wRooks, "h1", "f1");
        }
        else {
            pm.bKing.move("g8");
            movePieceByPosition(pm.bRooks, "h8", "f8");
        }
    }

    private void bigCastle(){
        if (whiteToPlay) {
            pm.wKing.move("c1");
            movePieceByPosition(pm.wRooks, "a1", "d1");
        }
        else {
            pm.bKing.move("c8");
            movePieceByPosition(pm.bRooks, "a8", "d8");
        }
    }

    private void handlePawn(){

        if (move.contains("=")){
            move = move.replace("=", "");
            pawnPromotion = move.substring(move.length() - 1);
            move = move.replace(pawnPromotion, "");
        }

        if (move.matches("[a-h]\\d")){
            movePawn("", move);
        }
        else {
            movePawn(move.substring(0,1), move.substring(1));
        }
    }

    private void movePawn(String srcFile, String dest){
        ArrayList<Piece> list;
        if (whiteToPlay) list = pm.wPawns; else list = pm.bPawns;

        String destFile = dest.substring(0,1);
        String destRank = dest.substring(1);


        if (srcFile.isEmpty()){

            if (whiteToPlay){
                for (Piece p : list) {
                    if (p.getFile().equals(destFile) && Ranks.getIndex(destRank) - Ranks.getIndex(p.getRank())  == 1){
                        p.move(dest);
                        promotePawn(p);
                        return;
                    }
                }
                for (Piece p : list) {
                    if (p.getFile().equals(destFile) && Ranks.getIndex(destRank) - Ranks.getIndex(p.getRank())  == 2){
                        p.move(dest);
                        promotePawn(p);
                        return;
                    }
                }
            }
            else{
                for (Piece p : list) {
                    if (p.getFile().equals(destFile) && Ranks.getIndex(destRank) - Ranks.getIndex(p.getRank())  == -1){
                        p.move(dest);
                        promotePawn(p);
                        return;
                    }
                }
                for (Piece p : list) {
                    if (p.getFile().equals(destFile) && Ranks.getIndex(destRank) - Ranks.getIndex(p.getRank())  == -2){
                        p.move(dest);
                        promotePawn(p);
                        return;
                    }
                }
            }
        }

        if (!srcFile.isEmpty()){

            // EN PASSANT NOT ELIGIBLE FOR THIS !!!!!!

            if (capture) { pm.capturePieceByPosition(dest); }

            if (whiteToPlay){
                for (Piece p : list) {
                    if ((Files.getIndex(destFile) - Files.getIndex(p.getFile()) == 1
                            || Files.getIndex(destFile) - Files.getIndex(p.getFile()) == -1)
                            && Ranks.getIndex(destRank) - Ranks.getIndex(p.getRank())  == 1){
                        p.move(dest);
                        promotePawn(p);
                        return;
                    }
                }
            }
            else{
                for (Piece p : list) {
                    if ((Files.getIndex(destFile) - Files.getIndex(p.getFile()) == 1
                            || Files.getIndex(destFile) - Files.getIndex(p.getFile()) == -1)
                            && Ranks.getIndex(destRank) - Ranks.getIndex(p.getRank())  == -1){
                        p.move(dest);
                        promotePawn(p);
                        return;
                    }
                }
            }
        }

    }

    // TO COMPLETE
    private void promotePawn(Piece p){
        if (pawnPromotion.isEmpty()) return;

        Piece newPiece;
        pm.capturePieceByPosition(p.getPosition());
        
        if (whiteToPlay){
            switch (pawnPromotion){
                case "Q":
                    newPiece = new Queen(p.getPosition(), "Q");
                    pm.wQueens.add(newPiece);
                    newPiece.move(p.getPosition());
                    break;

                case "R":
                    newPiece =new Rook(p.getPosition(), "R");
                    pm.wRooks.add(newPiece);
                    newPiece.move(p.getPosition());
                    break;

                case "N":
                    newPiece = new Knight(p.getPosition(), "N");
                    pm.wKnights.add(newPiece);
                    newPiece.move(p.getPosition());
                    break;

                case "B":
                    newPiece = new Bishop(p.getPosition(), "B");
                    pm.wBishops.add(newPiece);
                    newPiece.move(p.getPosition());
                    break;

                default:
                    break;
            }
        }
        else{
            switch (pawnPromotion){
                case "Q":
                    newPiece = new Queen(p.getPosition(), "q");
                    pm.bQueens.add(newPiece);
                    newPiece.move(p.getPosition());
                    break;

                case "R":
                    newPiece = new Rook(p.getPosition(), "r");
                    pm.bRooks.add(newPiece);
                    newPiece.move(p.getPosition());
                    break;

                case "N":
                    newPiece = new Knight(p.getPosition(), "n");
                    pm.bKnights.add(newPiece);
                    newPiece.move(p.getPosition());
                    break;

                case "B":
                    newPiece = new Bishop(p.getPosition(), "b");
                    pm.bBishops.add(newPiece);
                    newPiece.move(p.getPosition());
                    break;

                default:
                    break;
            }
        }
    }


    private void handleBishop(){
        move = move.replace("B", "");

        if (move.matches("[a-h]\\d")){
            moveBishop("", "", move);
        }
        else if(move.matches("[a-h][a-h]\\d")){
            moveBishop(move.substring(0,1), "", move.substring(1));
        }
        else if(move.matches("\\d[a-h]\\d")){
            moveBishop("", move.substring(0,1), move.substring(1));
        }
        else{
            moveBishop(move.substring(0,1), move.substring(1,2), move.substring(2));
        }

    }

    private void moveBishop(String srcFile, String srcRank, String dest){
        ArrayList<Piece> list;
        if (whiteToPlay) list = pm.wBishops; else list = pm.bBishops;

        if (capture) { pm.capturePieceByPosition(dest); }

        if (srcFile.isEmpty() && srcRank.isEmpty()){
            for (Piece p : list){
                if (vmc.validDiagonalMove(p.getPosition(), dest)){
                    p.move(dest);
                    return;
                }
            }
        }
        if (!srcFile.isEmpty() && srcRank.isEmpty()){
            for (Piece p : list){
                if (p.getFile().equals(srcFile) && vmc.validDiagonalMove(p.getPosition(), dest)){
                    p.move(dest);
                    return;
                }
            }
        }
        if (srcFile.isEmpty() && !srcRank.isEmpty()){
            for (Piece p : list){
                if (p.getRank().equals(srcRank) && vmc.validDiagonalMove(p.getPosition(), dest)){
                    p.move(dest);
                    return;
                }
            }
        }
        if (vmc.validDiagonalMove(srcFile+srcRank, dest)){
            movePieceByPosition(list, srcFile+srcRank, dest);
        }
    }

    private void handleRook(){
        move = move.replace("R", "");

        if (move.matches("[a-h]\\d")){
            moveRook("", "", move);
        }
        else if(move.matches("[a-h][a-h]\\d")){
            moveRook(move.substring(0,1), "", move.substring(1));
        }
        else if(move.matches("\\d[a-h]\\d")){
            moveRook("", move.substring(0,1), move.substring(1));
        }
        else{
            moveRook(move.substring(0,1), move.substring(1,2), move.substring(2));
        }
    }

    private void moveRook(String srcFile, String srcRank, String dest){
        ArrayList<Piece> list;
        if (whiteToPlay) list = pm.wRooks; else list = pm.bRooks;

        if (capture) { pm.capturePieceByPosition(dest); }

        if (srcFile.isEmpty() && srcRank.isEmpty()){
            for (Piece p : list){
                if (vmc.validHorizontalOrVerticalMove(p.getPosition(), dest)){
                    p.move(dest);
                    return;
                }
            }
        }
        if (!srcFile.isEmpty() && srcRank.isEmpty()){
            for (Piece p : list){
                if (p.getFile().equals(srcFile) && vmc.validHorizontalOrVerticalMove(p.getPosition(), dest)){
                    p.move(dest);
                    return;
                }
            }
        }
        if (srcFile.isEmpty() && !srcRank.isEmpty()){
            for (Piece p : list){
                if (p.getRank().equals(srcRank) && vmc.validHorizontalOrVerticalMove(p.getPosition(), dest)){
                    p.move(dest);
                    return;
                }
            }
        }
        if (vmc.validHorizontalOrVerticalMove(srcFile+srcRank, dest)){
            movePieceByPosition(list, srcFile+srcRank, dest);
        }
    }

    private void handleQueen(){
        move = move.replace("Q", "");

        if (move.matches("[a-h]\\d")){
            moveQueen("", "", move);
        }
        else if(move.matches("[a-h][a-h]\\d")){
            moveQueen(move.substring(0,1), "", move.substring(1));
        }
        else if(move.matches("\\d[a-h]\\d")){
            moveQueen("", move.substring(0,1), move.substring(1));
        }
        else{
            moveQueen(move.substring(0,1), move.substring(1,2), move.substring(2));
        }
    }

    private void moveQueen(String srcFile, String srcRank, String dest){
        ArrayList<Piece> list;
        if (whiteToPlay) list = pm.wQueens; else list = pm.bQueens;

        if (capture) { pm.capturePieceByPosition(dest); }

        if (srcFile.isEmpty() && srcRank.isEmpty()){
            for (Piece p : list){
                if (vmc.validHorizontalOrVerticalMove(p.getPosition(), dest)
                        || vmc.validDiagonalMove(p.getPosition(), dest)){
                    p.move(dest);
                    return;
                }
            }
        }
        if (!srcFile.isEmpty() && srcRank.isEmpty()){
            for (Piece p : list){
                if (p.getFile().equals(srcFile) &&
                        (vmc.validHorizontalOrVerticalMove(p.getPosition(), dest)
                        || vmc.validDiagonalMove(p.getPosition(), dest))){
                    p.move(dest);
                    return;
                }
            }
        }
        if (srcFile.isEmpty() && !srcRank.isEmpty()){
            for (Piece p : list){
                if (p.getRank().equals(srcRank) &&
                        (vmc.validHorizontalOrVerticalMove(p.getPosition(), dest)
                        || vmc.validDiagonalMove(p.getPosition(), dest))){
                    p.move(dest);
                    return;
                }
            }
        }
        if (vmc.validHorizontalOrVerticalMove(srcFile+srcRank, dest)
                || vmc.validDiagonalMove(srcFile+srcRank, dest)){
            movePieceByPosition(list, srcFile+srcRank, dest);
        }
    }

    private void handleKnight(){
        move = move.replace("N", "");

        if (move.matches("[a-h]\\d")){
            moveKnight("", "", move);
        }
        else if(move.matches("[a-h][a-h]\\d")){
            moveKnight(move.substring(0,1), "", move.substring(1));
        }
        else if(move.matches("\\d[a-h]\\d")){
            moveKnight("", move.substring(0,1), move.substring(1));
        }
        else{
            moveKnight(move.substring(0,1), move.substring(1,2), move.substring(2));
        }
    }

    private void moveKnight(String srcFile, String srcRank, String dest){
        ArrayList<Piece> list;
        if (whiteToPlay) list = pm.wKnights; else list = pm.bKnights;

        if (capture) { pm.capturePieceByPosition(dest); }

        if (srcFile.isEmpty() && srcRank.isEmpty()){

            for (Piece p : list){
                if (vmc.validGammaMove(p.getPosition(), dest)){
                    p.move(dest);
                    return;
                }
            }
        }
        if (!srcFile.isEmpty() && srcRank.isEmpty()){
            for (Piece p : list){
                if (p.getFile().equals(srcFile) && vmc.validGammaMove(p.getPosition(), dest)){
                    p.move(dest);
                    return;
                }
            }
        }
        if (srcFile.isEmpty() && !srcRank.isEmpty()){
            for (Piece p : list){
                if (p.getRank().equals(srcRank) && vmc.validGammaMove(p.getPosition(), dest)){
                    p.move(dest);
                    return;
                }
            }
        }
        if (vmc.validGammaMove(srcFile+srcRank, dest)){
            movePieceByPosition(list, srcFile+srcRank, dest);
        }
    }

    private void movePieceByPosition(ArrayList<Piece> pieces, String source, String dest){
        for (Piece p : pieces){
            if (p.getPosition().equals(source)){
                p.move(dest);
            }
        }
    }

    private void handleKing(){
        move = move.replace("K", "");

        if (capture) { pm.capturePieceByPosition(move); }

        if (whiteToPlay){
            pm.wKing.move(move);
        }
        else{
            pm.bKing.move(move);
        }
    }

}
