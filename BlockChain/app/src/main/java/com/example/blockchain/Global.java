package com.example.blockchain;

import android.util.Pair;

public class Global {
    //https://stackoverflow.com/questions/1944656/android-global-variable/
    private static Global mInstance= null;

    public String nicknameG;
    public String publicKeyRepr;

    public Pair<String, String> publicKey;
    public Pair<String, String> privateKey;

    protected Global(){}

    public static synchronized Global getInstance() {
        if(null == mInstance){
            mInstance = new Global();
        }
        return mInstance;
    }
}

