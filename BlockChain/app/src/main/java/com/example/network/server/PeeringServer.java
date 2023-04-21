package com.example.network.server;

import com.example.network.server.node_handler.PeeringNodeHandler;
import com.example.network.util.OnPacketReceptionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PeeringServer extends Server{
    private static volatile OnPacketReceptionHandler onPacketReceptionHandler;
    private PeeringNodeHandler firstPeeringNodeHandler;

    public PeeringServer(ServerSocket serverSocket) {
        super(serverSocket);
    }
    public PeeringServer(ServerSocket serverSocket, OnPacketReceptionHandler onPacketReceptionHandler) {
        super(serverSocket);
        setOnPacketReceptionHandler(onPacketReceptionHandler);
    }

    @Override
    protected void handleNode(Socket socket) {
        PeeringNodeHandler peeringNodeHandler = new PeeringNodeHandler(socket, onPacketReceptionHandler);
        if (firstPeeringNodeHandler==null) firstPeeringNodeHandler = peeringNodeHandler;
        Thread thread = new Thread(peeringNodeHandler);
        thread.start();
    }

    public static OnPacketReceptionHandler getOnPacketReceptionHandler() {
        return onPacketReceptionHandler;
    }

    public static void setOnPacketReceptionHandler(OnPacketReceptionHandler onPacketReceptionHandler) {
        PeeringServer.onPacketReceptionHandler = onPacketReceptionHandler;
    }

    public PeeringNodeHandler getFirstPeeringNodeHandler() {
        return firstPeeringNodeHandler;
    }
}
