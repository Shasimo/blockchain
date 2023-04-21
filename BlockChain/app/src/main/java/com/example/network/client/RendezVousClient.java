package com.example.network.client;

import com.example.block.Transaction;
//import com.example.network.server.RendezVousServer;
import com.example.network.util.OnPacketReceptionHandler;
import com.example.network.data.DataType;
import com.example.network.data.Packet;
import com.example.network.server.PeeringServer;
import com.example.network.util.Address;
import com.example.security.RSA;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;

public class RendezVousClient extends Client {

    private ArrayList<PeeringClient> peeringClients = new ArrayList<>();
    private PeeringServer peeringServer;

    public RendezVousClient(Socket socket, OnPacketReceptionHandler onPacketReceptionHandler) {
        super(socket, onPacketReceptionHandler);
    }

    @Override
    public void answerAlive(Packet packet) {
        try {
            greetNewNode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void answerAdvertisement(Packet packet) {

    }

    @Override
    public void answerPeeringRequest(Packet packet) {
        Address address = packet.getPeerAddress();

        try {
            Socket socket = new Socket(address.getIp(), address.getPort());
            // the split is necessary when runnning multiple instances on the same device TODO fix this for later
            PeeringClient peeringClient = new PeeringClient(socket, getPublicKeyRepr(), getOnPacketReceptionHandler());


            peeringClients.add(peeringClient);
            Thread thread = new Thread(peeringClient);
            thread.start();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // IOException when initializing the peeringClient
            e.printStackTrace();
        }

        try {
            sendPacket(new Packet(DataType.PEERING_SUCCESS, this, "peering success"));
        } catch (IOException e) {
            // IOException when sending msg to rendezVousServer
            throw new RuntimeException(e);
        }
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void answerStartPeeringServer(Packet packet) {
        try {
            int port = new Random().nextInt(5000) + 1000; // TODO assign a fixed port to avoid conflict when testing
            ServerSocket serverSocket = new ServerSocket(port);
            peeringServer = new PeeringServer(serverSocket, getOnPacketReceptionHandler());

            Thread thread = new Thread(peeringServer);
            thread.start();   // run runs the thread
            Address address = new Address(getLocalIpAddress(), port);
            Packet answerPacket = new Packet(DataType.PEERING_SERVER_RUNNING, this, address);
            sendPacket(answerPacket);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void answerNewBlock(Packet packet) {

    }

    @Override
    public void answerSignThis(Packet packet) {
        // TODO sign and send
        // signing shouldn't occur here but in the app
//        RSA rsa = new RSA(getPublicKey(), getPrivateKey());
//
//        if (packet.getTransaction().getPlayer1().getPublicKeyString().equals(this.getPublicKeyRepr()))
//            packet.getTransaction().setPlayer1Signature(rsa.EncryptUsingPrivate(Long.toString(packet.getTransaction().getTimeStamp())));
//        else
//            packet.getTransaction().setPlayer2Signature(rsa.EncryptUsingPrivate(Long.toString(packet.getTransaction().getTimeStamp())));
//
//        try {
//            sendPacket(packet);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void sendTransactionToSign(Transaction transaction) {
        // TODO change the type when done
        Packet packet = new Packet(DataType.SIGN_THIS, this, "sign this transaction");
        packet.setTransaction(transaction);
        try {
            sendPacket(packet);
        } catch (IOException e) {
            // problem when sending the packet
        }
    }

    public void askForChain() throws IOException {
        Packet packet = new Packet(DataType.ASK_FOR_CHAIN, this, "send me the whole chain");
        floodFill(packet);
    }

    private void floodFill(Packet packet) throws IOException {
        for (PeeringClient client : peeringClients)
            if (!client.getPublicKeyRepr().equals(this.getPublicKeyRepr()))
                client.sendPacket(packet);
        if (peeringServer != null)
            peeringServer.getFirstPeeringNodeHandler().broadcastPacket(packet);
    }

    public void sendChain(ArrayList<Transaction> transactions) throws IOException {
        Packet packet = new Packet(DataType.LIST_OF_TRANSACTIONS, this, "list of transactions");
        packet.setListOfTransactions(transactions);
        floodFill(packet);
    }

    private PeeringServer getPeeringServer() {
        return peeringServer;
    }

}

