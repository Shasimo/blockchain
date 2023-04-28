
package com.example.database;

// Constantes

import com.example.blockchain.User;

import static com.example.database.DBConst.*;
import static com.example.blockchain.ConstForTesting.*;
import static com.example.database.QueryHelper.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.example.database.DBErrorManager.*;

import android.database.Cursor;
import android.util.Log;

import com.example.blockchain.PopUp.*;
// Database Packages
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;

import com.example.block.Transaction;

// Constantes

public class DBManager extends SQLiteOpenHelper {
    // creating a constant variables for our database.
    // below variable is for our database name.
    private AppCompatActivity activity;
    private Boolean activityIsSet = false;

    public DBManager(Context context, AppCompatActivity activity) {
        super(context, DB, null, DB_V);
        this.activity = activity;
        if (activity != null) {
            activityIsSet = true;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //executes Query on creation
//        Log.d("dbTAG", BLOCKS_QUERY);
        db.execSQL(PLAYERS_QUERY_CA);
        db.execSQL(TRANSACTION_QUERY_CA);
        db.execSQL(BLOCKS_QUERY);
        Log.d("dbTAG", "db creation success");

    }

    public Boolean playerExists(String pseudo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(playerPublicKeyQuery(pseudo), null, null);
        return cursor.getCount() > 0;
    }

    public Boolean transactionExists(Transaction transaction) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("databaseError", transaction.getId());
//        Cursor cursor = db.rawQuery(String.format("select * from Transac where transacID=%s", transaction.getId()), null, null);
        Cursor cursor = db.rawQuery("select * from Transac where transacID=?", new String[]{transaction.getId()});
        return cursor.getCount() > 0;
    }

    //Method to insert new Player into Player Table
    public Boolean insertNewPlayer(User user, Pair<String, String> privateKeyUnformatted) {
        SQLiteDatabase db = this.getWritableDatabase();
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
        cv.put(PLAYERS_REFEREE, user.getElo().getRefereeElo());
        cv.put(PLAYERS_PSEUDO, user.getPseudo());
        cv.put(PLAYERS_P_KEYS, privateKeyFormatted);
        cv.put(PLAYERS_CA, user.getCAValue());
        long newRow = db.insert(PLAYERS_TABLE, null, cv);
        return true;
    }

    //Method to update ELO in Player table
    public void updatePlayerElo(float newELO, Pair<String, String> publicKeyUnformatted) {
        String publicKey = keyConcatenationQuery(publicKeyUnformatted);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PLAYERS_ID, publicKey);
        cv.put(PLAYERS_ELO, newELO);
        db.update(PLAYERS_TABLE, cv, "playerId = ?", new String[]{publicKey});
        Log.d("dbTAG", "db player update successful");
    }

    public void updatePlayerCA(double newCA, String pseudo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PLAYERS_PSEUDO, pseudo);
        cv.put(PLAYERS_CA, newCA);
        db.update(PLAYERS_TABLE, cv, "pseudo = ?", new String[]{pseudo});
    }


    public void resetOverallElo() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PLAYERS_ELO, 100);
        cv.put(PLAYERS_REFEREE, 0);
        db.update(PLAYERS_TABLE, cv, null, null);
    }

    public void resetOverallCA() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PLAYERS_CA, (float) 0.5);
        db.update(PLAYERS_TABLE, cv, null, null);
    }

    public float getPlayerElo(Pair<String, String> publicKeyUnformatted) {
        String publicKey = keyConcatenationQuery(publicKeyUnformatted);
        String elo = null;
        SQLiteDatabase db = this.getWritableDatabase();
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

    public double getCAPlayer(String pseudo) {
        String ca = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(playerCAQuery(pseudo), null, null);
        while (cursor.moveToNext()) {
            ca = cursor.getString(0);
        }
        if (ca == null) {
            return -1;
        }
        cursor.close();
        return Double.parseDouble(ca);
    }


    public float getPlayerRefereeElo(Pair<String, String> publicKeyUnformatted) {
        String publicKey = keyConcatenationQuery(publicKeyUnformatted);
        String elo = null;
        SQLiteDatabase db = this.getWritableDatabase();
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

    public ArrayList<User> fetchAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery("Select * from Players", null, null);
        while (cursor.moveToNext()) {
            Pair<String, String> player1Id = new Pair<>(cursor.getString(0).split(";")[0], cursor.getString(0).split(";")[1]);
            User player1 = new User(getPlayerPseudo(player1Id), player1Id);
            player1.getElo().setElo(getPlayerElo(player1Id));
            player1.getElo().setRefereeElo(getPlayerRefereeElo(player1Id));
            users.add(player1);
        }
        return users;
    }

    public ArrayList<String> fetchAllUsersNickname() {
        ArrayList<User> users = fetchAllUsers();
        ArrayList<String> usersNickname = new ArrayList<>();
        for(User user: users) {
            usersNickname.add(user.getPseudo());
        }
        return usersNickname;
    }

    public String getPlayerPseudo(Pair<String, String> publicKeyUnformatted) {
        String publicKey = keyConcatenationQuery(publicKeyUnformatted);
        String pseudo = "";
        SQLiteDatabase db = this.getWritableDatabase();
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

    public Pair<String, String> getPlayerPublicKey(String pseudo) {
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println(pseudo);
        Cursor cursor = db.rawQuery(playerPublicKeyQuery(pseudo), null, null);
//        while (cursor.moveToNext()) {
        cursor.moveToNext();
        Log.d(DB_TAG, "publicKey from cursor: " + cursor.getString(0));
        String publicKeyUnformatted = cursor.getString(0);
        System.out.println(publicKeyUnformatted);
        String[] publicKeyArray = publicKeyUnformatted.split(";");
        System.out.println(publicKeyArray.length);
//        }
//        Pair<String, String> publicKey = new Pair<>(publicKeyArray[0], publicKeyArray[1]);

        cursor.close();
        return new Pair<>(publicKeyArray[0], publicKeyArray[1]);
    }


    public String playerExists() {
        String res = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(PLAYER_EXISTS_QUERY, null, null);
        while (cursor.moveToNext()) {
            Log.d(DB_TAG, "pseudo from cursor: " + cursor.getString(0));
            res = cursor.getString(0);
        }
        cursor.close();
        return res;
    }





    public void insertNewTransac(Transaction transaction) {
        Log.d(DB_TAG, "--->>>>>>insertNewTransac entered");
        Log.d(DB_TAG, "player1Pseudo: " + transaction.getPlayer1().getPseudo());
        Log.d(DB_TAG, "player2Pseudo: " + transaction.getPlayer2().getPseudo());
        Log.d(DB_TAG, "player1: " + transaction.getPlayer1().getPublicKeyString());
        Log.d(DB_TAG, "player2: " + transaction.getPlayer2().getPublicKeyString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TRANSACTION_ID, transaction.getId());
        cv.put(TRANSACTION_TIMESTAMP, transaction.getTimeStamp());
        cv.put(TRANSACTION_PLAYER_SIGNATURE_PLAYER_ONE, transaction.getPlayer1Signature());
        cv.put(TRANSACTION_PLAYER_SIGNATURE_PLAYER_TWO, transaction.getPlayer2Signature());
        cv.put(TRANSACTION_REFEREE_SIGNATURE, transaction.getRefereeSignature());
        cv.put(TRANSACTION_PLAYER_ONE_ID, transaction.getPlayer1().getPublicKeyString());
        cv.put(TRANSACTION_PLAYER_TWO_ID, transaction.getPlayer2().getPublicKeyString());
        cv.put(TRANSACTION_REFEREE_ID, transaction.getReferee().getPublicKeyString());
        cv.put(TRANSACTION_WINNER, transaction.getWinner().getPublicKeyString());
        cv.put(TRANSACTION_PLAYER_ONE_MATCH_FAIR, transaction.isMatchFairForPlayer1());
        cv.put(TRANSACTION_PLAYER_TWO_MATCH_FAIR, transaction.isMatchFairForPlayer2());
        long newRow = db.insert(TRANSACTION_TABLE, null, cv);
    }

    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery("Select * from Transac", null, null);
        while (cursor.moveToNext()) {

            Pair<String, String> player1Id = new Pair<>(cursor.getString(2).split(";")[0], cursor.getString(2).split(";")[1]);
            Pair<String, String> player2Id = new Pair<>(cursor.getString(4).split(";")[0], cursor.getString(4).split(";")[1]);
            Pair<String, String> refereeId = new Pair<>(cursor.getString(6).split(";")[0], cursor.getString(6).split(";")[1]);
            Pair<String, String> winnerPublicKey = new Pair<>(cursor.getString(7).split(";")[0], cursor.getString(7).split(";")[1]);

            User player1 = new User(getPlayerPseudo(player1Id), player1Id);
            User player2 = new User(getPlayerPseudo(player2Id), player2Id);
            User referee = new User(getPlayerPseudo(refereeId), player2Id);
            User winner = new User(getPlayerPseudo(winnerPublicKey), winnerPublicKey);

            int p1Fairness = Integer.parseInt(cursor.getString(8));
            int p2Fairness = Integer.parseInt(cursor.getString(8));
            System.out.println("ATTENTION0: " + p1Fairness + " " + p2Fairness);

            boolean isFairP1 = (p1Fairness == 1);
            boolean isFairP2 = (p2Fairness == 1);
            System.out.println("ATTENTION: " + isFairP1 + " " + isFairP2);

            int indexOfTimestamp = cursor.getColumnIndex("timestamp");
            long timestamp = Long.parseLong(cursor.getString(indexOfTimestamp));



            Transaction transaction = new Transaction(player1, player2, referee, winner, cursor.getString(cursor.getColumnIndexOrThrow(TRANSACTION_REFEREE_SIGNATURE)), null, null, isFairP1, isFairP2, timestamp);

            int player1SignatureIndex = cursor.getColumnIndex(TRANSACTION_PLAYER_SIGNATURE_PLAYER_ONE);
            int player2SignatureIndex = cursor.getColumnIndex(TRANSACTION_PLAYER_SIGNATURE_PLAYER_TWO);

            if (player1SignatureIndex != -1)
                transaction.setPlayer1Signature(cursor.getString(player1SignatureIndex));
            if (player2SignatureIndex != -1)
                transaction.setPlayer2Signature(cursor.getString(player2SignatureIndex));

            Log.d("getAllTransactions", transaction.getPlayer1().getPseudo());

            transactions.add(transaction);

        }
        return transactions;
    }

    public void updateFairnessTransaction(Transaction transaction, boolean isAcceptedByP1, boolean isAcceptedByP2) {
        if (!transactionExists(transaction)) return;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        if(isAcceptedByP1) {
            contentValues.put(TRANSACTION_PLAYER_ONE_MATCH_FAIR, true);
        }
        if(isAcceptedByP2) {
            contentValues.put(TRANSACTION_PLAYER_TWO_MATCH_FAIR, true);
        }

        String selection = TRANSACTION_ID + " LIKE ?";
        String[] selectionArgs = {transaction.getId()};

        db.update(TRANSACTION_TABLE, contentValues, selection, selectionArgs);
    }

    public void updateTransaction(Transaction transaction) {
        if (!transactionExists(transaction)) return;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTION_PLAYER_SIGNATURE_PLAYER_ONE, transaction.getPlayer1Signature());
        contentValues.put(TRANSACTION_PLAYER_SIGNATURE_PLAYER_TWO, transaction.getPlayer2Signature());


        String selection = TRANSACTION_ID + " LIKE ?";
        String[] selectionArgs = {transaction.getId()};

        db.update(TRANSACTION_TABLE, contentValues, selection, selectionArgs);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public Pair<String, String> getPrivateKey() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select privateKey from Players where privateKey is not null", null);
        cursor.moveToNext();
        String[] formattedPrivateKey = cursor.getString(0).split(";");
        cursor.close();
        return new Pair<String, String>(formattedPrivateKey[0], formattedPrivateKey[1]);
    }

}