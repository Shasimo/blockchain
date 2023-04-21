package com.example.database;

import static com.example.database.DBConst.*;

import android.util.Pair;

import java.util.List;

public final class QueryHelper {
    QueryHelper(){};

    public static String playerEloQuery(String id){
        return "select " + PLAYERS_ELO + " from " + PLAYERS_TABLE + " where " + PLAYERS_ID + " = '" + id + "'";
    }

    public static String playerPseudoQuery(String id){
        return "select " + PLAYERS_PSEUDO + " from " + PLAYERS_TABLE + " where " + PLAYERS_ID + " = '" + id + "'";
    }

    public static String playerPublicKeyQuery(String pseudo){
        return "select " + PLAYERS_ID + " from " + PLAYERS_TABLE + " where " + PLAYERS_PSEUDO + " = '" + pseudo + "'";
    }

    public static String playerRefereeEloQuery(String id){
        return "select " + PLAYERS_REFEREE + " from " + PLAYERS_TABLE + " where " + PLAYERS_ID + " = '" + id + "'";
    }

    public static String keyConcatenationQuery(Pair<String, String> id){
        return String.format("%s;%s", id.first, id.second);
    }

}
