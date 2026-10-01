package com.moiris.chess_pgn_manager.pojos;

import lombok.Data;

@Data
public class Piece {
    private String name;
    private String colour;
    private String position;
    private String file;
    private String rank;

    public Piece(String position) {
        this.position = position;
        this.file = position.substring(0, 1);
        this.rank = position.substring(1, 2);
    }

    public Piece(String position, String name) {
        this.name = name;
        this.position = position;
        this.file = position.substring(0, 1);
        this.rank = position.substring(1, 2);
    }

    public void moveTo(String destination) {
        this.position = destination;

        this.file = position.substring(0, 1);
        this.rank = position.substring(1, 2);
    }

    public String toString() {
        return name;
    }

}