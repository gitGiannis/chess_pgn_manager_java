package com.moiris.chess_pgn_manager.parsing;

import com.moiris.chess_pgn_manager.auxiliery_classes.Board;
import com.moiris.chess_pgn_manager.pieces.*;
import com.moiris.chess_pgn_manager.pojos.Files;
import com.moiris.chess_pgn_manager.pojos.GameData;
import com.moiris.chess_pgn_manager.auxiliery_classes.PieceManager;
import com.moiris.chess_pgn_manager.pojos.Piece;
import com.moiris.chess_pgn_manager.pojos.Ranks;

import java.util.ArrayList;

import static java.lang.System.exit;

public class MoveParser {
    private final GameData gd;
    private final PieceManager pm;
    private final Board b;

    private boolean capture = false;
    private String pawnPromotion = "";
    private boolean check = false;
    private boolean brilliant = false;
    private boolean blunder = false;

    private boolean whiteToPlay = false;
    private String move;

    private ArrayList<Piece> attacker;

    public MoveParser(GameData gameData, PieceManager pieceManager, Board board) {
        this.gd = gameData;
        this.pm = pieceManager;
        this.b = board;
    }

    public void parseNextMove(){
        whiteToPlay = !whiteToPlay;
        b.updateSelf();

        if (whiteToPlay) {
            move = gd.getNextWhiteHalfMove();
            System.out.println("White -> " + move);
        }
        else{
            move = gd.getNextBlackHalfMove();
            System.out.println("Black -> " + move);
        }

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
        if (move.matches("[a-h].+")) {handlePawn(); return;}


        System.out.println("ERROR HANDLING MOVE at ["+ gd.getWhiteIndex() + "]");
        exit(0);
    }

    private void capturePieceIf(){
        if (capture) {
            if (whiteToPlay){
                pm.captureBlackPieceByPosition(move.substring(move.length()-2));
            }
            else{
                pm.captureWhitePieceByPosition(move.substring(move.length()-2));
            }
        }
        b.updateSelf();
    }

    private void smallCastle(){
        if (whiteToPlay) {
            if (b.get("f1") == null &&  b.get("g1") == null) {
                if (pm.wKing.getPosition().equals("e1"))
                    pm.wKing.moveTo("g1");
                for (Piece p : pm.wRooks){
                    if (p.getPosition().equals("h1")){
                        p.moveTo("f1");
                        return;
                    }
                }
            }
        }
        else {
            if (b.get("f8") == null && b.get("g8") == null) {
                if (pm.bKing.getPosition().equals("e8"))
                    pm.bKing.moveTo("g8");
                for (Piece p : pm.bRooks){
                    if (p.getPosition().equals("h8")){
                        p.moveTo("f8");
                        return;
                    }
                }
            }
        }

        // ERROR HANDLING
        System.out.println("ERROR HANDLING 0-0");
        exit(0);
    }

    private void bigCastle(){
        if (whiteToPlay) {
            if (b.get("b1") == null &&  b.get("c1") == null &&  b.get("d1") == null) {
                if (pm.wKing.getPosition().equals("e1"))
                    pm.wKing.moveTo("c1");
                for (Piece p : pm.wRooks){
                    if (p.getPosition().equals("a1")){
                        p.moveTo("d1");
                        return;
                    }
                }
            }
        }
        else {
            if (b.get("b8") == null &&  b.get("c8") == null &&  b.get("d8") == null) {
                if (pm.bKing.getPosition().equals("e8"))
                    pm.bKing.moveTo("c8");
                for (Piece p : pm.bRooks) {
                    if (p.getPosition().equals("a8")) {
                        p.moveTo("d8");
                        return;
                    }
                }
            }
        }

        // ERROR HANDLING
        System.out.println("ERROR HANDLING 0-0-0");
        exit(0);
    }

    private void handlePawn(){

        if (move.contains("=")){
            move = move.replace("=", "");
            pawnPromotion = move.substring(move.length() - 1);
            move = move.replace(pawnPromotion, "");
        }

        // pawn moves forwards
        if (move.matches("[a-h]\\d")){

            if (b.get(move) != null){
                return;
            }

            String destFile = move.substring(0,1);
            String destRank = move.substring(1);

            if (whiteToPlay){
                // pawns being one rank away from the designation cell are checked first
                // this way, logic to check if a pawns way to the destination cell is blocked, is not needed
                for (Piece p : pm.wPawns) {
                    if (p.getFile().equals(destFile) && Ranks.getIndex(destRank) - Ranks.getIndex(p.getRank()) == 1){
                        p.moveTo(move);
                        promotePawnIf(p);
                        return;
                    }
                }
                // no piece at one rank away was found, so it should be a two cell move
                for (Piece p : pm.wPawns) {
                    if (!p.getRank().equals("2")) continue;
                    if (b.get(p.getFile() + Ranks.getNextRank(p.getRank())) != null) continue;

                    if (p.getFile().equals(destFile) && Ranks.getIndex(destRank) - Ranks.getIndex(p.getRank()) == 2){
                        p.moveTo(move);
                        promotePawnIf(p);
                        return;
                    }

                }
            }
            else{
                // pawns being one rank away from the designation cell are checked first
                // this way, logic to check if a pawns way to the destination cell is blocked, is not needed
                for (Piece p : pm.bPawns) {
                    if (p.getFile().equals(destFile) && Ranks.getIndex(destRank) - Ranks.getIndex(p.getRank())  == -1){
                        p.moveTo(move);
                        promotePawnIf(p);
                        return;
                    }
                }
                // no piece at one rank away was found, so it should be a two cell move
                for (Piece p : pm.bPawns) {
                    if (!p.getRank().equals("7")) continue;
                    if (b.get(p.getFile() + Ranks.getPreviousRank(p.getRank())) != null) continue;

                    if (p.getFile().equals(destFile) && Ranks.getIndex(destRank) - Ranks.getIndex(p.getRank()) == -2){
                        p.moveTo(move);
                        promotePawnIf(p);
                        return;
                    }
                }
            }
        }

        // pawn moves diagonally and captures
        else if (move.matches("[a-h][a-h]\\d")){

            String srcFile = move.substring(0,1);
            move = move.substring(1);
            String destFile = move.substring(0,1);
            String destRank = move.substring(1);

            // en passant must be checked and if performed, the pawn captured does not occupy the destination cell of
            // the pawn performing the capture
            if (b.get(move) != null){
                capturePieceIf();
            }
            else { // destination cell is null, so a paw must be captured by en passant
                if (whiteToPlay){
                    if (!pm.bPawns.removeIf(p ->
                                            p.getPosition().equals(destFile + Ranks.getPreviousRank(destRank)))){

                        System.out.println("ERROR HANDLING White EN PASSANT at ["+ gd.getWhiteIndex() + "]");
                        exit(0);
                    }

                }
                else{
                    if (!pm.wPawns.removeIf(p ->
                                            p.getPosition().equals(destFile + Ranks.getNextRank(destRank)))){

                        System.out.println("ERROR HANDLING Black EN PASSANT at ["+ gd.getWhiteIndex() + "]");
                        exit(0);
                    }

                }
            }

            if (whiteToPlay){
                for (Piece p : pm.wPawns) {
                    if (p.getFile().equals(srcFile)
                            && Math.abs(Files.getIndex(destFile) - Files.getIndex(p.getFile())) == 1
                            && Ranks.getIndex(destRank) - Ranks.getIndex(p.getRank()) == 1) {
                        p.moveTo(move);
                        promotePawnIf(p);
                        return;
                    }
                }


            }
            else{
                for (Piece p : pm.bPawns) {
                    if (p.getFile().equals(srcFile)
                            && Math.abs(Files.getIndex(destFile) - Files.getIndex(p.getFile())) == 1
                            && Ranks.getIndex(destRank) - Ranks.getIndex(p.getRank())  == -1){
                        p.moveTo(move);
                        promotePawnIf(p);
                        return;
                    }
                }
            }
        }

        System.out.println("ERROR HANDLING PAWN at ["+ gd.getWhiteIndex() + "]");
        exit(0);
    }

    private void promotePawnIf(Piece p){
        if (pawnPromotion.isEmpty()) return;

        Piece newPiece;
        
        if (whiteToPlay){
            pm.wPawns.remove(p);
            pm.capturedPieces.add(p);
            switch (pawnPromotion){
                case "Q":
                    newPiece = new Queen(p.getPosition(), "Q");
                    pm.wQueens.add(newPiece);
                    newPiece.moveTo(p.getPosition());
                    pawnPromotion = "";
                    return;

                case "R":
                    newPiece =new Rook(p.getPosition(), "R");
                    pm.wRooks.add(newPiece);
                    newPiece.moveTo(p.getPosition());
                    pawnPromotion = "";
                    return;

                case "N":
                    newPiece = new Knight(p.getPosition(), "N");
                    pm.wKnights.add(newPiece);
                    newPiece.moveTo(p.getPosition());
                    pawnPromotion = "";
                    return;

                case "B":
                    newPiece = new Bishop(p.getPosition(), "B");
                    pm.wBishops.add(newPiece);
                    newPiece.moveTo(p.getPosition());
                    pawnPromotion = "";
                    return;

                default:
                    break;
            }
        }
        else{
            pm.bPawns.remove(p);
            pm.capturedPieces.add(p);
            switch (pawnPromotion){
                case "Q":
                    newPiece = new Queen(p.getPosition(), "q");
                    pm.bQueens.add(newPiece);
                    newPiece.moveTo(p.getPosition());
                    pawnPromotion = "";
                    return;

                case "R":
                    newPiece = new Rook(p.getPosition(), "r");
                    pm.bRooks.add(newPiece);
                    newPiece.moveTo(p.getPosition());
                    pawnPromotion = "";
                    return;

                case "N":
                    newPiece = new Knight(p.getPosition(), "n");
                    pm.bKnights.add(newPiece);
                    newPiece.moveTo(p.getPosition());
                    pawnPromotion = "";
                    return;

                case "B":
                    newPiece = new Bishop(p.getPosition(), "b");
                    pm.bBishops.add(newPiece);
                    newPiece.moveTo(p.getPosition());
                    pawnPromotion = "";
                    return;

                default:
                    break;
            }
        }
        System.out.println("ERROR HANDLING PAWN PROMOTION at [" + gd.getWhiteIndex() + "]");
    }

    private void handleBishop(){
        move = move.replace("B", "");

        if (whiteToPlay) {
            attacker = pm.wBishops;
        } else {
            attacker = pm.bBishops;
        }

        capturePieceIf();

        // e.g. Ba1
        if (move.matches("[a-h]\\d")){
            for (Piece p : attacker){
                if (hasValidBishopMove(p) && isNotPinned(p)){
                    p.moveTo(move);
                    return;
                }
            }
        }
        // e.g. Bah1
        else if(move.matches("[a-h][a-h]\\d")){
            String prefixFile = move.substring(0,1);
            move = move.substring(1);

            for (Piece p : attacker){
                // found piece on the same file as move prefix
                if (p.getFile().equals(prefixFile)){
                    if (hasValidBishopMove(p) && isNotPinned(p)){
                    p.moveTo(move);
                    return;
                    }
                }
            }
        }
        // e.g. B8a1
        else if(move.matches("\\d[a-h]\\d")){
            String prefixRank = move.substring(0,1);
            move = move.substring(1);

            for (Piece p : attacker){
                // found piece on the same rank as move prefix
                if (p.getRank().equals(prefixRank)){
                    if (hasValidBishopMove(p) && isNotPinned(p)){
                        p.moveTo(move);
                        return;
                    }
                }
            }
        }
        // e.g. Bh8a1
        else if (move.matches("[a-h]\\d[a-h]\\d")){
            String prefixPosition = move.substring(0,2);
            move = move.substring(2);

            for (Piece p : attacker){
                // found piece on the same rank as move prefix
                if (p.getPosition().equals(prefixPosition)){
                    if (hasValidBishopMove(p) && isNotPinned(p)){
                        p.moveTo(move);
                        return;
                    }
                }
            }
        }

        // ERROR HANDLING
        System.out.println("ERROR HANDLING BISHOP at [" + gd.getWhiteIndex() + "]");
        exit(0);

    }

    private void handleRook(){
        move = move.replace("R", "");

        if (whiteToPlay) {
            attacker = pm.wRooks;
        } else {
            attacker = pm.bRooks;
        }

        capturePieceIf();

        // e.g. Ra1
        if (move.matches("[a-h]\\d")){
            for (Piece p : attacker){
                if (hasValidRookMove(p) && isNotPinned(p)){
                    p.moveTo(move);
                    return;
                }
            }
        }
        // e.g. Rha1
        else if(move.matches("[a-h][a-h]\\d")){
            String prefixFile = move.substring(0,1);
            move = move.substring(1);

            for (Piece p : attacker){
                // found piece on the same file as move prefix
                if (p.getFile().equals(prefixFile)){
                    if (hasValidRookMove(p) && isNotPinned(p)){
                        p.moveTo(move);
                        return;
                    }
                }
            }
        }
        // e.g. R8a1
        else if(move.matches("\\d[a-h]\\d")){
            String prefixRank = move.substring(0,1);
            move = move.substring(1);

            for (Piece p : attacker){
                // found piece on the same rank as move prefix
                if (p.getRank().equals(prefixRank)){
                    if (hasValidRookMove(p) && isNotPinned(p)){
                        p.moveTo(move);
                        return;
                    }
                }
            }
        }
        // e.g.Ra8a1
        else if (move.matches("[a-h]\\d[a-h]\\d")){
            String prefixPosition = move.substring(0,2);
            move = move.substring(2);

            for (Piece p : attacker){
                // found piece on the same rank as move prefix
                if (p.getPosition().equals(prefixPosition)){
                    if (hasValidRookMove(p) && isNotPinned(p)){
                        p.moveTo(move);
                        return;
                    }
                }
            }
        }

        // ERROR HANDLING
        System.out.println("ERROR HANDLING ROOK at [" + gd.getWhiteIndex() + "]");
        exit(0);
    }

    private void handleQueen(){
        move = move.replace("Q", "");

        if (whiteToPlay) {
            attacker = pm.wQueens;
        } else {
            attacker = pm.bQueens;
        }

        capturePieceIf();

        // e.g. Qa1
        if (move.matches("[a-h]\\d")){
            for (Piece p : attacker){
                if (hasValidQueenMove(p) && isNotPinned(p)){
                    p.moveTo(move);
                    return;
                }
            }
        }
        // e.g. Qha1
        else if(move.matches("[a-h][a-h]\\d")){
            String prefixFile = move.substring(0,1);
            move = move.substring(1);

            for (Piece p : attacker){
                // found piece on the same file as move prefix
                if (p.getFile().equals(prefixFile)){
                    if (hasValidQueenMove(p) && isNotPinned(p)){
                        p.moveTo(move);
                        return;
                    }
                }
            }
        }
        // e.g. Q8a1
        else if(move.matches("\\d[a-h]\\d")){
            String prefixRank = move.substring(0,1);
            move = move.substring(1);

            for (Piece p : attacker){
                // found piece on the same rank as move prefix
                if (p.getRank().equals(prefixRank)){
                    if (hasValidQueenMove(p) && isNotPinned(p)){
                        p.moveTo(move);
                        return;
                    }
                }
            }
        }
        // e.g.Qa8a1
        else if (move.matches("[a-h]\\d[a-h]\\d")){
            String prefixPosition = move.substring(0,2);
            move = move.substring(2);

            for (Piece p : attacker){
                // found piece on the same rank as move prefix
                if (p.getPosition().equals(prefixPosition)){
                    if (hasValidQueenMove(p) && isNotPinned(p)){
                        p.moveTo(move);
                        return;
                    }
                }
            }
        }

        // ERROR HANDLING
        System.out.println("ERROR HANDLING QUEEN at [" + gd.getWhiteIndex() + "]");
        exit(0);
    }

    private void handleKnight(){
        move = move.replace("N", "");

        if (whiteToPlay) {
            attacker = pm.wKnights;
        } else {
            attacker = pm.bKnights;
        }

        capturePieceIf();

        // e.g. Na1
        if (move.matches("[a-h]\\d")){
            for (Piece p : attacker){
                if (hasValidKnightMove(p) && isNotPinned(p)){
                    p.moveTo(move);
                    return;
                }
            }
        }
        // e.g. Nba1
        else if(move.matches("[a-h][a-h]\\d")){
            String prefixFile = move.substring(0,1);
            move = move.substring(1);

            for (Piece p : attacker){
                // found piece on the same file as move prefix
                if (p.getFile().equals(prefixFile)){
                    if (hasValidKnightMove(p) && isNotPinned(p)){
                        p.moveTo(move);
                        return;
                    }
                }
            }
        }
        // e.g. N2a1
        else if(move.matches("\\d[a-h]\\d")){
            String prefixRank = move.substring(0,1);
            move = move.substring(1);

            for (Piece p : attacker){
                // found piece on the same rank as move prefix
                if (p.getRank().equals(prefixRank)){
                    if (hasValidKnightMove(p) && isNotPinned(p)){
                        p.moveTo(move);
                        return;
                    }
                }
            }
        }
        // e.g.Nb3a1
        else if (move.matches("[a-h]\\d[a-h]\\d")){
            String prefixPosition = move.substring(0,2);
            move = move.substring(2);

            for (Piece p : attacker){
                // found piece on the same rank as move prefix
                if (p.getPosition().equals(prefixPosition)){
                    if (hasValidKnightMove(p) && isNotPinned(p)){
                        p.moveTo(move);
                        return;
                    }
                }
            }
        }

        // ERROR HANDLING
        System.out.println("ERROR HANDLING KNIGHT at [" + gd.getWhiteIndex() + "]");
        exit(0);
    }

    private void handleKing(){
        move = move.replace("K", "");

        capturePieceIf();

        if (move.matches("[a-h]\\d")){
            if (whiteToPlay){
                if (b.get(move) == null){
                    int absoluteFileDiff =
                            Math.abs(Files.getIndex(pm.wKing.getFile()) - Files.getIndex(move.substring(0,1)));
                    int absoluteRankDiff =
                            Math.abs(Ranks.getIndex(pm.wKing.getRank()) - Ranks.getIndex(move.substring(1)));
                    if ((absoluteFileDiff == 1 || absoluteFileDiff == 0) &&
                            (absoluteRankDiff == 1 || absoluteRankDiff == 0)){
                        pm.wKing.moveTo(move);
                        return;
                    }
                }
            }
            else {
                if (b.get(move) == null){
                    int absoluteFileDiff =
                            Math.abs(Files.getIndex(pm.bKing.getFile()) - Files.getIndex(move.substring(0,1)));
                    int absoluteRankDiff =
                            Math.abs(Ranks.getIndex(pm.bKing.getRank()) - Ranks.getIndex(move.substring(1)));

                    if ((absoluteFileDiff == 1 || absoluteFileDiff == 0) &&
                            (absoluteRankDiff == 1 || absoluteRankDiff == 0)) {
                        pm.bKing.moveTo(move);
                        return;
                    }
                }
            }
        }
        //ERROR handling
        System.out.println("ERROR HANDLING KING");
        exit(0);
    }

    /**
     * Checks whether a diagonal move between two positions is valid.
     * The method checks all four diagonal directions and verifies that
     * every square between the source and destination is unoccupied.
     *
     * @param bishop the bishop to move
     * @return true if the destination lies on a diagonal from the source
     *         and the path between them is clear; false otherwise
     */
    public boolean hasValidBishopMove(Piece bishop){
        String srcFile = bishop.getFile();
        String destFile = move.substring(0,1);
        String srcRank = bishop.getRank();
        String destRank = move.substring(1);

        int absoluteFileDiff = Math.abs(Files.getIndex(srcFile) - Files.getIndex(destFile));
        int absoluteRankDiff = Math.abs(Ranks.getIndex(srcRank) - Ranks.getIndex(destRank));

        // destination cell is not within the bishop's valid moves
        if (absoluteFileDiff != absoluteRankDiff) return false;

        String srcPos =  bishop.getPosition();
        String destPos =  move;

        // right and upwards or downwards movement ---------------------------------------------------------------------
        // bishop moves right and upwards
        if (Files.getIndex(srcFile) < Files.getIndex(destFile) && Ranks.getIndex(srcRank) < Ranks.getIndex(destRank)) {
            while (!srcPos.equals(destPos)) {
                srcFile = Files.getNextFile(srcFile);
                srcRank = Ranks.getNextRank(srcRank);
                srcPos = srcFile + srcRank;
                if (b.get(srcPos) != null) return false;
            }
            return true;
        }
        // bishop moves right and downwards
        if (Files.getIndex(srcFile) < Files.getIndex(destFile) && Ranks.getIndex(srcRank) > Ranks.getIndex(destRank)) {
            while (!srcPos.equals(destPos)) {
                srcFile = Files.getNextFile(srcFile);
                srcRank = Ranks.getPreviousRank(srcRank);
                srcPos = srcFile + srcRank;
                if (b.get(srcPos) != null) return false;
            }
            return true;
        }

        // left and upwards or downwards movement ---------------------------------------------------------------------
        // bishop moves left and upwards
        if (Files.getIndex(srcFile) > Files.getIndex(destFile) && Ranks.getIndex(srcRank) < Ranks.getIndex(destRank)) {
            while (!srcPos.equals(destPos)) {
                srcFile = Files.getPreviousFile(srcFile);
                srcRank = Ranks.getNextRank(srcRank);
                srcPos = srcFile + srcRank;
                if (b.get(srcPos) != null) return false;
            }
            return true;
        }
        // bishop moves left and downwards
        if (Files.getIndex(srcFile) > Files.getIndex(destFile) && Ranks.getIndex(srcRank) > Ranks.getIndex(destRank)) {
            while (!srcPos.equals(destPos)) {
                srcFile = Files.getPreviousFile(srcFile);
                srcRank = Ranks.getPreviousRank(srcRank);
                srcPos = srcFile + srcRank;
                if (b.get(srcPos) != null) return false;
            }
            return true;
        }

        // not an eligible movement
        return  false;
    }

    /**
     * Checks whether a horizontal or vertical move between two positions
     * is valid.
     * <p>
     * The method checks all four horizontal and vertical directions and
     * verifies that every square between the source and destination is
     * unoccupied.
     *
     * @param rook the rook to move
     * @return true if the destination lies horizontally or vertically from
     *         the source and the path between them is clear; false otherwise
     */
    public boolean hasValidRookMove(Piece rook){
        String srcFile = rook.getFile();
        String destFile = move.substring(0,1);
        String srcRank = rook.getRank();
        String destRank = move.substring(1);

        int fileDiff = Files.getIndex(srcFile) - Files.getIndex(destFile);
        int rankDiff = Ranks.getIndex(srcRank) - Ranks.getIndex(destRank);

        // destination cell is not within the rook's valid moves
        if (fileDiff != 0 && rankDiff != 0) return false;


        // same file movement, only rank changes -----------------------------------------------------------------------
        // rook moves upwards
        if (srcFile.equals(destFile) && Ranks.getIndex(srcRank) < Ranks.getIndex(destRank)) {
            while (!srcRank.equals(destRank)) {
                srcRank = Ranks.getNextRank(srcRank);
                if (b.get(srcFile + srcRank) != null) return false;
            }
            return true;
        }
        // rook moves downwards
        if (srcFile.equals(destFile) && Ranks.getIndex(srcRank) > Ranks.getIndex(destRank)) {
            while (!srcRank.equals(destRank)) {
                srcRank = Ranks.getPreviousRank(srcRank);
                if (b.get(srcFile + srcRank) != null) return false;
            }
            return true;
        }

        // same rank movement, only file changes -----------------------------------------------------------------------
        // rook moves to the right
        if (srcRank.equals(destRank) && Files.getIndex(srcFile) < Files.getIndex(destFile)) {
            while (!srcFile.equals(destFile)) {
                srcFile = Files.getNextFile(srcFile);
                if (b.get(srcFile + srcRank) != null) return false;
            }
            return true;
        }
        // rook moves to the left
        if (srcRank.equals(destRank) && Files.getIndex(srcFile) > Files.getIndex(destFile)) {
            // rook moves to the left
            while (!srcFile.equals(destFile)) {
                srcFile = Files.getPreviousFile(srcFile);
                if (b.get(srcFile + srcRank) != null) return false;
            }
            return true;
        }
        // neither same file nor same rank movement
        return  false;
    }

    /**
     * Checks whether a move follows the movement pattern of a knight.
     * A valid knight move consists of a displacement of two squares in
     * one direction and one square in the perpendicular direction.
     *
     * @param knight the knight to move
     * @return true if the displacement between source and destination
     *         matches a valid knight move; false otherwise
     */
    public boolean hasValidKnightMove(Piece knight){
        if (b.get(move) != null) return false;

        int fileDistance = Files.getIndex(knight.getFile()) - Files.getIndex(move.substring(0,1));
        int rankDistance = Ranks.getIndex(knight.getRank()) - Ranks.getIndex(move.substring(1));

        if (fileDistance == -2 && (rankDistance == -1 || rankDistance == 1)){ return true; }
        if (fileDistance == -1 && (rankDistance == -2 || rankDistance == 2)){ return true; }
        if (fileDistance ==  1 && (rankDistance == -2 || rankDistance == 2)){ return true; }
        return fileDistance == 2 && (rankDistance == -1 || rankDistance == 1);
    }

    /**
     * Checks whether a horizontal or vertical move between two positions
     * is valid as well as a diagonal move
     * <p>
     *
     * @param queen the queen to move
     * @return true if the destination lies horizontally, vertically or diagonally from
     *         the source and the path between them is clear; false otherwise
     */
    public boolean hasValidQueenMove(Piece queen){
        return hasValidBishopMove(queen) || hasValidRookMove(queen);
    }

    /**
     * Checks whether a piece is pinned to its king piece by an enemy piece (Queen, Rook or Bishop).
     * The method checks if given that the piece is moved, the king gets "checked"
     *
     * @param piece the piece to move
     * @return true if the piece is indeed not pinned; false otherwise
     */
    public boolean isNotPinned(Piece piece){
        // flag used by all checks to determine whether an opponent pinner is being searched
        boolean checkForPinners = false;
        Piece king;
        if (whiteToPlay) {king = pm.wKing;}
        else{king = pm.bKing;}

        ArrayList<Piece> linePinners = pm.getLinePinners(whiteToPlay);
        ArrayList<Piece> diagPinners = pm.getDiagonalPinners(whiteToPlay);

        //--------------------------------------------------------------------------------------------------------------
        //piece and king are on the same file
        if (piece.getFile().equals(king.getFile())){
            //piece and king are on the same file, now two checks must be performed
            //1. if there is an opponent pinner on the same file that "sees" the king after the pinned piece moves
            //2. if there is another piece that protects the king if case 1. exists

            // the current file
            String curFile = piece.getFile();
            if (curFile.equals(move.substring(0,1))){
                // same file movement, so even if pinned, the piece still blocks the path to the king
                return true;
            }
            // king's rank
            String kingRank = king.getRank();


            //piece has higher rank than the king
            if (Ranks.getIndex(piece.getRank()) > Ranks.getIndex(king.getRank())){
                for (String r : Ranks.ranks){
                    //skipping the ranks until we reach the kings rank
                    if (Ranks.getIndex(r) <= Ranks.getIndex(kingRank)) continue;

                    // now examining the ranks after the king
                    // skipping the cells that contain no piece (friendly or opp)
                    if (b.get(curFile + r) == null) continue;

                    // a piece was found that also blocks the opponent path to the king
                    if (b.get(curFile + r) != piece && !checkForPinners) return true;

                    // checking for enemy pinners after the piece
                    if (b.get(curFile + r) == piece) {checkForPinners = true; continue;}

                    // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                    // a piece) is checked
                    // case 1: it is indeed an opponent pinner that pins the piece to its king
                    // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                    // does not need to continue further)
                    if (checkForPinners) {
                        if (b.get(curFile + r) != null) {
                            for (Piece op : linePinners) {
                                if (b.get(curFile + r) == op) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                }
                return true;
            }

            //piece has lower rank than the king
            if (Ranks.getIndex(piece.getRank()) < Ranks.getIndex(king.getRank())){
                for (String r : Ranks.ranksReversed){
                    //skipping the ranks until we reach the kings rank
                    if (Ranks.getIndexReversed(r) <= Ranks.getIndexReversed(kingRank)) continue;

                    // now examining the ranks after the king
                    // skipping the cells that contain no piece (friendly or opp)
                    if (b.get(curFile + r) == null) continue;
                    // a piece was found that also blocks the opponent path to the king
                    if (b.get(curFile + r) != piece && !checkForPinners) return true;

                    // checking for enemy pinners after the piece
                    if (b.get(curFile + r) == piece) {checkForPinners = true; continue;}

                    // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                    // a piece) is checked
                    // case 1: it is indeed an opponent pinner that pins the piece to its king
                    // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                    // does not need to continue further)
                    if (checkForPinners) {
                        if (b.get(curFile + r) != null) {
                            for (Piece op : linePinners) {
                                if (b.get(curFile + r) == op) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                }
                return true;
            }
        }


        //--------------------------------------------------------------------------------------------------------------
        // piece and king are on the same rank
        if (piece.getRank().equals(king.getRank())){
            // piece and king are on the same rank, now two checks must be performed
            // 1. if there is an opponent pinner on the same rank that "sees" the king after the pinned piece moves
            // 2. if there is another piece that protects the king if case 1. exists

            // the current rank
            String curRank = piece.getRank();
            if (curRank.equals(move.substring(1))){
                // same rank movement, so even if pinned, the piece still blocks the path to the king
                return true;
            }
            // king's rank
            String kingFile = king.getFile();


            //piece has higher (to the right) file than the king
            if (Files.getIndex(piece.getFile()) > Files.getIndex(king.getFile())){
                for (String f : Files.files){
                    //skipping the files until we reach the kings file
                    if (Files.getIndex(f) <= Files.getIndex(kingFile)) continue;

                    // now examining the files after the king
                    // skipping the cells that contain no piece (friendly or opp)
                    if (b.get(f + curRank) == null) continue;

                    // a piece was found that also blocks the opponent path to the king
                    if (b.get(f + curRank) != piece && !checkForPinners) return true;

                    // checking for enemy pinners after the piece
                    if (b.get(f + curRank) == piece) {checkForPinners = true; continue;}

                    // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                    // a piece) is checked
                    // case 1: it is indeed an opponent pinner that pins the piece to its king
                    // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                    // does not need to continue further)
                    if (checkForPinners) {
                        if (b.get(f + curRank) != null) {
                            for (Piece op : linePinners) {
                                if (b.get(f + curRank) == op) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                }
                return true;
            }

            //piece has lower file than the king
            if (Files.getIndex(piece.getFile()) < Files.getIndex(king.getFile())){
                for (String f : Files.filesReversed){
                    //skipping the files until we reach the kings rank
                    if (Files.getIndexReversed(f) <= Files.getIndexReversed(kingFile)) continue;

                    // now examining the ranks after the king
                    // skipping the cells that contain no piece (friendly or opp)
                    if (b.get(f + curRank) == null) continue;
                    // a piece was found that also blocks the opponent path to the king
                    if (b.get(f + curRank) != piece && !checkForPinners) return true;

                    // checking for enemy pinners after the piece
                    if (b.get(f + curRank) == piece) {checkForPinners = true; continue;}

                    // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                    // a piece) is checked
                    // case 1: it is indeed an opponent pinner that pins the piece to its king
                    // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                    // does not need to continue further)
                    if (checkForPinners) {
                        if (b.get(f + curRank) != null) {
                            for (Piece op : linePinners) {
                                if (b.get(f + curRank) == op) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                }
            }
            return true;
        }


        //--------------------------------------------------------------------------------------------------------------
        // file and rank difference between piece and king to determine whether they're on same diagonal
        int fileDiff = Files.getIndex(piece.getFile()) - Files.getIndex(king.getFile());
        int rankDiff = Ranks.getIndex(piece.getRank()) - Ranks.getIndex(king.getRank());
        // piece and king on the same diagonal
        if (Math.abs(fileDiff) == Math.abs(rankDiff)) {
            // position of the king

            String cell = king.getPosition();
            // piece is NE (upper and righter) in respect to the king
            if (fileDiff > 0 && rankDiff > 0) {
                while (true){
                    cell = Files.getNextFile(cell.substring(0,1)) + Ranks.getNextRank(cell.substring(1));
                    // reached out of bounds cell and no pinners found, so piece is free to move
                    if (!cell.matches("[a-h]\\d")) return true;

                    //same diagonal movement, so the pin can move whether it is pinned or not
                    if (cell.equals(move)) return true;

                    // now examining the cells after the king
                    // skipping the cells that contain no piece (friendly or opp)
                    if (b.get(cell) == null) continue;

                    // a piece was found that also blocks the opponent path to the king
                    if (b.get(cell) != piece && !checkForPinners) return true;

                    // checking for enemy pinners after the piece
                    if (b.get(cell) == piece) {checkForPinners = true; continue;}

                    // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                    // a piece) is checked
                    // case 1: it is indeed an opponent pinner that pins the piece to its king
                    // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                    // does not need to continue further)
                    if (checkForPinners) {
                        if (b.get(cell) != null) {
                            for (Piece op : diagPinners) {
                                if (b.get(cell) == op) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                }
            }

            if (fileDiff > 0 && rankDiff < 0) {
                while (true){
                    cell = Files.getNextFile(cell.substring(0,1)) + Ranks.getPreviousRank(cell.substring(1));
                    // reached out of bounds cell and no pinners found, so piece is free to move
                    if (!cell.matches("[a-h]\\d")) return true;

                    //same diagonal movement, so the pin can move whether it is pinned or not
                    if (cell.equals(move)) return true;

                    // now examining the cells after the king
                    // skipping the cells that contain no piece (friendly or opp)
                    if (b.get(cell) == null) continue;

                    // a piece was found that also blocks the opponent path to the king
                    if (b.get(cell) != piece && !checkForPinners) return true;

                    // checking for enemy pinners after the piece
                    if (b.get(cell) == piece) {checkForPinners = true; continue;}

                    // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                    // a piece) is checked
                    // case 1: it is indeed an opponent pinner that pins the piece to its king
                    // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                    // does not need to continue further)
                    if (checkForPinners) {
                        if (b.get(cell) != null) {
                            for (Piece op : diagPinners) {
                                if (b.get(cell) == op) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                }
            }

            if (fileDiff < 0 && rankDiff > 0) {
                while (true){
                    cell = Files.getPreviousFile(cell.substring(0,1)) + Ranks.getNextRank(cell.substring(1));
                    // reached out of bounds cell and no pinners found, so piece is free to move
                    if (!cell.matches("[a-h]\\d")) return true;

                    //same diagonal movement, so the pin can move whether it is pinned or not
                    if (cell.equals(move)) return true;

                    // now examining the cells after the king
                    // skipping the cells that contain no piece (friendly or opp)
                    if (b.get(cell) == null) continue;

                    // a piece was found that also blocks the opponent path to the king
                    if (b.get(cell) != piece && !checkForPinners) return true;

                    // checking for enemy pinners after the piece
                    if (b.get(cell) == piece) {checkForPinners = true; continue;}

                    // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                    // a piece) is checked
                    // case 1: it is indeed an opponent pinner that pins the piece to its king
                    // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                    // does not need to continue further)
                    if (checkForPinners) {
                        if (b.get(cell) != null) {
                            for (Piece op : diagPinners) {
                                if (b.get(cell) == op) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                }}

            if (fileDiff < 0 && rankDiff < 0) {
                while (true){
                    cell = Files.getPreviousFile(cell.substring(0,1)) + Ranks.getPreviousRank(cell.substring(1));
                    // reached out of bounds cell and no pinners found, so piece is free to move
                    if (!cell.matches("[a-h]\\d")) return true;

                    //same diagonal movement, so the pin can move whether it is pinned or not
                    if (cell.equals(move)) return true;

                    // now examining the cells after the king
                    // skipping the cells that contain no piece (friendly or opp)
                    if (b.get(cell) == null) continue;

                    // a piece was found that also blocks the opponent path to the king
                    if (b.get(cell) != piece && !checkForPinners) return true;

                    // checking for enemy pinners after the piece
                    if (b.get(cell) == piece) {checkForPinners = true; continue;}

                    // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                    // a piece) is checked
                    // case 1: it is indeed an opponent pinner that pins the piece to its king
                    // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                    // does not need to continue further)
                    if (checkForPinners) {
                        if (b.get(cell) != null) {
                            for (Piece op : diagPinners) {
                                if (b.get(cell) == op) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                }
            }
        }

        // piece is neither on same file or rank nor on same diagonal and therefore not pinned
        return true;
    }
}
