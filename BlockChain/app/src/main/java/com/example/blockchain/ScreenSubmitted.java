package com.example.blockchain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class ScreenSubmitted extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submitted_screen);
        Timer timer = new Timer("toggle off error message");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(ScreenSubmitted.this, SecondScreen.class);
                startActivity(intent);
            }
        };
        timer.schedule(timerTask, 1000L);

    }

}
