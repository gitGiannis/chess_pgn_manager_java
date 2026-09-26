package com.moiris.chess_pgn_manager.pojos;

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
    private List<String> whiteHalfMoves = new ArrayList<>();
    private List<String> blackHalfMoves = new ArrayList<>();

    private int whiteIndex = -1;
    private int blackIndex = -1;
    private int totalHalfMoves;

    public void appendWhiteHalfMove(String halfMove){
        whiteHalfMoves.add(halfMove);
    }

    public void appendBlackHalfMove(String halfMove){
        blackHalfMoves.add(halfMove);
    }

    public String getNextWhiteHalfMove(){
        whiteIndex++;
        if (whiteIndex < whiteHalfMoves.size()) {
            return whiteHalfMoves.get(whiteIndex);
        }
        return null;
    }

    public String getNextBlackHalfMove(){
        blackIndex++;
        if (blackIndex < blackHalfMoves.size()) {
            return blackHalfMoves.get(blackIndex);
        }
        return null;
    }

    public int totalHalfMoves(){
        return whiteHalfMoves.size() +  blackHalfMoves.size();
    }
}
