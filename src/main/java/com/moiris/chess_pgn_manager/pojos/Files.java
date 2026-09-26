package com.moiris.chess_pgn_manager.pojos;

import java.util.Arrays;


public class Files {
    public static final String[] files = {"a", "b", "c", "d", "e", "f", "g", "h"};
    public static final String[] filesReversed = {"h", "g", "f", "e", "d", "c", "b", "a"};

    public static String getNextFile(String file) {
        int i = getIndex(file);
        if (i == -1) return "";
        if (i >= 7) return "";
        return files[i+1];
    }

    public static String getNextFileReversed(String file) {
        int i = getIndex(file);
        if (i == -1) return "";
        if (i >= 7) return "";
        return filesReversed[i+1];
    }

    public static String getPreviousFile(String file) {
        int i = getIndex(file);
        if (i == -1) return "";
        if (i <= 0) return "";
        return files[i-1];
    }

    public static String getPreviousFileReversed(String file) {
        int i = getIndex(file);
        if (i == -1) return "";
        if (i <= 0) return "";
        return filesReversed[i-1];
    }


    public static int getIndex(String file) {
        return Arrays.asList(files).indexOf(file);
    }

}
