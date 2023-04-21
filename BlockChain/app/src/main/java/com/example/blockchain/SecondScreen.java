package com.example.blockchain;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.example.database.DBManager;
import com.example.database.DBPlayerManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SecondScreen extends AppCompatActivity {
    private DBManager db = new DBManager(this, this);
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_screen_layout);
        db = new DBManager(this, this);
        ((TextView)findViewById(R.id.welcomeTextView)).setText(String.format("Bienvenue %s", Global.getInstance().nicknameG));
        Log.d("secondScreen", Global.getInstance().publicKeyRepr);
        Log.d("secondScreen", Global.getInstance().publicKey.first);
        Log.d("secondScreen", Global.getInstance().publicKey.second);
        Log.d("secondScreen", Global.getInstance().publicKey.second);

        Log.d("secondScreen", Global.getInstance().nicknameG);
        ((TextView)findViewById(R.id.eloTextView)).setText(String.format("Votre score ELO est de %s points", Float.toString(db.getPlayerElo(Global.getInstance().publicKey))));
    }

    //returnButton method
    public void returnToMainScreen(View v){
        Global.getInstance().nicknameG = "";
        Intent intent = new Intent(SecondScreen.this, MainActivity.class);
        startActivity(intent);
    }

    public void goToMatchSubmission(View v){
        Intent intent = new Intent(SecondScreen.this, ScreenMatchSubmission.class);
        startActivity(intent);
    }

    public void goToValidateAsPlayer(View v){
        Intent intent = new Intent(SecondScreen.this, ScreenValidateAsPlayer.class);
        startActivity(intent);
    }

    public void goToLeaderBoard(View v) {
        Intent intent = new Intent(SecondScreen.this, ScreenLeaderboard.class);
        startActivity(intent);
    }
}
