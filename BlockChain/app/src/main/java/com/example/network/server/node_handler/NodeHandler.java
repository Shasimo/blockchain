package com.example.network.server.node_handler;

import android.util.Log;

import com.example.network.data.*;
import com.example.network.server.util.ServerPacketHandler;
import com.example.network.util.OnPacketReceptionHandler;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;


public abstract class NodeHandler implements Runnable, ServerPacketHandler {
    public static ArrayList<NodeHandler> nodes = new ArrayList<>();
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String publicKey;
    private Group group;
    public static volatile OnPacketReceptionHandler onPacketReceptionHandler = null;

    public NodeHandler(Socket socket, Group group) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.group = group;
            nodes.add(this);
            handlePacket(bufferedReader.readLine());
        } catch (IOException e) {
            this.terminate(socket, bufferedReader, bufferedWriter);
        } catch (Exception e) {
            // TODO : same problem do something ?
        }
    }

    public NodeHandler(Socket socket, Group group, OnPacketReceptionHandler onPacketReceptionHandler) {
        this(socket, group);
        setOnPacketReceptionHandler(onPacketReceptionHandler);
    }

    @Override
    public void run() {
        String nodeMessage;
        while (socket.isConnected()) {
            try {
                nodeMessage = bufferedReader.readLine();
                if (nodeMessage == null) {
                    terminate(socket, bufferedReader, bufferedWriter);
                    break;
                }
                handlePacket(nodeMessage);
//                broadcastMessage(nodeMessage);
            } catch (IOException e) {
                this.terminate(socket, bufferedReader, bufferedWriter);
                break;
            } catch (Exception e) {
                // TODO : maybe kicking the node if too many invalid packets are sent by it ?
            }
        }
    }


    private void handlePacket(String nodeMessage) throws Exception {
        Packet receivedPacket = (Packet) JsonHandler.decode(nodeMessage, Packet.class);
        Log.d("peeringServerPacketReception", nodeMessage);
        if (receivedPacket == null) return;
        if (getOnPacketReceptionHandler()!=null) getOnPacketReceptionHandler().onPacketReceptionEvent(receivedPacket);
        switch (receivedPacket.getType()) {
            case PEERING_SERVER_RUNNING : answerPeeringServerRunning(receivedPacket);
            case GREETING : answerGreeting(receivedPacket);
            case PEERING_SUCCESS : answerPeeringSuccess(receivedPacket);
            case GOODBYE : answerGoodBye(receivedPacket);
            case NEW_MATCH : {
                // TODO
            }
            case NEW_BLOCK : {
                // TODO
            }
            case WHO_ARE_YOU : {
                // TODO
            }
            case IS_HE : {
                // TODO
            }
            case BLOB : broadcastPacket(receivedPacket);
            case IS_USER : answerIsUser(receivedPacket);
            case SIGN_THIS : answerSignThis(receivedPacket);
            default : throw new Exception();
        }
    }

    public void mutlticastPacket(Packet packet, Group group) {
        // TODO
        for (NodeHandler node : nodes) {
            try {
                if (!node.getPublicKey().equals(this.publicKey) && node.getGroup() == group) {
                    node.bufferedWriter.write(JsonHandler.encode(packet) + "\n");
                    node.bufferedWriter.flush();
                }
            } catch (IOException e) {
                terminate(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void sendPacketToSingleNode(Packet packet) {
        String receiverPublicKey = packet.getReceiverPublicKey();
        for (NodeHandler node : nodes) {
            try {
                if (node.getPublicKey().equals(receiverPublicKey)) {
                    node.bufferedWriter.write(JsonHandler.encode(packet) + "\n");
                    node.bufferedWriter.flush();
                }
            } catch (IOException e) {
                terminate(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void broadcastPacket(Packet packet) {
        for (NodeHandler node : nodes) {
            try {
                if (!node.getPublicKey().equals(this.publicKey)) {
                    // can intercept messages here therefore the server might tamper with them
                    node.bufferedWriter.write(JsonHandler.encode(packet) + "\n");
                    node.bufferedWriter.flush();
                }
            } catch (IOException e) {
                terminate(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void multicastJson(Packet packet, Group group) {

    }

    @Deprecated
    public void broadcastMessage(String message) {
        // useful for testing but won't be used in implementation
        for (NodeHandler node : nodes) {
            try {
                if (!node.publicKey.equals(this.publicKey)) {
                    // can intercept messages here therefore the server might tamper them

                    Packet packet = new Packet(DataType.BLOB, this, message);
                    node.bufferedWriter.write(JsonHandler.encode(packet) + "\n");
                    node.bufferedWriter.flush();
                }
            } catch (IOException e) {
                terminate(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    protected void removeNodeHandler() {
        nodes.remove(this);
        broadcastMessage(String.format("the publicKey %s is now offline", this.getPublicKey()));
    }


    private void terminate(Socket socket, BufferedReader bufferedReader, BufferedWriter printWriter) {
        removeNodeHandler();
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

    public Socket getSocket() {
        return socket;
    }

    public String getPublicKey() {
        return publicKey;
    }

    protected void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public static OnPacketReceptionHandler getOnPacketReceptionHandler() {
        return onPacketReceptionHandler;
    }

    public static void setOnPacketReceptionHandler(OnPacketReceptionHandler onPacketReceptionHandler) {
        NodeHandler.onPacketReceptionHandler = onPacketReceptionHandler;
    }
}
