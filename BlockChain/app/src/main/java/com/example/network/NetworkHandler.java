package com.example.network;

import android.util.Log;
import android.util.Pair;

import com.example.network.client.RendezVousClient;

public class NetworkHandler {
    public static NetworkHandler instance;
    private static RendezVousClient rendezVousClient;

    private NetworkHandler() {
    }

    ;

    public static synchronized NetworkHandler getInstance() {
        if (instance == null) instance = new NetworkHandler();
        return instance;
    }

    public RendezVousClient getRendezVousClient() throws Exception {
        if (rendezVousClient != null) return rendezVousClient;
        else throw new Exception("no server yet");
    }

    public void setRendezVousClient(RendezVousClient rendezVousClient) {
        this.rendezVousClient = rendezVousClient;
    }

    public void connectToNetwork(Pair<String, String> publicKey, Pair<String, String> privateKey, String nickname) {
        try {
            Log.d("client","connection initialized");
            getRendezVousClient().setKeys(publicKey, privateKey);
            getRendezVousClient().setNickanme(nickname);
            getRendezVousClient().greetTheNetwork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
