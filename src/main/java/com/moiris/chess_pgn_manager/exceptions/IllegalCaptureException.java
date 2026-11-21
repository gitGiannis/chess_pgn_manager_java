package com.moiris.chess_pgn_manager.exceptions;

public class IllegalCaptureException extends Exception{
    public IllegalCaptureException(String colour){
        super("Could not perform " + colour + " capture move");
    }
}
