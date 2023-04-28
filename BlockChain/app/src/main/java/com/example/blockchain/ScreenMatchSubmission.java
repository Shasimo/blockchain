package com.example.blockchain;

import com.example.block.Transaction;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.CheckBox;

import com.example.database.DBManager;
import com.example.network.NetworkHandler;
import com.example.security.RSA;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScreenMatchSubmission extends AppCompatActivity {
    String referee = Global.getInstance().nickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_submission);

        //We need to set this activity in AndroidManifest.xml as well

        TextView txtPseudoArbitrage = findViewById(R.id.pseudo);
        txtPseudoArbitrage.setTypeface(null, Typeface.BOLD);
        txtPseudoArbitrage.setText(referee);

    }

    public Transaction extractTransaction() throws Exception {
        DBManager db = new DBManager(this, this);

        String player1Nickname = ((EditText) findViewById(R.id.joueur_un)).getText().toString();
        String player2Nickname = ((EditText) findViewById(R.id.joueur_deux)).getText().toString();
        String refereeNickname = referee;

        if (!db.playerExists(player1Nickname) || !db.playerExists(player2Nickname))
            throw new Exception("player not exist in db");

        Pair<String, String> refereePublicKey = db.getPlayerPublicKey(refereeNickname);
        Pair<String, String> refereePrivateKey = db.getPrivateKey();

        User player1 = new User(player1Nickname, db.getPlayerPublicKey(player1Nickname));
        User player2 = new User(player2Nickname, db.getPlayerPublicKey(player2Nickname));
        User referee = new User(refereeNickname, refereePublicKey);

        boolean player1Won = ((CheckBox) findViewById(R.id.victoireJ1)).isChecked();
        boolean player2Won = ((CheckBox) findViewById(R.id.victoireJ2)).isChecked();

        RSA rsa = new RSA(refereePublicKey, refereePrivateKey);

        if (player1Won)
            return new Transaction(player1, player2, referee, player1, rsa, System.currentTimeMillis());
        if (player2Won)
            return new Transaction(player1, player2, referee, player2, rsa, System.currentTimeMillis());
        else
            return new Transaction(player1, player2, referee, null, rsa, System.currentTimeMillis());
    }

    public Boolean checkIfFormIsFilled() {
        String player1Nickname = ((EditText) findViewById(R.id.joueur_un)).getText().toString();
        String player2Nickname = ((EditText) findViewById(R.id.joueur_deux)).getText().toString();

        if (player1Nickname.trim().length() == 0 || player2Nickname.trim().length() == 0) {
            Log.d("transctionSubmission", "player1 or player2 nicknames is/are blank");
            return false;
        }

        // todo uncomment the following lines when testing in real scenario
//        if (player1Nickname.trim().equals(Global.getInstance().nicknameG) || player2Nickname.trim().equals(Global.getInstance().nicknameG)) {
//            Log.d("transctionSubmission", "the referee is trying to send his own match");
//            return false;
//        }

        Log.d("transactionSubmission", String.format("player1 : %s\nplayer2 : %s", player1Nickname, player2Nickname));

        boolean player1Won = ((CheckBox) findViewById(R.id.victoireJ1)).isChecked();
        boolean player2Won = ((CheckBox) findViewById(R.id.victoireJ2)).isChecked();
        boolean draw = ((CheckBox) findViewById(R.id.matchNul)).isChecked();

        if ((player1Won & (player2Won || draw)) || (player2Won & (player1Won || draw)) || (!player1Won & !player2Won & !draw))
            return false;

        Log.d("transactionSubmission", "the form was correctly filled");

        return true;
    }


    public void submitABattle(View v) {
        Transaction transaction;
        try {
            transaction = extractTransaction();
            if (!checkIfFormIsFilled()) throw new Exception("bad form");
        } catch (Exception e) {
            displayBadInputError();
            return;
        }

        DBManager db = new DBManager(this, this);

        try {
            NetworkHandler.getInstance().getRendezVousClient().sendTransactionToSign(transaction);
        } catch (Exception e) {
            Log.d("transactionSubmission", "transaction sending error");
            e.printStackTrace();
        }
        db.insertNewTransac(transaction);
        goToSubmittedScreen(v);
    }

    private void displayBadInputError() {
        Log.d("transactionSubmission", "the match wasn't properly filled");
        findViewById(R.id.errorMessage).setVisibility(View.VISIBLE);
        Timer timer = new Timer("toggle off error message");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                findViewById(R.id.errorMessage).setVisibility(View.INVISIBLE);
            }
        };
        timer.schedule(timerTask, 1000L);
    }

    public void goToSubmittedScreen(View v) {
        Intent intent = new Intent(ScreenMatchSubmission.this, ScreenSubmitted.class);
        startActivity(intent);
    }

}