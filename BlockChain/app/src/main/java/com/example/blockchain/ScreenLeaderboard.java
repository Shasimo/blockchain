package com.example.blockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.database.DBManager;

import java.util.ArrayList;
import java.util.Comparator;

public class ScreenLeaderboard extends AppCompatActivity {

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


            playerRank.setText(Integer.toString(rank));
            playerRank.setPadding(16,0,16,0);
            rank++;

            playerNickname.setText(player.getPseudo());
            playerNickname.setPadding(16,0,32,0);
            playerElo.setText(Float.toString(player.getEloValue()));

            playerLayout.addView(playerRank);
            playerLayout.addView(playerNickname);
            playerLayout.addView(playerElo);

            scrollViewLinearLayout.addView(playerLayout);
        }
    }

    private ArrayList<User> getSortedPlayers() {
        DBManager dbManager = new DBManager(this, this);
        ArrayList<User> listOfOrderedPlayers = dbManager.fetchAllUsers();
        listOfOrderedPlayers.sort(new Comparator<User>() {
            @Override
            public int compare(User user, User t1) {
                return Float.compare(user.getEloValue(), t1.getEloValue());
            }
        });
        return listOfOrderedPlayers;
    }
}