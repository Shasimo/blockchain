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
        if(getIntent().getStringExtra("extra") != null) {
            Global.getInstance().nickname = getIntent().getStringExtra("extra");
            Global.getInstance().publicKey = db.getPlayerPublicKey(Global.getInstance().nickname);
            Global.getInstance().privateKey = db.getPrivateKey();
            Global.getInstance().publicKeyStr = String.format("%s;%s", Global.getInstance().publicKey.first, Global.getInstance().publicKey.second);
            Global.getInstance().privateKeyStr = String.format("%s;%s", Global.getInstance().privateKey.first, Global.getInstance().privateKey.second);
        }
        ((TextView)findViewById(R.id.welcomeTextView)).setText(String.format("Bienvenue %s", Global.getInstance().nickname));
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
