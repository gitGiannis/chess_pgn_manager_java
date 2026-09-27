package com.moiris.chess_pgn_manager.gameplay_managing;

import com.moiris.chess_pgn_manager.pojos.Files;
import com.moiris.chess_pgn_manager.pojos.Piece;
import com.moiris.chess_pgn_manager.pojos.Ranks;


import java.util.ArrayList;
import java.util.Arrays;

/**
 * Checks whether different types of chess piece movements are valid based
 * on their movement patterns and the pieces occupying the squares between
 * the source and destination positions.
 */
public class ValidMoveChecker {
    private final Board board;

    /**
     * Creates a ValidMoveChecker for the specified chess board.
     *
     * @param board the board whose occupied squares are used when checking moves
     */
    public ValidMoveChecker(Board board) {
        this.board = board;
    }

    /**
     * Checks whether a diagonal move between two positions is valid.
     * The method checks all four diagonal directions and verifies that
     * every square between the source and destination is unoccupied.
     *
     * @param source the starting position of the piece
     * @param dest the destination position of the piece
     * @return true if the destination lies on a diagonal from the source
     *         and the path between them is clear; false otherwise
     */
    public boolean validDiagonalMove(String source, String dest){
        //variables tha aid in creating each row and column of a piece
        String nextFile = source.substring(0,1);
        String prevFile = source.substring(0,1);
        String nextRank = source.substring(1);
        String prevRank = source.substring(1);

        // flag that takes value between 1-4 if path1-4 is valid (contains piece position and destination)
        int flag = 0;
        ArrayList<String> path1 = new ArrayList<>();
        ArrayList<String> path2 = new ArrayList<>();
        ArrayList<String> path3 = new ArrayList<>();
        ArrayList<String> path4 = new ArrayList<>();

        //all paths (up-right, down-right, up-left and down-left) are searched simultaneously
        for(int i=0; i<7; i++){
            nextFile = Files.getNextFile(nextFile);
            prevFile = Files.getPreviousFile(prevFile);
            nextRank = Ranks.getNextRank(nextRank);
            prevRank = Ranks.getPreviousRank(prevRank);

            if ((nextFile+nextRank).equals(dest)) { flag=1; break;}
            addCellToPath(path1, nextFile+nextRank);
            if ((nextFile+prevRank).equals(dest)) { flag=2; break;}
            addCellToPath(path2, nextFile+prevRank);
            if ((prevFile+nextRank).equals(dest)) { flag=3; break;}
            addCellToPath(path3, prevFile+nextRank);
            if ((prevFile+prevRank).equals(dest)) { flag=4; break;}
            addCellToPath(path4, prevFile+prevRank);
        }

        // the flag shows which path contains both source and destination
        // the given path is checked for availability (if the path is clear)
        return switch (flag) {
            case 1 -> pathIsClear(path1);
            case 2 -> pathIsClear(path2);
            case 3 -> pathIsClear(path3);
            case 4 -> pathIsClear(path4);
            default -> false;
        };

    }

    /**
     * Checks whether a horizontal or vertical move between two positions
     * is valid.
     * <p>
     * The method checks all four horizontal and vertical directions and
     * verifies that every square between the source and destination is
     * unoccupied.
     *
     * @param source the starting position of the piece
     * @param dest the destination position of the piece
     * @return true if the destination lies horizontally or vertically from
     *         the source and the path between them is clear; false otherwise
     */
    public boolean validHorizontalOrVerticalMove(String source, String dest){
        //starting file and rank of the piece
        String file = source.substring(0,1);
        String rank = source.substring(1);

        //variables tha aid in creating each row and column of a piece
        String nextFile = source.substring(0,1);
        String prevFile = source.substring(0,1);
        String nextRank = source.substring(1);
        String prevRank = source.substring(1);

        // flag that takes value between 1-4 if path1-4 is valid (contains piece position and destination)
        int flag = 0;
        ArrayList<String> path1 = new ArrayList<>();
        ArrayList<String> path2 = new ArrayList<>();
        ArrayList<String> path3 = new ArrayList<>();
        ArrayList<String> path4 = new ArrayList<>();

        //all paths (right, left, up and down) are searched simultaneously
        for(int i=0; i<7; i++){
            nextFile = Files.getNextFile(nextFile);
            prevFile = Files.getPreviousFile(prevFile);
            nextRank = Ranks.getNextRank(nextRank);
            prevRank = Ranks.getPreviousRank(prevRank);

            if ((nextFile+rank).equals(dest)) { flag=1; break;}
            addCellToPath(path1, nextFile+rank);
            if ((prevFile+rank).equals(dest)) { flag=2; break;}
            addCellToPath(path2, prevFile+rank);
            if ((file+nextRank).equals(dest)) { flag=3; break;}
            addCellToPath(path3, file+nextRank);
            if ((file+prevRank).equals(dest)) { flag=4; break;}
            addCellToPath(path4, file+prevRank);

        }

        // the flag shows which path contains both source and destination
        // the given path is checked for availability (if the path is clear)
        return switch (flag) {
            case 1 -> pathIsClear(path1);
            case 2 -> pathIsClear(path2);
            case 3 -> pathIsClear(path3);
            case 4 -> pathIsClear(path4);
            default -> false;
        };
    }

    /**
     * Checks whether a move follows the movement pattern of a knight.
     * A valid knight move consists of a displacement of two squares in
     * one direction and one square in the perpendicular direction.
     *
     * @param source the starting position of the piece
     * @param dest the destination position of the piece
     * @return true if the displacement between source and destination
     *         matches a valid knight move; false otherwise
     */
    public boolean validGammaMove(String source, String dest){
        int fileDistance = Files.getIndex(source.substring(0,1)) - Files.getIndex(dest.substring(0,1));
        int rankDistance = Ranks.getIndex(source.substring(1)) - Ranks.getIndex(dest.substring(1));

        if (fileDistance == -2 && (rankDistance == -1 || rankDistance == 1)){ return true; }
        if (fileDistance == -1 && (rankDistance == -2 || rankDistance == 2)){ return true; }
        if (fileDistance ==  1 && (rankDistance == -2 || rankDistance == 2)){ return true; }
        return fileDistance == 2 && (rankDistance == -1 || rankDistance == 1);
    }

    /**
     * Adds a board cell to a path.
     *
     * @param path the list representing the path of a potential move
     * @param cell the board cell to add to the path
     */
    private static void addCellToPath(ArrayList<String> path, String cell){
        path.add(cell);
    }

    /**
     * Checks whether every square in a potential movement path is unoccupied.
     *
     * @param path the list of board positions to check
     * @return true if all positions in the path are unoccupied; false if
     *         at least one position contains a piece
     */
    private boolean pathIsClear(ArrayList<String> path){
        for (String cell : path){
            if (board.get(cell) != null) { return false; }
        }
        return true;
    }

    /**
     * Checks whether a piece is pinned to its king piece by an enemy piece (Queen, Rook or Bishop).
     * The method checks if given that the piece is moved, the king gets "checked"
     *
     * @param piece the piece to move
     * @param king the piece's king
     * @param dest the destination of the moving piece
     * @param linePinners enemy pieces that can pin in straight lines (queen and rook)
     * @param diagPinners enemy pieces that can pin in diagonal lines (queen and bishop)
     * @return true if the piece is indeed not pinned; false otherwise
     */
    public boolean pieceIsNotPinned(Piece piece, Piece king, String dest,
                                    ArrayList<Piece> linePinners, ArrayList<Piece> diagPinners){
        // flag used by all checks to determine whether an opponent pinner is being searched
        boolean checkForPinners = false;

        //--------------------------------------------------------------------------------------------------------------
        //piece and king are on the same file
        if (piece.getFile().equals(king.getFile())){
            //piece and king are on the same file, now two checks must be performed
            //1. if there is an opponent pinner on the same file that "sees" the king after the pinned piece moves
            //2. if there is another piece that protects the king if case 1. exists

            // the current file
            String curFile = piece.getFile();
            if (curFile.equals(dest.substring(0,1))){
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
                    if (board.get(curFile + r) == null) continue;

                    // a piece was found that also blocks the opponent path to the king
                    if (board.get(curFile + r) != piece && !checkForPinners) return true;

                    // checking for enemy pinners after the piece
                    if (board.get(curFile + r) == piece) {checkForPinners = true; continue;}

                    // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                    // a piece) is checked
                    // case 1: it is indeed an opponent pinner that pins the piece to its king
                    // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                    // does not need to continue further)
                    if (checkForPinners) {
                        if (board.get(curFile + r) != null) {
                            for (Piece op : linePinners) {
                                if (board.get(curFile + r) == op) {
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
                    if (Ranks.getIndexR(r) <= Ranks.getIndexR(kingRank)) continue;

                    // now examining the ranks after the king
                    // skipping the cells that contain no piece (friendly or opp)
                    if (board.get(curFile + r) == null) continue;
                    // a piece was found that also blocks the opponent path to the king
                    if (board.get(curFile + r) != piece && !checkForPinners) return true;

                    // checking for enemy pinners after the piece
                    if (board.get(curFile + r) == piece) {checkForPinners = true; continue;}

                    // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                    // a piece) is checked
                    // case 1: it is indeed an opponent pinner that pins the piece to its king
                    // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                    // does not need to continue further)
                    if (checkForPinners) {
                        if (board.get(curFile + r) != null) {
                            for (Piece op : linePinners) {
                                if (board.get(curFile + r) == op) {
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
            if (curRank.equals(dest.substring(1))){
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
                    if (board.get(f + curRank) == null) continue;

                    // a piece was found that also blocks the opponent path to the king
                    if (board.get(f + curRank) != piece && !checkForPinners) return true;

                    // checking for enemy pinners after the piece
                    if (board.get(f + curRank) == piece) {checkForPinners = true; continue;}

                    // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                    // a piece) is checked
                    // case 1: it is indeed an opponent pinner that pins the piece to its king
                    // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                    // does not need to continue further)
                    if (checkForPinners) {
                        if (board.get(f + curRank) != null) {
                            for (Piece op : linePinners) {
                                if (board.get(f + curRank) == op) {
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
                    if (Files.getIndexR(f) <= Files.getIndexR(kingFile)) continue;

                    // now examining the ranks after the king
                    // skipping the cells that contain no piece (friendly or opp)
                    if (board.get(f + curRank) == null) continue;
                    // a piece was found that also blocks the opponent path to the king
                    if (board.get(f + curRank) != piece && !checkForPinners) return true;

                    // checking for enemy pinners after the piece
                    if (board.get(f + curRank) == piece) {checkForPinners = true; continue;}

                    // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                    // a piece) is checked
                    // case 1: it is indeed an opponent pinner that pins the piece to its king
                    // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                    // does not need to continue further)
                    if (checkForPinners) {
                        if (board.get(f + curRank) != null) {
                            for (Piece op : linePinners) {
                                if (board.get(f + curRank) == op) {
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
                    if (cell.equals(dest)) return true;

                    // now examining the cells after the king
                    // skipping the cells that contain no piece (friendly or opp)
                    if (board.get(cell) == null) continue;

                    // a piece was found that also blocks the opponent path to the king
                    if (board.get(cell) != piece && !checkForPinners) return true;

                    // checking for enemy pinners after the piece
                    if (board.get(cell) == piece) {checkForPinners = true; continue;}

                    // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                    // a piece) is checked
                    // case 1: it is indeed an opponent pinner that pins the piece to its king
                    // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                    // does not need to continue further)
                    if (checkForPinners) {
                        if (board.get(cell) != null) {
                            for (Piece op : diagPinners) {
                                if (board.get(cell) == op) {
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
                    if (cell.equals(dest)) return true;

                    // now examining the cells after the king
                    // skipping the cells that contain no piece (friendly or opp)
                    if (board.get(cell) == null) continue;

                    // a piece was found that also blocks the opponent path to the king
                    if (board.get(cell) != piece && !checkForPinners) return true;

                    // checking for enemy pinners after the piece
                    if (board.get(cell) == piece) {checkForPinners = true; continue;}

                    // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                    // a piece) is checked
                    // case 1: it is indeed an opponent pinner that pins the piece to its king
                    // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                    // does not need to continue further)
                    if (checkForPinners) {
                        if (board.get(cell) != null) {
                            for (Piece op : diagPinners) {
                                if (board.get(cell) == op) {
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
                if (cell.equals(dest)) return true;

                // now examining the cells after the king
                // skipping the cells that contain no piece (friendly or opp)
                if (board.get(cell) == null) continue;

                // a piece was found that also blocks the opponent path to the king
                if (board.get(cell) != piece && !checkForPinners) return true;

                // checking for enemy pinners after the piece
                if (board.get(cell) == piece) {checkForPinners = true; continue;}

                // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                // a piece) is checked
                // case 1: it is indeed an opponent pinner that pins the piece to its king
                // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                // does not need to continue further)
                if (checkForPinners) {
                    if (board.get(cell) != null) {
                        for (Piece op : diagPinners) {
                            if (board.get(cell) == op) {
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
                    if (cell.equals(dest)) return true;

                    // now examining the cells after the king
                    // skipping the cells that contain no piece (friendly or opp)
                    if (board.get(cell) == null) continue;

                    // a piece was found that also blocks the opponent path to the king
                    if (board.get(cell) != piece && !checkForPinners) return true;

                    // checking for enemy pinners after the piece
                    if (board.get(cell) == piece) {checkForPinners = true; continue;}

                    // checking for pinners has begun, the first cell found that is not null (meaning it is occupied by
                    // a piece) is checked
                    // case 1: it is indeed an opponent pinner that pins the piece to its king
                    // case 2: it is another non pinning piece (friend or foe) that blocks possible pinners (the search
                    // does not need to continue further)
                    if (checkForPinners) {
                        if (board.get(cell) != null) {
                            for (Piece op : diagPinners) {
                                if (board.get(cell) == op) {
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