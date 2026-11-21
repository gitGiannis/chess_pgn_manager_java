package com.moiris.chess_pgn_manager.logic.pojos.pieces;

import com.moiris.chess_pgn_manager.logic.pojos.Piece;

public class Bishop extends Piece {

    public Bishop(String position) {
        super(position);
    }

    public Bishop(String position, String name) {
            super(position,name);
    }
}
