package com.example.blockchain;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.VideoView;

import com.example.database.DBPlayerManager;
import com.example.network.NetworkHandler;

import com.example.database.DBManager;
import com.example.security.RSA;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ScreenInscription  extends AppCompatActivity {
    private DBPlayerManager db = new DBPlayerManager(this, this);

    protected volatile VideoView videoView ;
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
        setContentView(R.layout.inscription);
        this.videoView = findViewById(R.id.videoInscription);
        this.uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video_mainv2);
        this.videoView.setVideoURI(this.uri);
        this.videoView.start();
        //We need to set this activity in AndroidManifest.xml as well
    }

    public void goToConnectionScreen(View v){
        RSA rsa = new RSA(2048);
        EditText usernameEDT = (EditText)findViewById(R.id.inscriptionNickname);
        Global.getInstance().nicknameG = usernameEDT.getText().toString();

        Pair<String, String> publicKey = rsa.getPublicKey();
        Pair<String, String> privateKey = rsa.getPrivateKey();

        Global.getInstance().publicKeyRepr = String.format("%s;%s", publicKey.first, publicKey.second);

        Log.d("init", "public " + publicKey.first + " " + publicKey.second);
        Log.d("init", "private " + privateKey.first + " " + privateKey.second);
//
        String username = Global.getInstance().nicknameG;

        User user = new User(username, publicKey);

        if (db.insertNewPlayer(user, privateKey)) {

            Intent intent = new Intent(ScreenInscription.this, SecondScreen.class);
            startActivity(intent);

            NetworkHandler.getInstance().connectToNetwork(publicKey, privateKey, username);
        }
    }
}
