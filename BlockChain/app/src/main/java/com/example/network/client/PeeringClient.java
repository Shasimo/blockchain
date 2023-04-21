package com.example.network.client;

import android.util.Log;

import com.example.network.data.JsonHandler;
import com.example.network.util.OnPacketReceptionHandler;
import com.example.network.data.DataType;
import com.example.network.data.Packet;

import java.io.IOException;
import java.net.Socket;

public class PeeringClient extends Client {
    @Override
    public void greetTheNetwork(){
        try {
            Packet packet = new Packet(DataType.GREETING, this, String.format("this publicKey %s is now your peer", getPublicKeyRepr()));
            Log.d("peeringPacketSent", JsonHandler.encode(packet));
            sendPacket(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PeeringClient(Socket socket, String publicKey, OnPacketReceptionHandler onPacketReceptionHandler) {
        super(socket, publicKey, onPacketReceptionHandler);
        greetTheNetwork();
    }

    @Override
    public void answerAlive(Packet packet) {

    }

    @Override
    public void answerAdvertisement(Packet packet) {

    }

    @Override
    public void answerPeeringRequest(Packet packet) {

    }

    @Override
    public void answerStartPeeringServer(Packet packet) {

    }

    @Override
    public void answerNewBlock(Packet packet) {

    }

    @Override
    public void answerSignThis(Packet packet) {

    }
}
