package com.example.blockchain;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.block.Chain;
import com.example.block.Transaction;
import com.example.database.DBManager;
import com.example.network.NetworkHandler;
import com.example.security.RSA;

import java.util.ArrayList;


public class ScreenValidateAsPlayer extends AppCompatActivity {
    ArrayList<Transaction> listOfCheckedTransactions = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_validate_as_player);
        DBManager db = new DBManager(this, this);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.transactionLayout);

        for (Transaction transaction : db.getAllTransactions()) {
            logTransaction(transaction);
            addTransactionToLayout(linearLayout, transaction);
        }

    }

    private void addTransactionToLayout(LinearLayout linearLayout, Transaction transaction) {
        if (transaction.getPendingSignaturePlayerList().contains(Global.getInstance().publicKeyRepr))
            linearLayout.addView(getTransactionRepresentation(transaction));
    }

    private void logTransaction(Transaction transaction) {
        // todo extract to transaction if time else don't care
        Log.d("matchValidation", String.format("player1.signature : %s", transaction.getPlayer1Signature()));
        Log.d("matchValidation", String.format("player2.signature : %s", transaction.getPlayer2Signature()));
        Log.d("matchValidation", String.format("player1 pk : %s", transaction.getPlayer1().getPublicKeyString()));
        Log.d("matchValidation", String.format("player2 pk : %s", transaction.getPlayer2().getPublicKeyString()));
        Log.d("matchValidation", String.format("player1 username : %s", transaction.getPlayer1().getPseudo()));
        Log.d("matchValidation", String.format("player2 username : %s", transaction.getPlayer2().getPseudo()));
    }

    public void signAllChecked(View view) {
        RSA rsa = new RSA(Global.getInstance().publicKey, Global.getInstance().privateKey);
        DBManager dbManager = new DBManager(this, this);
        Chain chain = new Chain();

        int numberOfValidatedTransactions = listOfCheckedTransactions.size();   // might be handy for visualization

        for (Transaction transaction : listOfCheckedTransactions) {
            if (transaction.getPlayer1().getPublicKeyString().equals(Global.getInstance().publicKeyRepr))
                transaction.setPlayer1Signature(rsa.EncryptUsingPrivate(Long.toString(transaction.getTimeStamp())));
            else
                transaction.setPlayer2Signature(rsa.EncryptUsingPrivate(Long.toString(transaction.getTimeStamp())));

            dbManager.updateTransaction(transaction);
            try {
                NetworkHandler.getInstance().getRendezVousClient().sendTransactionToSign(transaction);
            } catch (Exception e) {
                Log.d("matchValidation", "transaction sending failed");
                e.printStackTrace();
            }
        }
        DBManager db = new DBManager(this, this);
        chain.updateAllFromTransactions(db);

        Intent intent = new Intent(ScreenValidateAsPlayer.this, TransactionValidationSuccess.class);
        startActivity(intent);

    }

    private View getTransactionRepresentation(Transaction transaction) {
        LinearLayout linearLayout = new LinearLayout(findViewById(R.id.transactionLayout).getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        String player1Nickname = transaction.getPlayer1().getPseudo();
        String player2Nickname = transaction.getPlayer2().getPseudo();

        TextView players = new TextView(linearLayout.getContext());
        players.setText(String.format("Joueurs : %s vs %s", player1Nickname, player2Nickname));

        TextView winner = new TextView(linearLayout.getContext());
        winner.setText(String.format("Gagnant : %s", transaction.getWinner().getPseudo()));

        CheckBox isLegit = new CheckBox(linearLayout.getContext());
        isLegit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) listOfCheckedTransactions.add(transaction);
                else try {
                    listOfCheckedTransactions.remove(transaction);
                } catch (Exception e) {
                    // todo nothing
                }
            }
        });

        linearLayout.addView(players);
        linearLayout.addView(winner);
        linearLayout.addView(isLegit);

        return linearLayout;
    }
}