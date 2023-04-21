package com.example.network.client;

import android.util.Log;
import android.util.Pair;

import com.example.blockchain.User;
import com.example.network.client.util.ClientPacketHandler;
import com.example.network.util.OnPacketReceptionHandler;
import com.example.network.data.DataType;
import com.example.network.data.JsonHandler;
import com.example.network.data.Packet;
import com.example.security.RSA;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public abstract class Client implements Runnable, ClientPacketHandler {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String publicKeyRepr;
    private Pair<String, String> publicKey;
    private Pair<String, String> privateKey;
    private String nickanme;

    public volatile static OnPacketReceptionHandler onPacketReceptionHandler;   // callback when a packet is received

    public Client(Socket socket, String publicKeyRepr, OnPacketReceptionHandler onPacketReceptionHandler) {
        this(socket, onPacketReceptionHandler);
        setPublicKeyRepr(publicKeyRepr);
    }

    public Client(Socket socket, OnPacketReceptionHandler onPacketReceptionHandler) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            this.terminate(socket, bufferedReader, bufferedWriter);
        }
        setOnPacketReceptionHandler(onPacketReceptionHandler);
    }

    public void setKeys(Pair<String, String> publicKey, Pair<String, String> privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        setPublicKeyRepr(String.format("%s;%s", publicKey.first, publicKey.second));
    }

    public void greetNewNode() throws Exception {
        greetings(DataType.STAYING_LIVE);
    }

    public void greetTheNetwork() throws Exception {
        greetings(DataType.GREETING);

    }

    private void greetings(DataType dataType) throws Exception {
        if (publicKey == null | privateKey == null) throw new Exception("the keys weren't setup");
        RSA rsa = new RSA(publicKey, privateKey);
        Packet packet = new Packet(dataType);

        // the calculation are too heavy for a phone todo find a way to make it work

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String signedMessage = rsa.EncryptUsingPrivate(Long.toString(packet.getTimestamp()));
                packet.setContent(signedMessage);
                packet.setSenderPublicKey(getPublicKeyRepr());
                packet.setSender(new User(getNickanme(), getPublicKey()));
                try {
                    sendPacket(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    protected void sendPacket(Packet packet) throws IOException {
        Log.d("packetSent", JsonHandler.encode(packet));
        this.bufferedWriter.write(JsonHandler.encode(packet) + "\n");
        this.bufferedWriter.flush();
    }

    protected void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String receivedMessage;
                while (socket.isConnected()) {
                    try {
                        receivedMessage = bufferedReader.readLine();
                        Packet receivedPacket = (Packet) JsonHandler.decode(receivedMessage, Packet.class);

                        if (onPacketReceptionHandler != null) { // TODO making an eventHandler per type of message
                            onPacketReceptionHandler.onPacketReceptionEvent(receivedPacket);    // fine for now
                        }
                        if (receivedPacket == null) throw new IOException();
                        switch (receivedPacket.getType()) {
                            case ALIVE:
                                answerAlive(receivedPacket);
                            case ADVERTISEMENT:
                                answerAdvertisement(receivedPacket);
                            case PEERING_REQUEST:
                                answerPeeringRequest(receivedPacket);
                            case START_PEERING_SERVER:
                                answerStartPeeringServer(receivedPacket);
                            case NEW_BLOCK: {
                            }
                            case WHO_ARE_YOU: {
                            }
                            case IS_HE: {
                            }
                            case BLOB: {
                            }
                            case SIGN_THIS:
                                answerSignThis(receivedPacket);
                        }
                    } catch (IOException e) {
                        terminate(socket, bufferedReader, bufferedWriter);
                    } catch (Exception e) {
                        // TODO : do something for invalid data type
                    }
                }
            }
        }).start();
    }

    private void terminate(Socket socket, BufferedReader bufferedReader, BufferedWriter printWriter) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();

            }
            if (printWriter != null) {
                printWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPublicKeyRepr() {
        return publicKeyRepr;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setPublicKeyRepr(String publicKeyRepr) {
        this.publicKeyRepr = publicKeyRepr;
    }

    @Override
    public void run() {
        listenForMessage();
    }

    public void setOnPacketReceptionHandler(OnPacketReceptionHandler onPacketReceptionHandler) {
        Client.onPacketReceptionHandler = onPacketReceptionHandler;
    }

    public OnPacketReceptionHandler getOnPacketReceptionHandler() {
        return onPacketReceptionHandler;
    }

    public Pair<String, String> getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(Pair<String, String> publicKey) {
        this.publicKey = publicKey;
    }

    public Pair<String, String> getPrivateKey() {
        return privateKey;
    }

    public String getNickanme() {
        return nickanme;
    }

    public void setNickanme(String nickanme) {
        this.nickanme = nickanme;
    }
}
