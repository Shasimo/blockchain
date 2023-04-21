package org.blockchainElo.data;

import org.blockchainElo.server.node_handler.NodeHandler;
import org.blockchainElo.util.Address;
import org.blockchainElo.util.Pair;
import org.blockchainElo.util.Transaction;
import org.blockchainElo.util.User;

import java.io.Serializable;

public class Packet implements Serializable {
    private DataType type;
    private User sender;
    private Group senderGroup;

    @Deprecated
    private String senderPublicKey;

    private String receiverPublicKey;
    private String content;

    private Address senderAddress;
    private Address receiverAddress;
    private Address peerAddress;

    private final long timestamp;
    private Transaction transaction;


    public Packet(DataType type, NodeHandler nodeHandler, String content) {
        this.timestamp = System.currentTimeMillis();
        setType(type);
        setSenderPublicKey(nodeHandler.getPublicKey());
        setContent(content);
    }

    public Packet(DataType type) {
        this.timestamp = System.currentTimeMillis();
        this.type = type;
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
}
