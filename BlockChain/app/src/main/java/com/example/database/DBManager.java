
package com.example.database;

// Constantes

import com.example.blockchain.User;

import static com.example.database.DBConst.*;
import static com.example.blockchain.ConstForTesting.*;
import static com.example.database.QueryHelper.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        db.execSQL(PLAYERS_QUERY);
        db.execSQL(TRANSACTION_QUERY);
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

    public void updateRefereeElo(float newElo, Pair<String, String> publicKeyUnformatted) {
        String publicKey = keyConcatenationQuery(publicKeyUnformatted);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PLAYERS_ID, publicKey);
        cv.put(PLAYERS_REFEREE, newElo);
        db.update(PLAYERS_TABLE, cv, "playerId = ?", new String[]{publicKey});
    }

    public void resetOverallElo() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PLAYERS_ELO, 0);
        cv.put(PLAYERS_REFEREE, 0);
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
        Cursor cursor = db.rawQuery(playerPublicKeyQuery(pseudo), null, null);
//        while (cursor.moveToNext()) {
        cursor.moveToNext();
        Log.d(DB_TAG, "publicKey from cursor" + cursor.getString(0));
        String publicKeyUnformatted = cursor.getString(0);
        String[] publicKeyArray = publicKeyUnformatted.split(";");
//        }
//        Pair<String, String> publicKey = new Pair<>(publicKeyArray[0], publicKeyArray[1]);

        cursor.close();
        return new Pair<>(publicKeyArray[0], publicKeyArray[1]);
    }

    public ArrayList<String> getAllPseudo() {
        ArrayList<String> pseudoList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(PLAYER_GET_ALL_PSUEDO, null, null);
        while (cursor.moveToNext()) {
            Log.d(DB_TAG, "pseudo from cursor" + cursor.getString(0));
            pseudoList.add(cursor.getString(0));
        }
        cursor.close();
        return pseudoList;
    }

    public String playerExists() {
        String res = null;
        SQLiteDatabase db = this.getWritableDatabase();
//        Log.d(DB_TAG, "select " + PLAYERS_PSEUDO + " from " + PLAYERS_TABLE + " where " + PLAYERS_ID + " is not null and " + PLAYERS_P_KEYS + " is not null");
        Cursor cursor = db.rawQuery(PLAYER_EXISTS_QUERY, null, null);
        while (cursor.moveToNext()) {
            Log.d(DB_TAG, "pseudo from cursor: " + cursor.getString(0));
            res = cursor.getString(0);
        }
        cursor.close();
        return res;
    }

    public void insertNewBlock(String transac) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("dbTAG", "Insert with transac " + transac);
        ContentValues cv = new ContentValues();
        cv.put(BLOCKS_TRANSACTION, transac);
        long newRow = db.insert(BLOCKS_TABLE, null, cv);
    }

    // TODELETE... PROBABLY ?
    public void updateBlock(String newELO, String ID) {
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("UPDATE Player SET elo = " + newELO + " Where ID = " + ID);
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

            int indexOfTimestamp = cursor.getColumnIndex("timestamp");
            long timestamp = Long.valueOf(cursor.getString(indexOfTimestamp));

            Transaction transaction = new Transaction(player1, player2, referee, winner, cursor.getString(cursor.getColumnIndexOrThrow(TRANSACTION_REFEREE_SIGNATURE)), null, null, timestamp);

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

    public void updateTransac(String id, String refereeSign, String playerSignOne, String playerSignTwo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TRANSACTION_ID, id);
        cv.put(TRANSACTION_REFEREE_SIGNATURE, refereeSign);
        cv.put(TRANSACTION_PLAYER_SIGNATURE_PLAYER_ONE, playerSignOne);
        cv.put(TRANSACTION_PLAYER_SIGNATURE_PLAYER_TWO, playerSignTwo);
        String selection = PLAYERS_ID + " LIKE ?";
        String[] selectionArgs = id.split("");
        int count = db.update(TRANSACTION_TABLE, cv, selection, selectionArgs);
        Log.d("dbTAG", "db player update successful");
    }

    //Method to delete from a table with ID condition, ID CAN BE ALSO THE ID OF A BLOCK
    public void deleteRowFromPlayerTable(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = PLAYERS_ID + " LIKE ? ";
        String[] selectionArgs = ID.split("");
        int deleted = db.delete(PLAYERS_TABLE, selection, selectionArgs);
        Log.d("dbTAG", "db player deletion successful");
    }

    public void deleteRowFromBlockTable(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = BLOCKS_ID + " LIKE ? ";
        String[] selectionArgs = ID.split("");
        int deleted = db.delete(BLOCKS_TABLE, selection, selectionArgs);
        Log.d("dbTAG", "db BLOCK deletion successful");
    }

    //Method to delete a table
    public void dropTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DELETE_QUERY + tableName);

    }

    //Method to read from a table
    public void ReadDataFromPlayerTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorP = db.rawQuery("Select * from Players", null, null);
        Cursor cursorB = db.rawQuery("Select * from Blocks", null, null);
        Cursor cursorT = db.rawQuery("Select * from Transac", null, null);
        //cursor.getString(index) will give you the info from like example row 0 : index 0 is the Id
        //index 1 is the elo
        //Then it will move to row 2 and so on
        int playerIndex = 0;
        while (cursorP.moveToNext()) {
            Log.d("dbTAG", "index: " + playerIndex + " ID is : " + cursorP.getString(0) + " elo is " + cursorP.getString(1) + "pseudo: " + cursorP.getString(2) + "key: " + cursorP.getString(3));
            playerIndex++;
        }
        cursorP.close();
        int blockIndex = 0;
        while (cursorB.moveToNext()) {
            Log.d("dbTAG", "index: " + blockIndex + " BLOCK_ID is : " + cursorB.getString(0) + " BLOCK_DETAILS " + cursorB.getString(1) + " BLOCK_TRANSACTION " + cursorB.getString(2));
            blockIndex++;
        }
        cursorB.close();
        int transacIndex = 0;
        while (cursorT.moveToNext()) {
            Log.d("dbTag", "index: " + transacIndex + " TRANSACTION_ID IS: " + cursorT.getString(0) + " TRANSACTION_PLAYER_SIGNATURE IS: " + cursorT.getString(1) + " TRANSACTION_REFEREE_SIGNATURE IS: " + cursorT.getString(2));
            transacIndex++;
        }
        cursorT.close();


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