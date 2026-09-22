package com.moiris.chess_pgn_manager.pgn_parsing;

import com.moiris.chess_pgn_manager.pojos.GameData;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @file PGNParser.java
 * Parses PGN files into {@link GameData} objects for downstream chess analysis.
 */
public class PGNParser {

    /** Stores the parsed games created by this parser instance. */
    private final List<GameData> gameDataList;

    public PGNParser() {
        this.gameDataList = new ArrayList<>();
    }

    /**
     * Parses a PGN file and prints every parsed game for quick debugging.
     *
     * @param filePath the PGN file path to parse
     */
    public void testClass(String filePath){
        this.getGamesAsListOfDataGameObjects(filePath);

        for (GameData gd : gameDataList) System.out.println(gd);
    }

    /**
     * Parses the given PGN file and fills the {@link gameDataList} list with {@link GameData} entries.
     * Any existing cached games are cleared before loading fresh data.
     *
     * @param filePath the path to the PGN file
     */
    public void getGamesAsListOfDataGameObjects(String filePath){
        for (String game : getGamesAsListOfStrings(filePath))
            gameDataList.add(createGameDataObject(game));
    }

    /**
     * Translates a raw PGN game block into a {@link GameData} instance.
     *
     * @param game the complete PGN text for one game
     * @return the populated game data, or {@code null} when the block is empty
     */
    private GameData createGameDataObject(String game) {
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

        // removal of comments
        while (sb.indexOf("{") != -1)
            sb.delete(sb.indexOf("{"), sb.indexOf("}")+1);

        String[] moves = sb.toString().split(" +|\\.");

        gd.setResult(moves[moves.length-1]);

        boolean white = true;

        for (int i=0; i<moves.length-1; i++){
            if (moves[i].matches("\\d+"))
                continue;

            if (white){
                white = false;
                gd.appendWhiteHalfMove(moves[i]);
                continue;
            }

            white = true;
            gd.appendBlackHalfMove(moves[i]);

        }
        return gd;
    }

    /**
     * Reads the PGN text and splits it into individual game blocks.
     *
     * @param filePath the path to the PGN file
     * @return a list containing one game block per entry, or an empty list on I/O failure
     */
    private List<String> getGamesAsListOfStrings(String filePath){
        try{
            String pgnText = Files.readString(Path.of(filePath));
            return spiltGamesFromPgnFileAsListOfStrings(pgnText);

        } catch (IOException e) {
            return List.of();
        }
    }

    /**
     * Splits the raw PGN text into separate games.
     *
     * @param pgnAsText the full PGN text content
     * @return one PGN game per list element
     */
    private static List<String> spiltGamesFromPgnFileAsListOfStrings(String pgnAsText){

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
        // adding last game if file does not end with an empty line
        if (!sb.isEmpty()) gamesList.add(sb.toString());

        return gamesList;
    }
}
