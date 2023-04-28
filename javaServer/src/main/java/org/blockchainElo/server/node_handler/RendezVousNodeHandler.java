package org.blockchainElo.server.node_handler;

import org.blockchainElo.data.DataType;
import org.blockchainElo.data.Group;
import org.blockchainElo.data.Packet;
import org.blockchainElo.server.node_handler.user_map.UserMap;
import org.blockchainElo.server.util.custom_exceptions.NetworkIsFull;
import org.blockchainElo.server.util.custom_exceptions.YouAreAlone;
import org.blockchainElo.util.Address;
import org.blockchainElo.util.Const;

import java.net.Socket;
import java.time.Instant;
import java.util.*;

public class RendezVousNodeHandler extends NodeHandler {
    private UserMap userMap = new UserMap();
    private static Timer timer;

    public RendezVousNodeHandler(Socket socket) {
        super(socket, Group.SUPER_NODE);
    }

    public RendezVousNodeHandler() {
        // phantom node for management purposes
        super();
//        authoritySwap();
    }

    private void authoritySwap() {
        if (timer != null) return;
        timer = new Timer("authority swap timer");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                chooseNewAuthorities();
                timer = null;
                authoritySwap();
            }
        };
        timer.schedule(timerTask, Const.AUTHORITY_SWAP_DELAY);
    }

    @Override
    public void answerGreeting(Packet packet) {
        String publicKey = packet.getSenderPublicKey();
        setPublicKey(publicKey);
        Packet answerPacket = new Packet(DataType.START_PEERING_SERVER);
        answerPacket.setReceiverPublicKey(publicKey);
        unicastPacket(answerPacket);
        Packet broadcastPacket = packet;
        broadcastPacket.setType(DataType.ALIVE);
        broadcastPacket(broadcastPacket);
    }

    @Override
    public void answerGoodBye(Packet packet) {
        // TODO : log the goodbye
        broadcastPacket(new Packet(DataType.BLOB, this, "user left"));
        removeNodeHandler();
    }

    @Override
    public void answerBlob(Packet packet) {

    }

    @Override
    public void answerPeeringServerRunning(Packet packet) {
        Address address = packet.getSenderAddress();
        try {
            userMap.newPeeringServer(packet.getSenderPublicKey(), address);
        } catch (Exception e) {
            throw new RuntimeException(e);
            // TODO : send an error to the node
        }

        Address peerAddress;
        try {
            peerAddress = userMap.addToNetwork(packet.getSenderPublicKey());
        } catch (NetworkIsFull e) {
            // should not happen
            // TODO : maybe make it so not every node starts a server ?
            throw new RuntimeException(e);
        } catch (YouAreAlone e) {
            // TODO : :( make the node a friend
            System.out.println(e.toString());
            return;
        }
        Packet answerPacket = new Packet(DataType.PEERING_REQUEST);
        answerPacket.setPeerAddress(peerAddress);
        answerPacket.setReceiverPublicKey(packet.getSenderPublicKey());

        unicastPacket(answerPacket);

    }

    @Override
    public void answerPeeringSuccess(Packet packet) {
    }

    @Override
    public void answerIsUser(Packet packet) {
        String userToCheck = packet.getContent();
        // verify in db if user exists
    }

    @Override
    public void answerSignThis(Packet packet) {
//        ArrayList<String> publicKeyList = packet.getTransaction().getPendingSignaturePlayerList();
//        System.out.println(publicKeyList);
//        if (publicKeyList.isEmpty()) broadcastPacket(packet);
//        this.multicastPacket(packet,publicKeyList);
        broadcastPacket(packet);
    }

    @Override
    protected void removeNodeHandler() {
        userMap.removeNode(this.getPublicKey());
        super.removeNodeHandler();
    }

    public void changeNodeGroup(String publicKey, Group newGroup) {
        Packet packet = new Packet(DataType.ADVERTISEMENT, this, String.format("The node %s is now a %s", publicKey, newGroup.toString()));
        broadcastPacket(packet);
    }

    private void chooseNewAuthorities() {
        ArrayList<String> newAuthorities = new ArrayList<>();
        ArrayList<String> onlineNodes = new ArrayList<>(userMap.getListOfServers());
        if (onlineNodes.isEmpty()) return;
        Random random = new Random();
        for (int i = 0; i < Const.NUMBER_OF_AUTHORITIES; i++) {
            String newAuthority = onlineNodes.get(random.nextInt(onlineNodes.size()));
            newAuthorities.add(newAuthority);

            changeNodeGroup(newAuthority, Group.AUTHORITY);
        }
        Packet packet = new Packet(DataType.ADVERTISEMENT);
        packet.setContent(String.format("you are now a %s", Group.AUTHORITY.toString()));
        multicastPacket(packet, newAuthorities);
        System.out.println("authorities have been swapped " + Instant.ofEpochMilli(Instant.now().toEpochMilli()));
    }

}
