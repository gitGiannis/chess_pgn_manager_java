package com.moiris.chess_pgn_manager;

import com.moiris.chess_pgn_manager.auxiliery_classes.Board;
import com.moiris.chess_pgn_manager.auxiliery_classes.PieceManager;
import com.moiris.chess_pgn_manager.parsing.MoveParser;
import com.moiris.chess_pgn_manager.parsing.PGNParser;
import com.moiris.chess_pgn_manager.pojos.GameData;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;

@SpringBootApplication
public class ChessPgnManagerApplication {

    public static void main(String[] args) throws FileNotFoundException {

        //SpringApplication.run(ChessPgnManagerApplication.class, args);

        //checkGame("Karpov (3500+)", 1);

        //Karpov (3500+)
        checkAllGames("Adams (3400+)", 3400, false);

    }

    private static void checkGame(String fileName, int gameIndex){
        PGNParser parser = new PGNParser();
        //test_pgn.pgn
        parser.parse("src/main/resources/pgn_files/"+fileName+".pgn");
        GameData gamedata = parser.getGameDataByIndex(gameIndex-1);

        PieceManager pm = new PieceManager();
        Board b =  new Board(pm);

        MoveParser moveParser = new MoveParser(gamedata, pm, b);

        for (int i=0; i<= gamedata.totalHalfMoves(); i++){ b.updateAndPrintSelf(); moveParser.parseNextMove(); }
    }

    private static void checkAllGames(String fileName, int noOfGames, boolean verbose){
        PGNParser parser = new PGNParser();
        parser.parse("src/main/resources/pgn_files/"+fileName+".pgn");

        for (int j=0; j<noOfGames; j++) {
            PieceManager pm = new PieceManager();
            Board b =  new Board(pm);
            System.out.println("\n\n\ngame no ->" + (j+1) + "--------------------------------------------------------");
            MoveParser moveParser = new MoveParser(parser.getGameDataByIndex(j), pm, b);
            for (int i=0; i<= parser.getGameDataByIndex(j).totalHalfMoves(); i++){
                if (verbose) b.updateAndPrintSelf();
                else b.updateSelf();
                moveParser.parseNextMove(); }
        }

    }

}
