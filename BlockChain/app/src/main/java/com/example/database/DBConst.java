
package com.example.database;

public final class DBConst {
    public DBConst(){}

    public static final String DB = "UserDB.db";

    public static final int DB_V = 1;
    // ----------------------------------------------------------------
    // Block DB
    public static final String BLOCKS_TABLE = "Blocks";

    public static final String BLOCKS_ID = "blockId";

    public static final String BLOCKS_TRANSACTION = "transac";

    public static final String BLOCKS_DETAILS = "details";

    // --------------------------------------------------------
    // Player DB
    public static final String PLAYERS_TABLE = "Players";

    public static final String PLAYERS_ID = "playerId";

    public static final String PLAYERS_ELO = "elo";

    public static final String PLAYERS_REFEREE = "referee";

    public static final String PLAYERS_P_KEYS = "privateKey";

    public static final String PLAYERS_PSEUDO = "pseudo";

    public static final String PLAYERS_CA = "coefficientArbitrage";

    // Transaction DB
    // ----------------------------------------------------------------
    public static final String TRANSACTION_TABLE = "Transac";

    public static final String TRANSACTION_ID = "transacID";

    public static final String TRANSACTION_PLAYER_SIGNATURE_PLAYER_ONE = "playerSignatureOfPlayerOne";

    public static final String TRANSACTION_PLAYER_ONE_ID = "playerOneId";

    public static final String TRANSACTION_PLAYER_SIGNATURE_PLAYER_TWO = "playerSignatureOfPlayerTwo";

    public static final String TRANSACTION_PLAYER_TWO_ID = "playerTwoId";

    public static final String TRANSACTION_REFEREE_SIGNATURE = "refereeSignature";

    public static final String TRANSACTION_REFEREE_ID = "refereeId";

    public static final String TRANSACTION_WINNER = "winnerId";

    public static final String TRANSACTION_TIMESTAMP = "timestamp";

    public static final String TRANSACTION_PLAYER_ONE_MATCH_FAIR = "playerOneMatchFair";

    public static final String TRANSACTION_PLAYER_TWO_MATCH_FAIR = "playerTwoMatchFair";

    // Creation Query
    public static final String BLOCKS_QUERY = "CREATE TABLE IF NOT EXISTS " + BLOCKS_TABLE + " ( "
            + BLOCKS_ID + " TEXT PRIMARY KEY, "
            + BLOCKS_TRANSACTION + " TEXT, "
            + " FOREIGN KEY(" + BLOCKS_TRANSACTION + ") REFERENCES " + TRANSACTION_TABLE + "(" + TRANSACTION_ID + ") )";
    public static final String PLAYERS_QUERY = "CREATE TABLE IF NOT EXISTS " + PLAYERS_TABLE + "("
            + PLAYERS_ID + " TEXT PRIMARY KEY, "
            + PLAYERS_ELO + " FLOAT,"
            + PLAYERS_REFEREE + " FLOAT,"
            + PLAYERS_PSEUDO + " TEXT,"
            + PLAYERS_P_KEYS + " TEXT) ";

    public static final String PLAYERS_QUERY_CA = "CREATE TABLE IF NOT EXISTS " + PLAYERS_TABLE + "("
            + PLAYERS_ID + " TEXT PRIMARY KEY, "
            + PLAYERS_ELO + " FLOAT,"
            + PLAYERS_REFEREE + " FLOAT,"
            + PLAYERS_PSEUDO + " TEXT,"
            + PLAYERS_P_KEYS + " TEXT, "
            + PLAYERS_CA + " DOUBLE) ";

    public static final String TRANSACTION_QUERY = "CREATE TABLE IF NOT EXISTS " + TRANSACTION_TABLE + " ( "
            + TRANSACTION_ID + " TEXT PRIMARY KEY, "
            + TRANSACTION_PLAYER_SIGNATURE_PLAYER_ONE + " INTEGER DEFAULT 0, "
            + TRANSACTION_PLAYER_ONE_ID + " TEXT, "
            + TRANSACTION_PLAYER_SIGNATURE_PLAYER_TWO + " INTEGER DEFAULT 0, "
            + TRANSACTION_PLAYER_TWO_ID + " TEXT, "
            + TRANSACTION_REFEREE_SIGNATURE + " INTEGER DEFAULT 0, "
            + TRANSACTION_REFEREE_ID + " TEXT, "
            + TRANSACTION_WINNER + " TEXT, "
            + TRANSACTION_TIMESTAMP + " TEXT) ";

    public static final String TRANSACTION_QUERY_CA = "CREATE TABLE IF NOT EXISTS " + TRANSACTION_TABLE + " ( "
            + TRANSACTION_ID + " TEXT PRIMARY KEY, "
            + TRANSACTION_PLAYER_SIGNATURE_PLAYER_ONE + " INTEGER DEFAULT 0, "
            + TRANSACTION_PLAYER_ONE_ID + " TEXT, "
            + TRANSACTION_PLAYER_SIGNATURE_PLAYER_TWO + " INTEGER DEFAULT 0, "
            + TRANSACTION_PLAYER_TWO_ID + " TEXT, "
            + TRANSACTION_REFEREE_SIGNATURE + " INTEGER DEFAULT 0, "
            + TRANSACTION_REFEREE_ID + " TEXT, "
            + TRANSACTION_WINNER + " TEXT, "
            + TRANSACTION_PLAYER_ONE_MATCH_FAIR + " BOOLEAN, "
            + TRANSACTION_PLAYER_TWO_MATCH_FAIR + " BOOLEAN, "
            + TRANSACTION_TIMESTAMP + " TEXT) ";

    public static final String DELETE_QUERY = "DROP TABLE IF EXISTS ";

    public static final String PLAYER_EXISTS_QUERY = "select " + PLAYERS_PSEUDO + " from " + PLAYERS_TABLE + " where " + PLAYERS_ID + " is not null and " + PLAYERS_P_KEYS + " is not null";

    public static final String PLAYER_GET_ALL_PSUEDO = "select " + PLAYERS_PSEUDO + " from " + PLAYERS_TABLE;

    public static final String PLAYERS_GET_PRIVATE_KEY = "select " +  PLAYERS_P_KEYS +  " from " + PLAYERS_TABLE + " where " + PLAYERS_P_KEYS + " is not null";

    public static final String PLAYER_GET_CA = "select " + PLAYERS_CA + " from " + PLAYERS_TABLE;
}