package com.example.blockchain;
import com.example.elo.Elo;
import static com.example.database.QueryHelper.keyConcatenationQuery;

import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class User implements Serializable {
    private transient Elo elo; // transient because calculated from scratch each time
    private String pseudo = null;
    private transient Pair<String, String> publicKey = null;
    private String publicKeyRepr;   // used for serialization

    private transient Pair<String, String> privateKey;
    private double scoreArbitrage = 0;

    public User(String pseudo, Pair<String, String> publicKey, Pair<String, String> privateKey) {
        elo = new Elo();
        this.pseudo = pseudo;
        this.publicKey = publicKey;
        this.privateKey = privateKey;

        this.publicKeyRepr = String.format("%s;%s", publicKey.first, publicKey.second);
    }

    public User(String pseudo, Pair<String, String> publicKey){
        elo = new Elo();
        this.pseudo = pseudo;
        this.publicKey = publicKey;

        this.publicKeyRepr = String.format("%s;%s", publicKey.first, publicKey.second);
    }

    public void setPseudo(String pseudo){
        this.pseudo = pseudo;
    }

    public void setPublicKey(Pair<String, String> publicKey){
        this.publicKey = publicKey;
        this.publicKeyRepr = String.format("%s;%s", publicKey.first, publicKey.second);
    }

    public Elo getElo(){
        return this.elo;
    }

    public float getEloValue(){
        return this.elo.getElo();
    }

    public String getPseudo(){
        return this.pseudo;
    }

    public Pair<String, String> getPublicKey(){
        return this.publicKey;
    }

    public String getPublicKeyString() {
        if (this.publicKeyRepr!=null) return this.publicKeyRepr;
        else return keyConcatenationQuery(this.publicKey);
    }


    public List<Object> getUser(){
        List<Object> user = new ArrayList<>();
        user.add(this.elo.getElo());
        user.add(this.pseudo);
        user.add(this.publicKey);
        return user;
    }
}
