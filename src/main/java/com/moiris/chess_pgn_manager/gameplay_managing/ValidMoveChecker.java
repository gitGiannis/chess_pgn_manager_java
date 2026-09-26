package com.moiris.chess_pgn_manager.gameplay_managing;

import com.moiris.chess_pgn_manager.pojos.Files;
import com.moiris.chess_pgn_manager.pojos.Ranks;


import java.util.ArrayList;

public class ValidMoveChecker {
    private Board board;

    public ValidMoveChecker(Board board) {
        this.board = board;
    }

    public void attachBoard(Board board){
        this.board = board;
    }

    public boolean validDiagonalMove(String source, String dest){
        String nextFile = source.substring(0,1);
        String prevFile = source.substring(0,1);
        String nextRank = source.substring(1);
        String prevRank = source.substring(1);

        int flag = 0;
        ArrayList<String> path1 = new ArrayList<>();
        ArrayList<String> path2 = new ArrayList<>();
        ArrayList<String> path3 = new ArrayList<>();
        ArrayList<String> path4 = new ArrayList<>();

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

            //System.out.println(nextFile+nextRank + " " + nextFile+prevRank + " " +prevFile+nextRank + " " +prevFile+prevRank + " dest="+ dest);
        }

        return switch (flag) {
            case 1 -> checkPath(path1);
            case 2 -> checkPath(path2);
            case 3 -> checkPath(path3);
            case 4 -> checkPath(path4);
            default -> false;
        };

    }

    public boolean validHorizontalOrVerticalMove(String source, String dest){
        String file = source.substring(0,1);
        String rank = source.substring(1);

        String nextFile = source.substring(0,1);
        String prevFile = source.substring(0,1);
        String nextRank = source.substring(1);
        String prevRank = source.substring(1);

        int flag = 0;
        ArrayList<String> path1 = new ArrayList<>();
        ArrayList<String> path2 = new ArrayList<>();
        ArrayList<String> path3 = new ArrayList<>();
        ArrayList<String> path4 = new ArrayList<>();

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

        return switch (flag) {
            case 1 -> checkPath(path1);
            case 2 -> checkPath(path2);
            case 3 -> checkPath(path3);
            case 4 -> checkPath(path4);
            default -> false;
        };
    }

    public boolean validGammaMove(String source, String dest){
        int fileDistance = Files.getIndex(source.substring(0,1)) - Files.getIndex(dest.substring(0,1));
        int rankDistance = Ranks.getIndex(source.substring(1)) - Ranks.getIndex(dest.substring(1));

        if (fileDistance == -2 && (rankDistance == -1 || rankDistance == 1)){ return true; }
        if (fileDistance == -1 && (rankDistance == -2 || rankDistance == 2)){ return true; }
        if (fileDistance ==  1 && (rankDistance == -2 || rankDistance == 2)){ return true; }
        if (fileDistance ==  2 && (rankDistance == -1 || rankDistance == 1)){ return true; }

        return false;
    }

    private static void addCellToPath(ArrayList<String> path, String cell){
        path.add(cell);
    }

    private boolean checkPath(ArrayList<String> path){
        for (String cell : path){
            //System.out.println(cell + " -> " + board.get(cell));
            if (board.get(cell) != null) { return false; }
        }
        return true;
    }

}
