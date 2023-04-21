package com.example.blockchain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ScreenSubmittedPlayer extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.submitted_screen_player);
    //We need to set this activity in AndroidManifest.xml as well
    }

    public void returnSecondScreenSubmittedPlayer(View v){
        Intent intent = new Intent(ScreenSubmittedPlayer.this, SecondScreen.class);
        startActivity(intent);
    }
}
