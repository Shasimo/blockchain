package com.example.blockchain;

import static com.example.blockchain.ConstForTesting.DB_TAG;
import static com.example.blockchain.PopUp.*;
import static com.example.database.DBConst.*;
import static com.example.database.DBErrorManager.*;

import com.example.block.Transaction;
import com.example.blockchain.PopUp.*;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.database.DBManager;
import com.example.database.DBPlayerManager;

import java.util.ArrayList;


// this a test file will not appear at final code only for showing test
public class DBTester extends AppCompatActivity {
    private DBPlayerManager db;
//    private RendezVousClient client = RendezVousClient.get
    EditText pID, pElo, pKey, pPseudo;
    //    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_test);
//        TableView tableView = findViewById(R.id.main_table);
        db = new DBPlayerManager(this, this);
        pID = (EditText) findViewById(R.id.userKey);
        pElo = (EditText) findViewById(R.id.userElo);
        pPseudo = (EditText) findViewById(R.id.userPseudo);
    }
    public void clickToGetDB(View v){

        Pair<String, String> shanPKey = new Pair<>("123", "456");
        Pair<String, String> bappiPKey = new Pair<>("456", "123");
        User shan = new User("Shan", shanPKey);
        User bappi = new User("Bappi", bappiPKey);
        User referee = new User("RefereeBro",new Pair<>("789","123"));
        db.insertNewPlayer(shan, null);
        db.insertNewPlayer(bappi, null);
        db.insertNewPlayer(referee,null);
        ArrayList<User> users = db.fetchAllUsers();
        Log.d("DBUSER", "clickToGetDB: "+users.get(0).getPseudo() + "," + users.get(0).getEloValue() + "," + users.get(0).getPublicKeyString());

//        db.insertNewTransac(new Transaction(shan,bappi,referee,shan,referee.getPublicKeyString(),shan.getPublicKeyString(),bappi.getPublicKeyString(), System.currentTimeMillis()));
//        ArrayList<Transaction> transactions = db.getAllTransactions();
//        Log.d("player1Transc", "clickToGetDB: "+transactions.get(0).getPlayer1().getPseudo());
//        Log.d("player1Transc", "clickToGetDB: "+transactions.get(0).getPlayer1().getEloValue());
//        Log.d("player2Transc", "clickToGetDB: "+transactions.get(0).getPlayer2().getPseudo());
//        Log.d("WinnerTransc", "clickToGetDB: "+transactions.get(0).getWinner().getPseudo());

        String exist = db.playerExists();
        Log.d(DB_TAG, "player exists " + exist);
        Toast.makeText(this, "Everything worked my man", Toast.LENGTH_SHORT).show();
//        db.ReadDataFromPlayerTable();

    }
}

