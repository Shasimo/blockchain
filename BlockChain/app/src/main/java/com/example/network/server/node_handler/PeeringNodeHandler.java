package com.example.network.server.node_handler;

import com.example.network.data.DataType;
import com.example.network.data.Group;
import com.example.network.data.Packet;
import com.example.network.util.OnPacketReceptionHandler;

import java.net.Socket;

public class PeeringNodeHandler extends NodeHandler {
    public PeeringNodeHandler(Socket socket) {
        super(socket, Group.PEER);
    }

    public PeeringNodeHandler(Socket socket, OnPacketReceptionHandler onPacketReceptionHandler) {
        super(socket, Group.PEER);
        setOnPacketReceptionHandler(onPacketReceptionHandler);
    }

    @Override
    public void answerGreeting(Packet packet) {
        //TODO
    }

    @Override
    public void answerGoodBye(Packet packet) {
        //TODO
    }

    @Override
    public void answerBlob(Packet packet) {
        //TODO
    }

    @Override
    public void answerPeeringServerRunning(Packet packet) {
        //TODO
    }

    @Override
    public void answerPeeringSuccess(Packet packet) {
        //TODO
    }

    @Override
    public void answerIsUser(Packet packet) {
        //TODO
    }

    @Override
    public void answerSignThis(Packet packet) {
        // TODO
    }

    @Override
    public void removeNodeHandler() {
        // this code is useless but implementing it might be good practice
        Packet packet = new Packet(DataType.BLOB, this,
                String.format("this publicKey %s peering server is offline", this.getPublicKey()));
        broadcastPacket(packet);
        nodes.remove(this);
    }
}
