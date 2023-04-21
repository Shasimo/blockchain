package org.blockchainElo.server.node_handler;

import org.blockchainElo.data.*;
import org.blockchainElo.server.util.ServerPacketHandler;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


public abstract class NodeHandler implements Runnable, ServerPacketHandler {
    public static ArrayList<NodeHandler> nodes = new ArrayList<>();
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String publicKey;
    private Group group;
    protected static HashMap<Group, ArrayList<NodeHandler>> groupNodes = new HashMap<>();

    public NodeHandler() {
        // phantom node
        setGroup(Group.SUPER_NODE);
        setPublicKey("0");
    }
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
                terminate(socket, bufferedReader, bufferedWriter);
                break;
            } catch (Exception e) {
                // TODO : maybe kicking the node if too many invalid packets are sent by it ?
            }
        }
    }


    private void handlePacket(String nodeMessage) throws Exception {
        Packet receivedPacket = (Packet) JsonHandler.decode(nodeMessage, Packet.class);
        System.out.println(nodeMessage);
        if (receivedPacket == null) return;
        switch (receivedPacket.getType()) {
            case PEERING_SERVER_RUNNING -> answerPeeringServerRunning(receivedPacket);
            case GREETING -> answerGreeting(receivedPacket);
            case PEERING_SUCCESS -> answerPeeringSuccess(receivedPacket);
            case GOODBYE -> answerGoodBye(receivedPacket);
            case NEW_MATCH -> {
                // TODO
            }
            case NEW_BLOCK -> {
                // TODO
            }
            case WHO_ARE_YOU -> {
                // TODO
            }
            case IS_HE -> {
                // TODO
            }
            case BLOB, STAYING_LIVE -> broadcastPacket(receivedPacket);
            case IS_USER -> answerIsUser(receivedPacket);
            case SIGN_THIS -> answerSignThis(receivedPacket);
            default -> throw new Exception();
        }
    }

    public void unicastPacket(Packet packet) {
        String receiverPublicKey = packet.getReceiverPublicKey();
        for (NodeHandler node : nodes) {
            try {
                if (node.getPublicKey().equals(receiverPublicKey)) {
                    node.writeToBufferedWriter(JsonHandler.encode(packet));
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
//                    System.out.println("----sending the packet---" + packet.getType().toString());
                    node.writeToBufferedWriter(JsonHandler.encode(packet));

                }
            } catch (IOException e) {
                terminate(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void multicastPacket(Packet packet, Group group) {
        for (NodeHandler node : groupNodes.get(group)) {
            try {
                if (!node.getPublicKey().equals(getPublicKey()) && node.getGroup() == group){
                    node.writeToBufferedWriter(JsonHandler.encode(packet));
                }
            } catch (IOException e) {
                terminate(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void multicastPacket(Packet packet, ArrayList<String> receiversPublicKeys) {
        for (NodeHandler node : nodes) {
            try {
                if (receiversPublicKeys.contains(node.getPublicKey())){
                    node.writeToBufferedWriter(JsonHandler.encode(packet));
                }
            } catch (IOException e) {
                terminate(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    @Deprecated
    public void broadcastMessage(String message) {
        // useful for testing but won't be used in implementation
        for (NodeHandler node : nodes) {
            try {
                if (!node.publicKey.equals(this.publicKey)) {
                    // can intercept messages here therefore the server might tamper them

                    Packet packet = new Packet(DataType.BLOB, this, message);
                    node.writeToBufferedWriter(JsonHandler.encode(packet));
                }
            } catch (IOException e) {
                terminate(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    protected void removeNodeHandler() {
        nodes.remove(this);
        // todo : the user should send a goodbye packet before going offline making the following line useless
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

    private void writeToBufferedWriter(String message) throws IOException {
        this.bufferedWriter.write(message + "\n");
        this.bufferedWriter.flush();
    }

}
