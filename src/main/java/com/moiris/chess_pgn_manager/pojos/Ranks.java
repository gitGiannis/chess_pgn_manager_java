package com.moiris.chess_pgn_manager.pojos;

import java.util.Arrays;

public class Ranks {
    public static final String[] ranks = {"1", "2", "3", "4", "5", "6", "7", "8"};
    public static final String[] ranksReversed = {"8", "7", "6", "5", "4", "3", "2", "1"};

    public static String getNextRank(String rank) {
        int i = getIndex(rank);
        if (i == -1) return "";
        if (i >= 7) return "";
        return ranks[i+1];
    }

    public static String getPreviousRank(String rank) {
        int i = getIndex(rank);
        if (i == -1) return "";
        if (i <= 0) return "";
        return ranks[i-1];
    }

    public static int getIndex(String rank) {
        return Arrays.asList(ranks).indexOf(rank);
    }

    public static int getIndexReversed(String rank) {
        return Arrays.asList(ranksReversed).indexOf(rank);
    }
}
