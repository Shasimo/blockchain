package org.blockchainElo.data;

public enum DataType {
    // supernode interactions
    ALIVE,  // broadcast to all peers that a node is now available
    STAYING_LIVE,
    ADVERTISEMENT,  // changing a node's type
    PEERING_REQUEST,    // when a certain nodes want to connect to your peeringServer
    START_PEERING_SERVER,   // one of the peers has to start the server
    PEERING_SERVER_RUNNING, // when a node starts a p2p network
    PEERING_SUCCESS,
    // normal node possible interactions
    GREETING,   // when connecting to a server
    GOODBYE,    // when logging off
    NEW_MATCH,
    NEW_BLOCK,
    WHO_ARE_YOU,    // checking the node type
    IS_HE,   // asking about a node identity
    BLOB,
    IS_USER,
    CHECK_MATCH,
    SIGN_THIS,
    ASK_FOR_CHAIN

}
