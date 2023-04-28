package com.example.block;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blockchain.User;
import com.example.database.DBManager;
import com.example.elo.CARating;
import com.example.elo.EloRating;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Chain extends AppCompatActivity {

    //Array of all the blocks in the Chain
    private ArrayList<Block> blockChain = new ArrayList<Block>();
    private DBManager db = null;
    private int blockChainScore = 0;


    //Method to get the last block in the chain.
    public Block getLastBlock() {
        if (blockChain.isEmpty()) return null;

        return blockChain.get(blockChain.size() - 1);
    }

    public ArrayList<Block> getTheChain() {
        if (blockChain.isEmpty()) return null;

        return blockChain;
    }

    //We update the BlockChain with the chain.
    public void updateChain(Block newBlock) {
        blockChain.add(newBlock);
    }


    public void updateAllFromTransactions(DBManager db) {
        ArrayList<String> users = db.fetchAllUsersNickname();
        ArrayList<Integer> valueOfFairnessByUser = new ArrayList<>(Collections.nCopies(users.size(), 0));


        db.resetOverallElo();
        db.resetOverallCA();
        for (Transaction transaction : db.getAllTransactions()) {
            if (transaction.getPendingSignaturePlayerList().isEmpty()) {
                if (transaction.isResultAcceptedByBoth()) {
                    updateCAForMatch(db, users, valueOfFairnessByUser, transaction.getReferee(), 1);
                    updateCAForMatch(db, users, valueOfFairnessByUser, transaction.getLoser(), 1);
                    EloRating.getInstance().Rating(transaction, 30, 1.5, db);
                    //m peut être modifié. Il aide simplement à balancer le coefficient d arbitrage
                    //pour éviter de faire trop baisser les elos au début (1.5 * 0.5 = 0.75)
                }
                if (transaction.isResultUnfairForBoth()) {
                    updateCAForMatch(db, users, valueOfFairnessByUser, transaction.getReferee(), -1);
                }

                if (transaction.isResultUnfairForLoserOnly()) {
                    updateCAForMatch(db, users, valueOfFairnessByUser, transaction.getLoser(), -1);
                }
            }
        }
    }


    private void updateCAForMatch(DBManager db, ArrayList<String> usersNicknames, ArrayList<Integer> fairMatches, User user, int delta) {
        int index = usersNicknames.indexOf(user.getPseudo());
        int numberOfFairMatches = fairMatches.get(index) + delta;
        fairMatches.set(index, numberOfFairMatches);
        CARating.getInstance().Rating(db, user, numberOfFairMatches);
    }

}



