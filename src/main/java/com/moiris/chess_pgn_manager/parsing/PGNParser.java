package com.moiris.chess_pgn_manager.parsing;

import com.moiris.chess_pgn_manager.pojos.GameData;
import lombok.Getter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @file PGNParser.java
 * Parses PGN files into {@link GameData} objects for downstream chess analysis.
 */
@Getter
public class PGNParser {

    /** Stores the parsed games created by this parser instance. */
    private final List<GameData> gameDataList;

    public PGNParser() {
        this.gameDataList = new ArrayList<>();
    }

    /**
     * Parses the given PGN file and fills the {@link gameDataList} list with {@link GameData} entries.
     * Any existing cached games are cleared before loading fresh data.
     *
     * @param filePathOfPgnFile the path to the PGN file
     */
    public void parseFile(String filePathOfPgnFile) throws FileNotFoundException {
        List<String> games;
        try{
            String pgnText = Files.readString(Path.of(filePathOfPgnFile));
            games =  getListOfGamesAsStrings(pgnText);

        } catch (IOException e) {
            throw new FileNotFoundException();
        }

        for (String game : games)
            gameDataList.add(createGameDataObject(game));
    }

    public GameData getGameDataByIndex(int index){
        return gameDataList.get(index);
    }

    /**
     * Parses the given PGN text and fills the {@link gameDataList} list with {@link GameData} entries.
     * Any existing cached games are cleared before loading fresh data.
     *
     * @param pgnTxt the text of the PGN file
     */
    public void parseText(String pgnTxt){
        String game = getListOfGamesAsStrings(pgnTxt).get(0);
        gameDataList.add(createGameDataObject(game));
    }

    public GameData getGameData(){
        return gameDataList.get(0);
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

        // removal of round numbering and splitting based on whitespaces between half moves
        String[] moves = sb.toString().replaceAll("\\d+\\.\\s*", "").trim().split("\\s+");

        gd.setResult(moves[moves.length-1]);

        boolean white = true;

        for (int i=0; i<moves.length-1; i++){

            if (white){
                white = false;
                gd.appendWhitePly(moves[i]);
                continue;
            }

            white = true;
            gd.appendBlackPly(moves[i]);

        }
        return gd;
    }

    /**
     * Splits the raw PGN text into separate games and returns each game as element of a List<String>.
     *
     * @param pgnAsText the full PGN text content
     * @return one PGN game per list element
     */
    private static List<String> getListOfGamesAsStrings(String pgnAsText){

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
