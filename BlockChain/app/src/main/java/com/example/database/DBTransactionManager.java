package com.example.database;

import static com.example.blockchain.ConstForTesting.*;
import static com.example.database.QueryHelper.*;
import static com.example.database.DBErrorManager.*;
import static com.example.database.DBConst.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;

import com.example.block.Transaction;
import com.example.blockchain.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class DBTransactionManager {
    private DBManager db;
    private Boolean activityIsSet = false;
    private AppCompatActivity activity = null;

    private Context context;

    public DBTransactionManager(Context context, AppCompatActivity activity){
            this.db = new DBManager(context, activity);
            this.activity = activity;
            this.activityIsSet = true;
            this.context = context;
        }

    public DBTransactionManager(Context context){
//        this.db = new DBManager(context);
        this.context = context;
    }

    public void insertNewTransac(Transaction transaction) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        String currentTimestamp = sdf.format(new Date());
        cv.put(TRANSACTION_ID, "1");
        cv.put(TRANSACTION_TIMESTAMP, currentTimestamp);
        cv.put(TRANSACTION_PLAYER_SIGNATURE_PLAYER_ONE, transaction.getPlayer1Signature());
        cv.put(TRANSACTION_PLAYER_SIGNATURE_PLAYER_TWO, transaction.getPlayer2Signature());
        cv.put(TRANSACTION_REFEREE_SIGNATURE, transaction.getRefereeSignature());
        cv.put(TRANSACTION_PLAYER_ONE_ID, transaction.getPlayer1().getPublicKeyString());
        cv.put(TRANSACTION_PLAYER_TWO_ID, transaction.getPlayer2().getPublicKeyString());
        cv.put(TRANSACTION_REFEREE_ID, transaction.getReferee().getPublicKeyString());
        cv.put(TRANSACTION_WINNER,transaction.getWinner().getPublicKeyString());
        long newRow = db.insert(TRANSACTION_TABLE, null, cv);
    }

    public ArrayList<Transaction> getAllTransactions(){
        DBPlayerManager dbPlayerManager = new DBPlayerManager(this.context);
        ArrayList<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery("Select * from Transac", null, null);
        while(cursor.moveToNext()){
            Pair<String,String> player1Id = new Pair<>(cursor.getString(2).split(";")[0],cursor.getString(2).split(";")[1]);
            Pair<String,String> player2Id = new Pair<>(cursor.getString(4).split(";")[0],cursor.getString(4).split(";")[1]);
            Pair<String,String> refereeId = new Pair<>(cursor.getString(6).split(";")[0],cursor.getString(6).split(";")[1]);
            Pair<String,String> winner = new Pair<>(cursor.getString(7).split(";")[0],cursor.getString(7).split(";")[1]);
            User player1 = new User(dbPlayerManager.getPlayerPseudo(player1Id),player1Id);
            User player2 = new User(dbPlayerManager.getPlayerPseudo(player2Id),player2Id);
            User referee = new User(dbPlayerManager.getPlayerPseudo(refereeId),player2Id);
            if(Objects.equals(winner.first, player1Id.first) && Objects.equals(winner.second, player1Id.second)){
//                transactions.add(new Transaction(player1,player2,referee,player1,cursor.getString(6),cursor.getString(2),cursor.getString(4)));
            }else{
//                transactions.add(new Transaction(player1,player2,referee,player2,cursor.getString(6),cursor.getString(2),cursor.getString(4)));
            }

        }
        return transactions;
    }

    public void updateTransac(String id, String refereeSign, String playerSignOne, String playerSignTwo) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TRANSACTION_ID, id);
        cv.put(TRANSACTION_REFEREE_SIGNATURE, refereeSign);
        cv.put(TRANSACTION_PLAYER_SIGNATURE_PLAYER_ONE, playerSignOne);
        cv.put(TRANSACTION_PLAYER_SIGNATURE_PLAYER_TWO, playerSignTwo);
        String selection = TRANSACTION_ID + " LIKE ?";
        String[] selectionArgs = id.split("");
        int count = db.update(TRANSACTION_TABLE, cv, selection, selectionArgs);
        Log.d("dbTAG", "db player update successful");
    }

    public void deleteRowFromBlockTable(String ID) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        String selection = BLOCKS_ID + " LIKE ? ";
        String[] selectionArgs = ID.split("");
        int deleted = db.delete(BLOCKS_TABLE, selection, selectionArgs);
        Log.d("dbTAG", "db BLOCK deletion successful");
    }
}
