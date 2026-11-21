package com.moiris.chess_pgn_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;

@SpringBootApplication
public class ChessPgnManagerApplication {

    public static void main(String[] args) throws FileNotFoundException {

        SpringApplication.run(ChessPgnManagerApplication.class, args);
    }

}
