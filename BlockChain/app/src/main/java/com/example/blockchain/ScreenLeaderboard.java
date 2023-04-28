package com.example.blockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.database.DBManager;
import com.example.elo.CARating;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScreenLeaderboard extends AppCompatActivity {
    DBManager db = new DBManager(this, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard);

        LinearLayout scrollViewLinearLayout = (LinearLayout) findViewById(R.id.learderboardScrollViewLinearLayout);
        int rank = 1;
        for (User player : getSortedPlayers()) {

            LinearLayout playerLayout = new LinearLayout(scrollViewLinearLayout.getContext());

            playerLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView playerRank = new TextView(playerLayout.getContext());
            TextView playerNickname = new TextView(playerLayout.getContext());
            TextView playerElo = new TextView(playerLayout.getContext());
            TextView playerCA = new TextView(playerLayout.getContext());


            playerRank.setText(Integer.toString(rank));
            playerRank.setPadding(16,0,16,0);
            rank++;

            playerNickname.setText(player.getPseudo());
            playerNickname.setPadding(16,0,32,0);
            playerElo.setText(Integer.toString((int) player.getEloValue()));
            playerCA.setPadding(16, 0, 48, 0);

            playerCA.setPadding(16,0,64,0);
            playerCA.setText(Double.toString(CARating.getInstance().getFourFirstDigit(db.getCAPlayer(player.getPseudo()))));

            playerLayout.addView(playerRank);
            playerLayout.addView(playerNickname);
            playerLayout.addView(playerElo);
            playerLayout.addView(playerCA);

            scrollViewLinearLayout.addView(playerLayout);
        }
    }

    private ArrayList<User> getSortedPlayers() {
        DBManager dbManager = new DBManager(this, this);
        ArrayList<User> listOfOrderedPlayers = dbManager.fetchAllUsers();
        listOfOrderedPlayers.sort((user, t1) -> Float.compare((int)user.getEloValue(), (int)t1.getEloValue()));
        Collections.reverse(listOfOrderedPlayers);
        return listOfOrderedPlayers;
    }

    public void returnToSecondScreen(View v){
        Intent intent = new Intent(ScreenLeaderboard.this, SecondScreen.class);
        startActivity(intent);
    }
}