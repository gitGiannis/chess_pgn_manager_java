package com.moiris.chess_pgn_manager.testing;

import com.moiris.chess_pgn_manager.logic.Board;
import com.moiris.chess_pgn_manager.logic.PieceManager;
import com.moiris.chess_pgn_manager.logic.exceptions.IllegalPlyException;
import com.moiris.chess_pgn_manager.logic.MoveDecoder;
import com.moiris.chess_pgn_manager.logic.PGNParser;
import com.moiris.chess_pgn_manager.logic.GameData;

import java.io.FileNotFoundException;

import static java.lang.System.exit;

public class Testing {

    public static void main(String[] args){
        checkAllGames("Adams (3400+)", 1, true);
    }

    private static void checkGame(String fileName, int gameIndex){
        PGNParser parser = new PGNParser();
        //test_pgn.pgn
        try {
            parser.parseFile("src/main/resources/pgn_files/"+fileName+".pgn");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            exit(0);
        }
        GameData gamedata = parser.getGameDataByIndex(gameIndex-1);

        PieceManager pm = new PieceManager();
        Board b =  new Board(pm);

        MoveDecoder moveDecoder = new MoveDecoder(gamedata, pm, b);

        for (int i = 0; i<= gamedata.totalPliesCount(); i++){
            b.updateAndPrintSelf();
            boolean white = (i % 2 == 0);
            if (i==gamedata.totalPliesCount()) break;
            System.out.print(white ? "white -> " : "black -> ");
            System.out.println(white ? gamedata.getWhitePlies().get(i / 2) : gamedata.getBlackPlies().get(i / 2));
            try {
                moveDecoder.decodeNextPly();
            } catch (IllegalPlyException e) {
                e.printStackTrace();
                exit(0);
            }
        }
    }

    private static void checkAllGames(String fileName, int noOfGames, boolean verbose){
        PGNParser parser = new PGNParser();
        try {
            parser.parseFile("src/main/resources/pgn_files/"+fileName+".pgn");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            exit(0);
        }

        for (int j=0; j<noOfGames; j++) {
            PieceManager pm = new PieceManager();
            Board b =  new Board(pm);
            GameData gamedata = parser.getGameDataByIndex(j);
            System.out.println("\n\n\ngame no ->" + (j+1) + "--------------------------------------------------------");
            MoveDecoder moveDecoder = new MoveDecoder(gamedata, pm, b);
            for (int i = 0; i<= gamedata.totalPliesCount(); i++){
                if (verbose) b.updateAndPrintSelf();

                boolean white = (i % 2 == 0);
                if (i==gamedata.totalPliesCount()) break;
                System.out.print(white ? "white -> " : "black -> ");
                System.out.println(white ? gamedata.getWhitePlies().get(i / 2) : gamedata.getBlackPlies().get(i / 2));

                try {
                    moveDecoder.decodeNextPly();
                } catch (IllegalPlyException e) {
                    e.printStackTrace();
                    exit(0);
                }
            }
        }

    }
}
