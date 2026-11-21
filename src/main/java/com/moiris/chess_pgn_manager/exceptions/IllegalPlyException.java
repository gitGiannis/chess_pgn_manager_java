package com.moiris.chess_pgn_manager.exceptions;

public class IllegalPlyException extends Exception{
    public IllegalPlyException(int index){
        super("Could not parse move at index " + index);
    }
    public IllegalPlyException(int index, String msg){
        super("Could not parse move at index " + index + " (" + msg + ")");
    }
}
