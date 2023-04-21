package org.blockchainElo.server.node_handler.user_map;

import org.blockchainElo.data.Group;
import org.blockchainElo.server.util.custom_exceptions.NetworkIsFull;
import org.blockchainElo.server.util.custom_exceptions.YouAreAlone;
import org.blockchainElo.util.Address;
import org.blockchainElo.util.Pair;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;

public class UserMap {
    private static HashMap<String, ArrayList<String>> knownNetwork = new HashMap<>();
    private static HashMap<String, Address> peeringServersAddresses = new HashMap<>();
    private static LinkedList<String> fillingOrder = new LinkedList<>();
    private static final int maxNumberOfPeers = 5;

    public ArrayList<String> getNodeNeighbours(String publicKey) throws NoSuchElementException {
        ArrayList<String> listOfNeighbours = knownNetwork.get(publicKey);
        if (listOfNeighbours != null) {
            return listOfNeighbours;
        }
        throw new NoSuchElementException();
    }

    public boolean isServer(String publicKey) {
        return peeringServersAddresses.get(publicKey) != null;
    }

    public Address getAddress(String publicKey) throws NoSuchElementException {
        if (isServer(publicKey)) {
            return peeringServersAddresses.get(publicKey);
        }
        throw new NoSuchElementException();
    }

    public Set<String> getListOfServers() {
        return peeringServersAddresses.keySet();
    }

    private boolean isFull(String publicKey) {
        return knownNetwork.get(publicKey).size() == maxNumberOfPeers;
    }

    public void newPeeringServer(String publicKey, Address address) throws InstanceAlreadyExistsException {
        if (isServer(publicKey)) throw new InstanceAlreadyExistsException();
        peeringServersAddresses.put(publicKey, address);
        knownNetwork.put(publicKey, new ArrayList<>());
        fillingOrder.add(publicKey);
    }

    private void addPeerToServer(String serverPublicKey, String peerPublicKey) {
        ArrayList<String> peers = knownNetwork.get(serverPublicKey);
        peers.add(peerPublicKey);
        knownNetwork.replace(serverPublicKey, peers);
    }

    public Address addToNetwork(String publicKey) throws NetworkIsFull, YouAreAlone {
        LinkedList<String> orderWithoutSelf = (LinkedList<String>) fillingOrder.clone();
        if (orderWithoutSelf.remove(publicKey) && orderWithoutSelf.isEmpty())
            throw new YouAreAlone("No other node on the network");
        String lastIn = orderWithoutSelf.getLast();
        while (isFull(lastIn) &&
                (getNodeNeighbours(lastIn).contains(publicKey) || getNodeNeighbours(publicKey).contains(lastIn))) {
            orderWithoutSelf.removeLast();
            lastIn = orderWithoutSelf.getLast();
        }
        if (lastIn == null) throw new NetworkIsFull("There is no open server to join");
        addPeerToServer(lastIn, publicKey);
        return getAddress(lastIn);  // returns the pk of the server the new node has to connect to
    }

    public ArrayList<String> removeNode(String publicKey) {
        if (!isServer(publicKey)) throw new NoSuchElementException();
        peeringServersAddresses.remove(publicKey);
        ArrayList<String> nodesToNotify = getNodeNeighbours(publicKey);
        knownNetwork.remove(publicKey);
        fillingOrder.remove(publicKey);
        return nodesToNotify;
    }

}
