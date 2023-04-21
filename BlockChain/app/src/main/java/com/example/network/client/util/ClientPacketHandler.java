package com.example.network.client.util;

import com.example.network.data.Packet;

public interface ClientPacketHandler {
    void answerAlive(Packet packet);

    void answerAdvertisement(Packet packet);

    void answerPeeringRequest(Packet packet);

    void answerStartPeeringServer(Packet packet);

    void answerNewBlock(Packet packet);

    void answerSignThis(Packet packet);
}
