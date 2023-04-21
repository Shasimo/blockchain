package com.example.blockchain;

import androidx.appcompat.app.AppCompatActivity;
import static com.example.blockchain.ConstForTesting.*;
import static com.example.network.data.Group.PEER;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.block.Chain;
import com.example.block.Transaction;
import com.example.database.DBManager;
import com.example.database.DBPlayerManager;
import com.example.network.NetworkHandler;
import com.example.network.client.RendezVousClient;
import com.example.network.data.DataType;
import com.example.network.data.Group;
import com.example.network.server.util.Const;
import com.example.network.util.OnPacketReceptionHandler;
import com.example.network.data.JsonHandler;
import com.example.network.data.Packet;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private DBManager db = new DBManager(this, this);
    private static Timer devMenuTimer;
    private TimerTask timerTask;
    private static int devMenuCounter = 0;

    public volatile TextView serverMessageView;

    protected volatile VideoView videoView;
    protected volatile Uri uri;

    @Override
    protected void onResume() {
        super.onResume();
        // to restart the video after coming from other activity like Sing up
        this.videoView.start();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.videoView = findViewById(R.id.videoMain);
        this.uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video_mainv2);
        videoView.setVideoURI(this.uri);
        videoView.start();

        serverMessageView = findViewById(R.id.serverMessage);

        // ---------- server setup ----------

        // android cannot run network on main thread without that
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Chain chain = new Chain();
        // end of small hack
        // TODO : run the network task on a separate thread from the main one or async task
        try {
            Socket socket = new Socket(Const.RENDEZVOUS_SERVER_IP, Const.RENDEZVOUS_SERVER_PORT);   // put the ip of the server here
            db = new DBManager(this,this);
            RendezVousClient client = new RendezVousClient(socket, new OnPacketReceptionHandler() {
                @Override
                public void onPacketReceptionEvent(Packet receivedPacket) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO make a callback for each packet type or parse it here ?
                            if (receivedPacket==null) return;   // not a real fix but will do the job fine
                            String receivedPacketJson = JsonHandler.encode(receivedPacket);
                            Log.d(SERVER_TESTER_TAG, receivedPacketJson);

                            serverMessageView.setText(receivedPacketJson);

                            if (receivedPacket.getSenderGroup() == PEER) {
                                Log.d("peerTransaction", receivedPacketJson);
                                switch (receivedPacket.getType()) {
                                    case ASK_FOR_CHAIN: {
                                        ArrayList<Transaction> transactions = db.getAllTransactions();
                                        receivedPacket.getSender().getPublicKeyString();
                                        try {
                                            NetworkHandler.getInstance().getRendezVousClient().sendChain(transactions);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    }
                                    case LIST_OF_TRANSACTIONS: {
                                        for (Transaction transaction : receivedPacket.getListOfTransactions())
                                            if (db.transactionExists(transaction))
                                                db.updateTransaction(transaction);
                                            else
                                                db.insertNewTransac(transaction);
                                    }
                                }
                            } else {
                                switch (receivedPacket.getType()) {
                                    case ALIVE :
                                    case STAYING_LIVE: {
                                        String senderNickname = receivedPacket.getSender().getPseudo();
                                        if (!db.playerExists(senderNickname))
                                            db.insertNewPlayer(new User(senderNickname, receivedPacket.getSenderPublicKeyPair()),null);
                                        break;  // break is needing when the case namespace is more than one line
                                    }
                                    case SIGN_THIS : {
                                        Transaction transaction = receivedPacket.getTransaction();
                                        Log.d("transactionToSign", receivedPacketJson);

                                        if (db.transactionExists(transaction)) db.updateTransaction(transaction);
                                        else db.insertNewTransac(transaction);

                                        if (transaction.getPendingSignaturePlayerList().isEmpty())
                                            chain.updateAllFromTransactions(db);
                                        break;
                                    }
                                    default:
                                        break;
                                }
                            }

                        }
                    });
                }
            });
            NetworkHandler.getInstance().setRendezVousClient(client);
            Thread thread = new Thread(client);
            thread.start();
        } catch (IOException e) {
            // server is offline
            // TODO : display it to the user and stop the app from crashing
            serverMessageView.setText(R.string.serverIsOffline);
            e.printStackTrace();
        }
        // ---------- server setup ----------
    }


    public void goToInscriptionScreen(View v) {
        String pseudo = db.playerExists();
        Intent intent = null;
        if (pseudo != null ){
            Pair<String, String> publicKey = db.getPlayerPublicKey(pseudo);
            Log.d("client", String.format("publicKey : %s;%s",publicKey.first, publicKey.second));
            Pair<String, String> privateKey = db.getPrivateKey();
            Log.d("client", String.format("privateKey : %s;%s",privateKey.first, privateKey.second));

            // todo handle what happens when the server is offline
            NetworkHandler.getInstance().connectToNetwork(publicKey, privateKey, pseudo);
            intent = new Intent(MainActivity.this, SecondScreen.class);

            Global.getInstance().nicknameG = pseudo;
            Global.getInstance().publicKeyRepr = String.format("%s;%s",publicKey.first, publicKey.second);

            Global.getInstance().publicKey = publicKey;
            Global.getInstance().privateKey = privateKey;

            try {
                NetworkHandler.getInstance().getRendezVousClient().askForChain();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            intent = new Intent(MainActivity.this, ScreenInscription.class);
        }
        startActivity(intent);
    }

    public void goToDBTest(View v) {
        Intent intent = new Intent(MainActivity.this, DBTester.class);
        startActivity(intent);
    }

    public void sendPacket(View view) throws Exception {
        Chain chain = new Chain();
        DBManager dbManager = new DBManager(this, this);
        chain.updateAllFromTransactions(db);
    }

    public void toggleDevMenu(View view) {
        try {
            devMenuTimer.cancel();
        } catch (Exception e){
            // the first press on the button will make the app crash without this try/catch
        }
        devMenuCounter++;
        if (devMenuCounter >= 7) {
            ((Button) findViewById(R.id.dbmanager)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.packetSender)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.refreshEloOnPress)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.serverMessage)).setVisibility(View.VISIBLE);
        } else {
            devMenuTimer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    devMenuCounter = 0;
                }
            };
            devMenuTimer.schedule(timerTask, 1000L);
        }
    }

    public void refreshElo(View view) {
        Chain chain = new Chain();
        chain.updateAllFromTransactions(db);
    }
}


