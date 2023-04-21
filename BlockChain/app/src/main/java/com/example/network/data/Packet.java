package com.example.network.data;

import static com.example.network.data.Group.NODE;
import static com.example.network.data.Group.PEER;

import android.util.Pair;

import com.example.block.Transaction;
import com.example.blockchain.User;
import com.example.network.client.PeeringClient;
//import com.example.network.server.node_handler.RendezVousNodeHandler;
import com.google.gson.Gson;
import com.example.network.client.Client;
import com.example.network.server.node_handler.NodeHandler;
import com.example.network.util.Address;

import java.io.Serializable;
import java.util.ArrayList;

public class Packet implements Serializable {
    private DataType type;
    private Group senderGroup;
    private User sender;

    @Deprecated
    private String senderPublicKey;
    private String receiverPublicKey;
    private String content;

    private Address senderAddress;
    private Address receiverAddress;
    private Address peerAddress;

    private final long timestamp;
    private Transaction transaction;


    // for peering only
    private ArrayList<Transaction> listOfTransactions;

    public Packet(DataType type, NodeHandler nodeHandler, String content) {
        this.timestamp = System.currentTimeMillis();
        setType(type);
        setSenderPublicKey(nodeHandler.getPublicKey());
        setContent(content);
        this.senderGroup = PEER;
        //        this.senderGroup = (nodeHandler instanceof RendezVousNodeHandler) ? NODE : Group.PEER;
    }

    public Packet(DataType type, Client client, String content) {
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.senderPublicKey = client.getPublicKeyRepr();
        this.content = content;
        this.senderGroup = Group.NODE;
    }

    public Packet(DataType type, PeeringClient peeringClient, String content) {
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.senderPublicKey = peeringClient.getPublicKeyRepr();
        this.content = content;
        this.senderGroup = Group.PEER;
    }

    public Packet(DataType type, Client client, Address senderAddress) {
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.senderPublicKey = client.getPublicKeyRepr();
        this.senderAddress = senderAddress;
        this.senderGroup = Group.NODE;
    }

    public Packet(DataType type, PeeringClient peeringClient, Address senderAddress) {
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.senderPublicKey = peeringClient.getPublicKeyRepr();
        this.content = content;
        this.senderGroup = Group.PEER;
    }

    public Packet(DataType type) {
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.senderGroup = Group.NODE;
    }

    public String getReceiverPublicKey() {
        return receiverPublicKey;
    }

    public void setReceiverPublicKey(String receiverPublicKey) {
        this.receiverPublicKey = receiverPublicKey;
    }

    public Address getPeerAddress() {
        return peerAddress;
    }

    public void setPeerAddress(Address peerAddress) {
        this.peerAddress = peerAddress;
    }

    public Group getSenderGroup() {
        return senderGroup;
    }

    public void setSenderGroup(Group senderGroup) {
        this.senderGroup = senderGroup;
    }



    public Address getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(Address senderAddress) {
        this.senderAddress = senderAddress;
    }

    public Address getreceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(Address receiverAddress) {
        this.receiverAddress = receiverAddress;
    }


    public void setType(DataType type) {
        this.type = type;
    }

    public DataType getType() {
        return type;
    }

    public String getSenderPublicKey() {
        return senderPublicKey;
    }

    public void setSenderPublicKey(String senderPublicKey) {
        this.senderPublicKey = senderPublicKey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Pair<String, String> getSenderPublicKeyPair() {
        String[] pair = getSenderPublicKey().split(";");
        return new Pair<String,String>(pair[0], pair[1]);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Address getReceiverAddress() {
        return receiverAddress;
    }

    public ArrayList<Transaction> getListOfTransactions() {
        return listOfTransactions;
    }

    public void setListOfTransactions(ArrayList<Transaction> listOfTransactions) {
        this.listOfTransactions = listOfTransactions;
    }
}
