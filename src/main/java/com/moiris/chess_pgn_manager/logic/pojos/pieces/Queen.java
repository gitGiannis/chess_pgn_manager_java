package com.moiris.chess_pgn_manager.logic.pojos.pieces;

import com.moiris.chess_pgn_manager.logic.pojos.Piece;

public class Queen extends Piece {
    public Queen(String position) {
        super(position);
    }

    public Queen(String position, String name) {
        super(position,name);
    }
}
