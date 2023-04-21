package com.example.block;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blockchain.User;
import com.example.database.DBManager;
import com.example.elo.EloRating;

import java.util.ArrayList;
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

    public void calculateScore() {
        for (int i = 0; i < blockChain.size(); i++) {
            blockChainScore += blockChain.get(i).getScore();
        }
    }

    public int getBlockChainScore() {
        return blockChainScore;
    }


    public void updateOverallElo() {
        db.resetOverallElo();
        for (Block block : blockChain) {
            // todo not used for now

            //            updateAllFromTransactions(block);
        }
    }

    public void updateAllFromTransactions(DBManager db) {
        ArrayList<User> users = db.fetchAllUsers();

        db.resetOverallElo();
        for (Transaction transaction : db.getAllTransactions()) {
            if (transaction.getPendingSignaturePlayerList().isEmpty()) {
                //Calculate player1 and player2 scores and increase the referee's score as well.
                EloRating.getInstance().Rating(transaction,30, db);
                //Recuperate the actual elo from db.
//                Log.d("chain", "player1 : " + transaction.getPlayer1().getPseudo());
//                Log.d("chain", "player2 : " + transaction.getPlayer2().getPseudo());
//                Log.d("chain", "winner : " + transaction.getWinner().getPseudo());


//                float player1Elo = db.getPlayerElo(transaction.getPlayer1().getPublicKey());
//                float player2Elo = db.getPlayerElo(transaction.getPlayer2().getPublicKey());
//
//                Log.d("chain", "player1 elo : " + player1Elo);
//                Log.d("chain", "player2 elo : " + player2Elo);

                //Add the new score to the current score.
//                player1Elo += transaction.getPlayer1().getElo().getElo();
//                player2Elo += transaction.getPlayer2().getElo().getElo();
//                transaction.getReferee().getElo().increaseRefereeElo();
//
//                //Update scores in db with the new scores.
//                db.updatePlayerElo(player1Elo, transaction.getPlayer1().getPublicKey());
//                db.updatePlayerElo(player2Elo, transaction.getPlayer2().getPublicKey());
//                db.updateRefereeElo(transaction.getReferee().getElo().getRefereeElo(), transaction.getReferee().getPublicKey());
            }
        }
    }

    public void updateDB() {
        //Update the db
    }

    public void fetchDB() {
        //fetch the db
    }


}
