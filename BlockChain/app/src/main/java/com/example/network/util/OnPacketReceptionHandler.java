package com.example.network.util;

import com.example.network.data.Packet;

public interface OnPacketReceptionHandler {
    // override this class with action to perform
    public void onPacketReceptionEvent(Packet receivedPacket);
}
