package org.blockchainElo.server.util;

import org.blockchainElo.data.Packet;

public interface ServerPacketHandler {
    void answerGreeting(Packet packet);

    void answerGoodBye(Packet packet);

    void answerBlob(Packet packet);

    void answerPeeringServerRunning(Packet packet);

    void answerPeeringSuccess(Packet packet);

    void answerIsUser(Packet packet);

    void answerSignThis(Packet packet);

}
