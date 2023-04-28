package com.example.elo;

import com.example.block.Transaction;
import com.example.blockchain.User;
import com.example.database.DBManager;
import java.lang.Math;

public class CARating {
    private static CARating coefRating = null;


    public void Rating(DBManager db, User player, int nbFairMatchPlayed) {
        double currentCA = db.getCAPlayer(player.getPseudo());
        double newCA = computeCoefficient(getFourFirstDigit(currentCA), nbFairMatchPlayed);
        db.updatePlayerCA(getFourFirstDigit(newCA), player.getPseudo());
    }

    private double computeCoefficient(double prevScore, int nbMatchFairPlayed) {
        return (prevScore + computeFormula((nbMatchFairPlayed)))/2;
    }

    private double computeFormula(int nbMatchFairPlayed) {
        return (1 / (1 + Math.exp((nbMatchFairPlayed * -0.05))));
    }

    public static CARating getInstance()
    {
        if (coefRating == null)
            coefRating = new CARating();

        return coefRating;
    }

    public double getFourFirstDigit(double num) {
        String numStr = String.valueOf(num); // convert double to string
        if (numStr.length() >= 5) {
            numStr = numStr.substring(0, 5); // extract first four digits
        }
        return Double.parseDouble(numStr);
    }
}
