package com.moiris.chess_pgn_manager.pgn_parsing;

import com.moiris.chess_pgn_manager.pojos.GameData;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class PGNParser {

    // data class for storing data
    private final List<GameData> gameDataList;

    public PGNParser() {
        this.gameDataList = new ArrayList<>();
    }

    public void testClass(String filePath){
        for (String game : spiltGamesFromPgnFile(filePath)){
            gameDataList.add(createGameDataObject(game));
        }

        for (GameData gd : gameDataList){System.out.println(gd);}

    }

    public GameData createGameDataObject(String game) {
        String[] keys = {"Event ", "Site ", "Date ", "Round ", "White ", "Black ", "Result "};
        StringBuilder sb = new StringBuilder(game);
        StringBuilder temp = new StringBuilder();
        GameData gd = new GameData();

        // parsing of game info
        while (sb.indexOf("[") != -1) {
            temp.append(sb, sb.indexOf("[")+1, sb.indexOf("]"));
            sb.delete(sb.indexOf("["), sb.indexOf("]")+1);

            if (temp.indexOf(keys[0]) != -1){
                gd.setEvent(temp.substring(temp.indexOf("\"")+1, temp.lastIndexOf("\"")));
            }
            if (temp.indexOf(keys[1]) != -1){
                gd.setSite(temp.substring(temp.indexOf("\"")+1, temp.lastIndexOf("\"")));
            }
            if (temp.indexOf(keys[2]) != -1){
                gd.setDate(temp.substring(temp.indexOf("\"")+1, temp.lastIndexOf("\"")));
            }
            if (temp.indexOf(keys[3]) != -1){
                gd.setRound(temp.substring(temp.indexOf("\"")+1, temp.lastIndexOf("\"")));
            }
            if (temp.indexOf(keys[4]) != -1){
                gd.setWhite(temp.substring(temp.indexOf("\"")+1, temp.lastIndexOf("\"")));
            }
            if (temp.indexOf(keys[5]) != -1){
                gd.setBlack(temp.substring(temp.indexOf("\"")+1, temp.lastIndexOf("\"")));
            }
            if (temp.indexOf(keys[6]) != -1){
                gd.setResult(temp.substring(temp.indexOf("\"")+1, temp.lastIndexOf("\"")));
            }
            temp.setLength(0);
        }

        //System.out.println(sb.toString());

        //removal of comments
        while (sb.indexOf("{") != -1){
            sb.delete(sb.indexOf("{"), sb.indexOf("}")+1);
        }

        //System.out.println(sb.toString());

        String[] moves = sb.toString().split(" +|\\.");

        //for (String m : moves) System.out.println(m);

        gd.setResult(moves[moves.length-1]);
        //System.out.println(gd.getResult());

        boolean white = true;

        for (int i=0; i<moves.length-1; i++){
            if (moves[i].matches("\\d+")) continue;
            if (white){
                white = false;
                gd.appendWhiteHalfMove(moves[i]);
                continue;
            }
            white = true;
            gd.appendBlackHalfMove(moves[i]);


        }
        //System.out.println(gd.toString());
        return gd;
    }

    private List<String> spiltGamesFromPgnFile(String PgnFilePath){
        try{
            String pgnText = Files.readString(Path.of(PgnFilePath));
            return getGamesAsList(pgnText);

        } catch (IOException e) {
            return List.of();
        }
    }

    private List<String> getGamesAsList(String pgnAsText){

        List<String> gamesList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean parsingMoves = false;

        String[] l = pgnAsText.split("\\R", -1);

        for (String line : l) {

            if (line.startsWith("[")) {
                sb.append(line);
                continue;
            }

            if (line.isEmpty() && !parsingMoves) {
                continue;
            }

            if (line.startsWith("1.") && !parsingMoves) {
                parsingMoves = true;
                sb.append(line).append(" ");
                continue;
            }

            if (parsingMoves && !line.isEmpty()) {
                sb.append(line).append(" ");
                continue;
            }

            if (line.isEmpty()){
                gamesList.add(sb.toString());
                parsingMoves = false;
                sb.setLength(0);
            }
        }
        //adding last game if file does not end with empty line
        if (!sb.isEmpty()) gamesList.add(sb.toString());

        return gamesList;
    }
}
