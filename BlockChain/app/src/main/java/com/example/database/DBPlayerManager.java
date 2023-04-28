package com.example.database;

import static com.example.blockchain.ConstForTesting.*;
import static com.example.database.DBConst.*;
import static com.example.database.DBErrorManager.*;
import static com.example.database.QueryHelper.*;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blockchain.User;

import java.util.ArrayList;
import java.util.List;

public class DBPlayerManager {
    private DBManager db;
    private Boolean activityIsSet = false;
    private AppCompatActivity activity = null;
    public DBPlayerManager(Context context){
//        this.db = new DBManager(context);
    }

    public DBPlayerManager(Context context, AppCompatActivity activity){
        this.db = new DBManager(context, activity);
        this.activity = activity;
        this.activityIsSet = true;
    }

    public Boolean playerExists(String pseudo) {
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor cursor = db.rawQuery(playerPublicKeyQuery(pseudo), null, null);
        return cursor.getCount() > 0;
    }

    public Boolean playerExists(Pair<String, String> publicKeyUnformatted) {
        SQLiteDatabase db = this.db.getReadableDatabase();
        String publicKey = keyConcatenationQuery(publicKeyUnformatted);
        Cursor cursor = db.rawQuery(playerPublicKeyQuery(publicKey), null, null);
        return cursor.getCount() > 0;
    }

    //Method to insert new Player into Player Table
    public Boolean insertNewPlayer(User user, Pair<String,String> privateKeyUnformatted) {
        SQLiteDatabase db = this.db.getWritableDatabase();
//        Log.d("dbTAG", "Insert with elo " + playerElo + " ID " + ID);
        if (playerExists(user.getPseudo()) && activityIsSet) {
            dbErrorChecker(PLAYER_NOT_EXIST, activity, "Please choose another pseudo");
            return false;
        }
        String privateKeyFormatted = null;
        if (privateKeyUnformatted != null) {
            privateKeyFormatted = keyConcatenationQuery(privateKeyUnformatted);
        }
        ContentValues cv = new ContentValues();
        cv.put(PLAYERS_ID, user.getPublicKeyString());
        cv.put(PLAYERS_ELO, user.getEloValue());
        cv.put(PLAYERS_REFEREE,user.getElo().getRefereeElo());
        cv.put(PLAYERS_PSEUDO, user.getPseudo());
        cv.put(PLAYERS_P_KEYS, privateKeyFormatted);
        cv.put(PLAYERS_CA, user.getCAValue());
        long newRow = db.insert(PLAYERS_TABLE, null, cv);
        return true;
    }

    //Method to update ELO in Player table

    public float getPlayerElo(Pair<String, String> publicKeyUnformatted) {

        if (!playerExists(publicKeyUnformatted) && activityIsSet) {
            dbErrorChecker(KEY_NOT_EXIST, activity, null);
            return -1;
        }
        String publicKey = keyConcatenationQuery(publicKeyUnformatted);
        String elo = null;
        SQLiteDatabase db = this.db.getWritableDatabase();
//        Log.d(DB_TAG, "select " + PLAYERS_ELO + " from " + PLAYERS_TABLE + " where " + PLAYERS_ID + " = '" + id + "'");
        Cursor cursor = db.rawQuery(playerEloQuery(publicKey), null, null);
        while (cursor.moveToNext()) {
            Log.d(DB_TAG, "elo from cursor" + cursor.getString(0));
            elo = cursor.getString(0);
        }
        if (elo == null) {
            Log.d(DB_TAG, "elo is null");
            return -1;
        }
        cursor.close();
        Log.d(DB_TAG, "return elo from fonction" + elo);
        return Float.parseFloat(elo);
    }


    public float getPlayerRefereeElo(Pair<String, String> publicKeyUnformatted){
        if (!playerExists(publicKeyUnformatted) && activityIsSet) {
            dbErrorChecker(KEY_NOT_EXIST, activity, null);
            return -1;
        }
        String publicKey = keyConcatenationQuery(publicKeyUnformatted);
        String elo = null;
        SQLiteDatabase db = this.db.getWritableDatabase();
//        Log.d(DB_TAG, "select " + PLAYERS_ELO + " from " + PLAYERS_TABLE + " where " + PLAYERS_ID + " = '" + id + "'");
        Cursor cursor = db.rawQuery(playerRefereeEloQuery(publicKey), null, null);
        while (cursor.moveToNext()) {
            Log.d(DB_TAG, "elo from cursor" + cursor.getString(0));
            elo = cursor.getString(0);
        }
        if (elo == null) {
            Log.d(DB_TAG, "elo is null");
            return -1;
        }
        cursor.close();
        Log.d(DB_TAG, "return elo from fonction" + elo);
        return Float.parseFloat(elo);
    }

    public ArrayList<User> fetchAllUsers(){
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase db = this.db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery("Select * from Players", null, null);
        while(cursor.moveToNext()){
            Pair<String,String> player1Id = new Pair<>(cursor.getString(0).split(";")[0],cursor.getString(0).split(";")[1]);
            User player1 = new User(getPlayerPseudo(player1Id),player1Id);
            player1.getElo().setElo(getPlayerElo(player1Id));
            player1.getElo().setRefereeElo(getPlayerRefereeElo(player1Id));
            users.add(player1);
        }
        return users;
    }

    public String getPlayerPseudo(Pair<String, String> publicKeyUnformatted) {
        if (!playerExists(publicKeyUnformatted) && activityIsSet) {
            dbErrorChecker(KEY_NOT_EXIST, activity, null);
            return null;
        }
        String publicKey = keyConcatenationQuery(publicKeyUnformatted);
        String pseudo = "";
        SQLiteDatabase db = this.db.getWritableDatabase();
//        Log.d(DB_TAG, "select " + PLAYERS_PSEUDO + " from " + PLAYERS_TABLE + " where " + PLAYERS_ID + " = '" + id + "'");
        Cursor cursor = db.rawQuery(playerPseudoQuery(publicKey), null, null);
        while (cursor.moveToNext()) {
            Log.d(DB_TAG, "pseudo from cursor" + cursor.getString(0));
            pseudo = cursor.getString(0);
            if (pseudo == null) {
//                throw new Exception("Player does not exist"); A méditer
                return null;
            }
        }
        cursor.close();
        return pseudo;
    }


    public String playerExists() {
        String res = null;
        SQLiteDatabase db = this.db.getWritableDatabase();
//        Log.d(DB_TAG, "select " + PLAYERS_PSEUDO + " from " + PLAYERS_TABLE + " where " + PLAYERS_ID + " is not null and " + PLAYERS_P_KEYS + " is not null");
        Cursor cursor = db.rawQuery(PLAYER_EXISTS_QUERY, null, null);
        while (cursor.moveToNext()) {
            Log.d(DB_TAG, "pseudo from cursor: " + cursor.getString(0));
            res = cursor.getString(0);
        }
        cursor.close();
        return res;
    }

    public Pair<String, String> getPrivateKey() {
        SQLiteDatabase db = this.db.getWritableDatabase();
//        Cursor cursor = db.rawQuery("select privateKey from Players where privateKey is not null", null);
        Cursor cursor = db.rawQuery(PLAYERS_GET_PRIVATE_KEY, null);
        cursor.moveToNext();
        String[] formattedPrivateKey = cursor.getString(0).split(";");
        cursor.close();
        return new Pair<String, String>(formattedPrivateKey[0],formattedPrivateKey[1]);
    }
}
