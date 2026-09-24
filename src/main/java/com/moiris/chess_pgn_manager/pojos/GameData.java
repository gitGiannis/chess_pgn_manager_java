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

    public void appendWhiteHalfMove(String halfMove){
        whiteHalfMoves.add(halfMove);
    }

    public void appendBlackHalfMove(String halfMove){
        blackHalfMoves.add(halfMove);
    }

    public String getWhiteHalfMoveAt(int index){ return whiteHalfMoves.get(index); }

    public String getBlackHalfMoveAt(int index){ return blackHalfMoves.get(index); }
}
