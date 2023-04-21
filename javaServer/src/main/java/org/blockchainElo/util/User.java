package org.blockchainElo.util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class User implements Serializable {
    private String pseudo = null;
    private transient Pair<String, String> publicKey;
    private String publicKeyRepr;   // used for serialization

    private transient Pair<String, String> privateKey;
    private transient double scoreArbitrage = 0;

    public User(String pseudo, Pair<String, String> publicKey, Pair<String, String> privateKey) {
        this.pseudo = pseudo;
        this.publicKey = publicKey;
        this.privateKey = privateKey;

        this.publicKeyRepr = String.format("%s;%s", publicKey.first, publicKey.second);
    }

    User(String pseudo, Pair<String, String> publicKey){
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



    public String getPseudo(){
        return this.pseudo;
    }

    public Pair<String, String> getPublicKey(){
        return this.publicKey;
    }

    public String getPublicKeyString() {
        return String.format("%s;%s", publicKey.first, publicKey.second);
    }
}
