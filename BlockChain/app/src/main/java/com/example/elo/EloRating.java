package com.example.elo;

import com.example.block.Transaction;
import com.example.blockchain.User;
import com.example.database.DBManager;

/*
 * Here are three steps to generating Elo ratings:
 * Expected score: predict the outcome of a game.
 * Actual score: observe the outcome.
 * Update: increase or decrease each player’s rating based on result.
 *
 * */
public class EloRating{

    private static EloRating eloRatingInstance = null;

    //Expected Score : E_{A} described below.
    private static float Probability(float rating1, float rating2) {
        return 1.0f / (1 + (float) (Math.pow(10, (rating1 - rating2) / 400)));
    }

    //K is a constant that can be changed but normally it has a value of 30
    public static void Rating(Transaction transaction, float k, double m, DBManager db) {
        float player1Elo = db.getPlayerElo(transaction.getPlayer1().getPublicKey());
        float player2Elo = db.getPlayerElo(transaction.getPlayer2().getPublicKey());

        double player1CA = m * CARating.getInstance().getFourFirstDigit(db.getCAPlayer(transaction.getPlayer1().getPseudo()));
        double player2CA = m * CARating.getInstance().getFourFirstDigit(db.getCAPlayer(transaction.getPlayer2().getPseudo()));

        User player1 = transaction.getPlayer1();
        User player2 = transaction.getPlayer2();

        String player1Name = player1.getPseudo();
        String winnerName = transaction.getWinner().getPseudo();


        // To calculate the Winning
        // Probability of Player 2
        float Pb = Probability(player1Elo, player2Elo);

        // Probability of Player 1
        //Pa = E_{A} = 1 / 1 + 10^((R_{B} - R_{A} ) / D ) where D has the value 400 in most of the elo systems but it can be changed.
        float Pa = Probability(player2Elo, player1Elo);

        //Case where Player 1 won the game
        // If a player with higher ELO rating wins, only a few points are transferred from the lower rated player.
        // However if lower rated player wins, then transferred points from a higher rated player are far greater.
        // S_{a} = {1 if Player 1 won , 0 otherwise } and we have R_{A}' = R_{A} + K * (S_{A} - E_{A}) for R_{B}' is the same but we replace R_{A} with R_{B}
        if(winnerName.equals(player1Name)){
            db.updatePlayerElo((float) ((player1Elo + k * (1-Pa)) * player1CA), player1.getPublicKey());
            //We don't allow negative elo rating, so we do a quick check on the loser's new elo to not be < 0
            if((player2Elo + k * (0-Pb)) >= 0){
                db.updatePlayerElo(player2Elo + k * (0-Pa), player2.getPublicKey());
            }else{
                db.updatePlayerElo(0, player2.getPublicKey());
            }

        }else{
            if((player1Elo + k * (0-Pa)) >= 0)
                db.updatePlayerElo(player1Elo + k * (0-Pa), player1.getPublicKey());
            else
                db.updatePlayerElo(0, player1.getPublicKey());
            db.updatePlayerElo((float) ((player2Elo + k * (1-Pb)) * player2CA), player2.getPublicKey());
        }



    }

    // Static method to create instance of Singleton class
    public static EloRating getInstance()
    {
        if (eloRatingInstance == null)
            eloRatingInstance = new EloRating();

        return eloRatingInstance;
    }


}

