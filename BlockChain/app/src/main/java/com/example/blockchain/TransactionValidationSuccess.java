package com.example.blockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class TransactionValidationSuccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_validation_success);
        Timer timer = new Timer("countdown until menu swap");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(TransactionValidationSuccess.this, SecondScreen.class);
                startActivity(intent);
            }
        };
        timer.schedule(timerTask, 1000L);
    }
}