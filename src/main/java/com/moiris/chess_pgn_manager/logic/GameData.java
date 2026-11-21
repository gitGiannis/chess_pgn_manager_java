package com.moiris.chess_pgn_manager.logic;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GameData {

    private String event;
    private String site;
    private String date;
    private String round;
    private String white;
    private String black;
    private String result;
    private List<String> whitePlies = new ArrayList<>();
    private List<String> blackPlies = new ArrayList<>();

    private int roundIndex = -1;

    public void appendWhitePly(String ply){
        whitePlies.add(ply);
    }

    public void appendBlackPly(String ply){
        blackPlies.add(ply);
    }

    public String getNextWhitePly(){
        roundIndex++;
        if (roundIndex < whitePlies.size()) {
            return whitePlies.get(roundIndex);
        }
        return null;
    }

    public String getNextBlackPly(){
        if (roundIndex < blackPlies.size()) {
            return blackPlies.get(roundIndex);
        }
        return null;
    }

    public int totalPliesCount(){
        return whitePlies.size() +  blackPlies.size();
    }
}
